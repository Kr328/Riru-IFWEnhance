package com.github.kr328.ifw;

import android.app.IActivityManager;
import android.content.pm.IPackageManager;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import com.github.kr328.ifw.proxy.OriginalExporter;
import com.github.kr328.ifw.proxy.ProxyBinderFactory;
import com.github.kr328.ifw.proxy.ServiceManagerProxy;
import com.github.kr328.ifw.proxy.StackUtils;

@SuppressWarnings("unused")
public class Injector {
    private static Binder packageManager;
    private static Binder activityManager;

    public static void inject(String placeholder) {
        Log.i(Constants.TAG, "In system_server pid = " + android.os.Process.myPid());

        try {
            ServiceManagerProxy.install(new ServiceManagerProxy.Callback() {
                @Override
                public IBinder addService(String name, IBinder original) {
                    switch (name) {
                        case "package":
                            packageManager = (Binder) original;
                            break;
                        case "activity":
                            activityManager = (Binder) original;
                            break;
                    }

                    return original;
                }

                @Override
                public IBinder getService(String name, IBinder original) {
                    if ( "package".equals(name) ) {
                        if ( StackUtils.hasMethodOnStack(Thread.currentThread(), "getCommonServicesLocked") ) {
                            try {
                                return ProxyBinderFactory.createProxyBinder(original instanceof Binder ? (Binder)original : packageManager,
                                        new PackageManagerProxy(IPackageManager.Stub.asInterface(original),
                                                IActivityManager.Stub.asInterface(OriginalExporter.exportBinder(activityManager))));
                            } catch (ReflectiveOperationException e) {
                                Log.w(Constants.TAG, "Proxy PackageManager failure", e);
                            }
                        }
                    }

                    return original;
                }

                @Override
                public IBinder checkService(String name, IBinder original) {
                    return original;
                }
            });

            Log.i(Constants.TAG, "Inject successfully");
        } catch (Exception e) {
            Log.e(Constants.TAG, "Inject failure", e);
        }
    }
}
