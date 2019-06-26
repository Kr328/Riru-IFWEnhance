package com.github.kr328.ifw;

import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.IPackageManager;
import android.content.pm.ParceledListSlice;
import android.content.pm.ResolveInfo;
import android.os.Binder;
import android.os.IInterface;
import android.os.RemoteException;
import android.util.Log;

import java.lang.reflect.Method;
import java.util.ArrayList;

public class PackageManagerProxy extends IPackageManager.Stub {
    private IPackageManager original;
    private IntentFirewall intentFirewall;

    public PackageManagerProxy(IPackageManager original) {
        this.original = original;
    }

    public void putActivityManager(Binder activityManager) {
        if ( activityManager == null )
            return;

        try {
            intentFirewall = IntentFirewall.fromActivityManager(activityManager);
        } catch (ReflectiveOperationException e) {
            Log.e(Constants.TAG, "Find IntentFirewall failure", e);
        }
    }

    @Override
    public IInterface queryLocalInterface(String descriptor) {
        return null;
    }

    @Override
    public ParceledListSlice<ResolveInfo> queryIntentActivities(Intent intent, String resolvedType, int flags, int userId) throws RemoteException {
        if ( intentFirewall == null )
            return original.queryIntentActivities(intent, resolvedType, flags, userId);
        if (intent == null)
            return original.queryIntentActivities(intent, resolvedType, flags, userId);
        if (intent.getPackage() != null)
            return original.queryIntentActivities(intent, resolvedType, flags, userId);
        if (intent.getComponent() != null)
            return original.queryIntentActivities(intent, resolvedType, flags, userId);

        ParceledListSlice<ResolveInfo> or = original.queryIntentActivities(intent, resolvedType, flags, userId);

        try {
            ArrayList<ResolveInfo> replaced = new ArrayList<>();
            int callingUid = Binder.getCallingUid();
            int callingPid = Binder.getCallingPid();

            long callingIdentity = Binder.clearCallingIdentity();

            for (ResolveInfo info : or.getList()) {
                intent.setComponent(ComponentName.createRelative(info.activityInfo.packageName, info.activityInfo.name));

                if ( intentFirewall.checkStartActivity(intent, callingUid, callingPid, resolvedType, info.activityInfo.applicationInfo) )
                    replaced.add(info);
            }

            Binder.restoreCallingIdentity(callingIdentity);

            return new ParceledListSlice<>(replaced);
        }
        catch (Exception e) {
            Log.w(Constants.TAG, "Unknown exception", e);
        }

        return or;
    }
}
