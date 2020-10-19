package com.github.kr328.ifw;

import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.IPackageManager;
import android.content.pm.ParceledListSlice;
import android.content.pm.ResolveInfo;
import android.os.Binder;
import android.os.RemoteException;
import android.util.Log;

import com.github.kr328.ifw.ProxyFactory.TransactHook;

import java.util.ArrayList;

public class PackageProxy extends IPackageManager.Stub {
    private final IPackageManager original;
    private final IntentFirewall firewall;

    PackageProxy(IPackageManager original, IntentFirewall firewall) {
        this.original = original;
        this.firewall = firewall;
    }

    @Override
    @TransactHook
    public ParceledListSlice<ResolveInfo> queryIntentActivities(Intent intent, String resolvedType, int flags, int userId) throws RemoteException {
        if (firewall == null)
            return original.queryIntentActivities(intent, resolvedType, flags, userId);
        if (intent == null)
            return original.queryIntentActivities(null, resolvedType, flags, userId);
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
                intent.setPackage(info.activityInfo.packageName);

                if (firewall.checkStartActivity(intent, callingUid, callingPid, resolvedType, info.activityInfo.applicationInfo))
                    replaced.add(info);
            }

            Binder.restoreCallingIdentity(callingIdentity);

            return new ParceledListSlice<>(replaced);
        } catch (Exception e) {
            Log.w(Injector.TAG, "Unknown exception", e);
        }

        return or;
    }

    @Override
    public String[] getPackagesForUid(int uid) throws RemoteException {
        return original.getPackagesForUid(uid);
    }
}
