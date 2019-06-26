package com.github.kr328.ifw;

import android.content.Intent;
import android.content.pm.IPackageManager;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.os.IServiceManager;
import android.os.ServiceManager;
import android.util.Log;
import com.github.kr328.ifw.proxy.ProxyBinder;
import com.github.kr328.ifw.proxy.ServiceManagerProxy;
import com.github.kr328.ifw.proxy.StackUtils;
import com.github.kr328.ifw.proxy.TransactCodeExporter;

@SuppressWarnings("unused")
public class Injector {
    private static boolean injected = false;
    private static Binder originalPackageManager;
    private static Binder activityManager;
    private static PackageManagerProxy packageManagerProxy;

    public static void inject(String placeholder) {
        Log.i(Constants.TAG, "In system_server pid = " + android.os.Process.myPid());

        try {
            if ( !injected ) {
                injected = true;
                replaceServiceManager();
            }
        } catch (Exception e) {
            Log.e(Constants.TAG, "Inject failure", e);
        }
    }

    private static void replaceServiceManager() throws Exception {
        IServiceManager serviceManager = ServiceManagerProxy.getOriginalIServiceManager();
        ServiceManagerProxy serviceManagerProxy = new ServiceManagerProxy(serviceManager, new ServiceManagerProxy.Callback() {
            @Override
            public IBinder addService(String name, IBinder original) {
                switch (name) {
                    case "package":
                        originalPackageManager = (Binder) original;
                        packageManagerProxy = new PackageManagerProxy(IPackageManager.Stub.asInterface(originalPackageManager));

                        packageManagerProxy.putActivityManager(activityManager);

                        return createProxyBinder(packageManagerProxy);
                    case "activity":
                        activityManager = (Binder) original;

                        if ( packageManagerProxy != null )
                            packageManagerProxy.putActivityManager(activityManager);

                        break;
                }

                return original;
            }

            @Override
            public IBinder getService(String name, IBinder original) {
                if ( "package".equals(name) ) {
                    if ( originalPackageManager == null || StackUtils.hasMethodOnStack(Thread.currentThread(), "getCommonServicesLocked") )
                        return original;
                    return original == null ? null : originalPackageManager;
                }

                return original;
            }

            @Override
            public IBinder checkService(String name, IBinder original) {
                if ( "package".equals(name) ) {
                    if ( originalPackageManager == null || StackUtils.hasMethodOnStack(Thread.currentThread(), "getCommonServicesLocked") )
                        return original;
                    return original == null ? null : originalPackageManager;
                }

                return original;
            }
        });
        ServiceManagerProxy.setDefaultServiceManager(serviceManagerProxy);

        Log.i(Constants.TAG, "Inject successfully");
    }

    private static Binder createProxyBinder(PackageManagerProxy packageManagerProxy) {
        int queryIntentCode;

        try {
            queryIntentCode = new TransactCodeExporter(IPackageManager.Stub.class)
                    .export(IPackageManager.Stub.class.getMethod("queryIntentActivities", Intent.class, String.class, int.class, int.class));
        } catch (ReflectiveOperationException e) {
            Log.e(Constants.TAG, "Find transact code failure", e);
            return originalPackageManager;
        }

        Log.i(Constants.TAG, "queryIntentActivities transact code " + queryIntentCode);

        return new ProxyBinder(originalPackageManager, ((original, code, data, reply, flags) -> {
            if ( code == queryIntentCode ) {
                packageManagerProxy.transact(code, data, reply, flags);
                return true;
            }

            return false;
        }));
    }

    public static void main(String[] args) throws Exception {
        IPackageManager packageManager = IPackageManager.Stub.asInterface(ServiceManager.getService("package"));
        Intent intent = new Intent(Intent.ACTION_VIEW).setData(Uri.parse("https://www.google.com"));

        System.out.println(packageManager.queryIntentActivities(intent, null, 0, 0));
    }
}
