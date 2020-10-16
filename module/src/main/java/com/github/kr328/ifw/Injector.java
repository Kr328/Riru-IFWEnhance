package com.github.kr328.ifw;

import android.app.IActivityManager;
import android.content.pm.IPackageManager;
import android.os.IBinder;
import android.os.Process;
import android.os.ServiceManager;
import android.util.Log;

@SuppressWarnings({"unused", "RedundantSuppression"})
public class Injector extends ServiceProxy {
    public static final String TAG = "IFWEnhance";
    public static final String KEY_ACTIVITY_MANAGER_SERVICE = "original-activity-manager-service";

    private IActivityManager activityManager = null;

    private static native Object getGlobalObject(String key);

    private static native void putGlobalObject(String key, Object value);

    public static void inject(String argument) {
        Log.i(TAG, String.format("Uid = %d Pid = %d", Process.myUid(), Process.myPid()));

        Injector injector = new Injector();

        try {
            injector.install();

            Log.i(TAG, "Inject successfully");
        } catch (Exception e) {
            Log.e(TAG, "Inject failure", e);
        }
    }

    @Override
    protected IBinder onAddService(String name, IBinder service) {
        try {
            if ("activity".equals(name)) {
                synchronized (ServiceManager.class) {
                    IBinder binder = (IBinder) getGlobalObject(KEY_ACTIVITY_MANAGER_SERVICE);

                    if (binder == null) {
                        binder = service;

                        putGlobalObject(KEY_ACTIVITY_MANAGER_SERVICE, binder);
                    }

                    activityManager = IActivityManager.Stub.asInterface(binder);
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Query original AMS failure", e);
        }

        return service;
    }

    @Override
    protected IBinder onGetService(String name, IBinder service) {
        for (StackTraceElement stack : Thread.currentThread().getStackTrace()) {
            if ("getCommonServicesLocked".equals(stack.getMethodName())) {
                if ("package".equals(name) && activityManager != null) {
                    Log.i(TAG, "Package Manager found");

                    try {
                        IntentFirewall firewall = IntentFirewall.fromActivityManager(activityManager);

                        PackageProxy proxy =  new PackageProxy(
                                service,
                                IPackageManager.Stub.asInterface(service),
                                firewall
                        );

                        Log.i(TAG, "Package Manager replaced");

                        return proxy;
                    } catch (Exception e) {
                        Log.e(TAG, "Proxy Package Manager failure", e);
                    }
                }

                return service;
            }
        }

        return service;
    }
}
