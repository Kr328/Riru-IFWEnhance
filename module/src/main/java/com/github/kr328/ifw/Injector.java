package com.github.kr328.ifw;

import android.app.IActivityManager;
import android.content.pm.IPackageManager;
import android.os.Binder;
import android.os.IBinder;
import android.os.Process;
import android.util.Log;

@SuppressWarnings({"unused", "RedundantSuppression"})
public class Injector extends ServiceProxy {
    public static final String TAG = "IFWEnhance";

    private IActivityManager activityManager = null;

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
        if (!name.equals("activity"))
            return service;

        try {
            activityManager = (IActivityManager) ObjectResolver
                    .resolve(service, "com.android.server.am.ActivityManagerService", 30);
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

                        Binder proxy = ProxyFactory.instance(service,
                                new PackageProxy(IPackageManager.Stub.asInterface(service), firewall));

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
