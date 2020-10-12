package com.github.kr328.ifw;

import android.os.IBinder;
import android.util.Log;
import android.os.Process;

@SuppressWarnings({"unused", "RedundantSuppression"})
public class Injector extends ServiceProxy {
    public static final String TAG = "IFWEnhance";

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
    protected void onServiceAdded(String name, IBinder service) {
        Log.i(TAG, "New Service " + name);
    }
}
