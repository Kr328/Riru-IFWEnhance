package com.github.kr328.ifw;

import android.content.pm.IPackageManager;
import android.os.Binder;
import android.os.Process;
import android.util.Log;

import com.github.kr328.magic.services.ServiceManagerProxy;
import com.github.kr328.zloader.BinderInterceptors;
import com.github.kr328.zloader.ZygoteLoader;

public class Main {
    public static final String TAG = "IFWEnhance";

    @SuppressWarnings("unused")
    public static void main() {
        Log.i(TAG, "ProcessName = " + ZygoteLoader.getPackageName());
        Log.i(TAG, "Uid = " + Process.myUid());
        Log.i(TAG, "Pid = " + Process.myPid());

        if (!ZygoteLoader.PACKAGE_SYSTEM_SERVER.equals(ZygoteLoader.getPackageName())) {
            return;
        }

        try {
            ServiceManagerProxy.install(new ServiceManagerProxy.Interceptor() {
                @Override
                public Binder addService(final String name, final Binder service) {
                    if ("package".equals(name)) {
                        Log.d(TAG, "PackageManagerService found");

                        try {
                            BinderInterceptors.install(service, next -> {
                                final IPackageManager original = IPackageManager.Stub.asInterface(next);
                                return Proxy.FACTORY.create(original, new Proxy(original));
                            });

                            Log.d(TAG, "Binder interceptor installed");
                        } catch (Exception e) {
                            Log.e(TAG, "Initialize binder interceptor failed", e);
                        }
                    }

                    return super.addService(name, service);
                }
            });

            Log.i(TAG, "Inject successfully");
        } catch (final Exception e) {
            Log.e(TAG, "Inject: " + e, e);
        }
    }
}
