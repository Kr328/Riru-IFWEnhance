package com.github.kr328.ifw;

import android.content.pm.IPackageManager;
import android.os.Binder;
import android.os.IBinder;
import android.os.Process;
import android.util.Log;

import com.github.kr328.magic.proxy.AIDLProxy;
import com.github.kr328.magic.proxy.ServiceManagerProxy;
import com.github.kr328.magic.util.ClassLoaderUtils;
import com.github.kr328.magic.util.StackUtils;
import com.github.kr328.zloader.ZygoteLoader;

import java.util.Objects;
import java.util.Properties;

public class Injector {
    public static final String TAG = "IFWEnhance";

    private static boolean initialized;
    private static Class<?> cPackageProxy;

    @SuppressWarnings("unused")
    public static void main(String processName, Properties properties) {
        Log.i(TAG, "ProcessName = " + processName);
        Log.i(TAG, "Uid = " + Process.myUid());
        Log.i(TAG, "Pid = " + Process.myPid());

        if (!ZygoteLoader.PACKAGE_SYSTEM_SERVER.equals(processName)) {
            return;
        }

        try {
            new ServiceManagerProxy.Builder()
                    .setAddServiceFilter(Injector::findClassLoader)
                    .setGetServiceFilter(Injector::replacePackage)
                    .build()
                    .install();

            Log.i(TAG, "Inject successfully");
        } catch (Exception e) {
            Log.e(TAG, "Inject: " + e, e);
        }
    }

    private static Binder findClassLoader(String name, Binder service) {
        if (!initialized && service.getClass().getName().startsWith("com.android.server")) {
            initialized = true;

            try {
                ClassLoaderUtils.replaceParentClassLoader(
                        Injector.class.getClassLoader(),
                        service.getClass().getClassLoader()
                );

                Log.d(TAG, "Parent ClassLoader injected");
            } catch (Throwable e) {
                Log.e(TAG, "Inject parent ClassLoader: " + e, e);
            }

            try {
                cPackageProxy = Objects.requireNonNull(Injector.class.getClassLoader())
                        .loadClass("com.github.kr328.ifw.PackageProxy");
            } catch (Exception e) {
                Log.e(TAG, "Load PackageProxy: " + e);
            }
        }

        return service;
    }

    private static IBinder replacePackage(String name, IBinder original) {
        if (!"package".equals(name)) {
            return original;
        }

        if (cPackageProxy != null && StackUtils.isStacktraceContains("getCommonServicesLocked")) {
            try {
                final IPackageManager fallback = IPackageManager.Stub.asInterface(original);
                final IPackageManager delegated = (IPackageManager) cPackageProxy
                        .getConstructor(IPackageManager.class)
                        .newInstance(fallback);
                final Binder result = AIDLProxy.newServer(IPackageManager.class, fallback, delegated);

                Log.i(TAG, "PackageProxy instanced");

                return result;
            } catch (Throwable e) {
                Log.e(TAG, "Proxy package: " + e, e);
            }
        }

        return original;
    }
}
