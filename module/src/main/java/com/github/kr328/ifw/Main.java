package com.github.kr328.ifw;

import android.content.pm.IPackageManager;
import android.os.IBinder;
import android.os.Process;
import android.util.Log;

import com.github.kr328.magic.services.ServiceManagerProxy;
import com.github.kr328.magic.util.StackUtils;
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
            new ServiceManagerProxy.Builder()
                    .setGetServiceFilter(Main::replacePackage)
                    .build()
                    .install();

            Log.i(TAG, "Inject successfully");
        } catch (Exception e) {
            Log.e(TAG, "Inject: " + e, e);
        }
    }

    private static IBinder replacePackage(String name, IBinder original) {
        if (!"package".equals(name)) {
            return original;
        }

        if (StackUtils.isStacktraceContains("getCommonServicesLocked")) {
            try {
                final IPackageManager fallback = IPackageManager.Stub.asInterface(original);
                return Proxy.FACTORY.create(IPackageManager.Stub.asInterface(original), new Proxy(fallback));
            } catch (Throwable e) {
                Log.e(TAG, "Replacing 'package': " + e, e);
            }
        }

        return original;
    }
}
