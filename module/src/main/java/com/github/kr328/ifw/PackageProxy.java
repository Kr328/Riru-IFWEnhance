package com.github.kr328.ifw;

import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.IPackageManager;
import android.content.pm.PackageManager;
import android.content.pm.ParceledListSlice;
import android.content.pm.ResolveInfo;
import android.os.Binder;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.RemoteException;

import com.android.server.firewall.IntentFirewall;
import com.github.kr328.magic.proxy.AIDLProxy.TransactProxy;
import com.github.kr328.magic.util.BinderUtils;

import java.util.List;
import java.util.stream.Collectors;

public class PackageProxy extends IPackageManager.Stub implements IntentFirewall.AMSInterface {
    private static final HandlerThread thread = new HandlerThread("package_proxy");
    private static final Handler handler;

    static {
        thread.start();

        handler = new Handler(thread.getLooper());
    }

    private final IPackageManager original;
    private final IntentFirewall firewall;

    public PackageProxy(IPackageManager original) {
        this.original = original;
        this.firewall = new IntentFirewall(this, handler);
    }

    @Override
    @TransactProxy
    public ParceledListSlice<ResolveInfo> queryIntentActivities(Intent intent, String resolvedType, int flags, int userId) throws RemoteException {
        if (firewall == null)
            return original.queryIntentActivities(intent, resolvedType, flags, userId);
        if (intent == null)
            return original.queryIntentActivities(null, resolvedType, flags, userId);
        if (intent.getComponent() != null)
            return original.queryIntentActivities(intent, resolvedType, flags, userId);

        final ParceledListSlice<ResolveInfo> result = original.queryIntentActivities(intent, resolvedType, flags, userId);

        try {
            int callingUid = Binder.getCallingUid();
            int callingPid = Binder.getCallingPid();
            final List<ResolveInfo> filtered = BinderUtils.withEvaluated(() -> result.getList().stream().filter((info) -> {
                intent.setComponent(ComponentName.createRelative(info.activityInfo.packageName, info.activityInfo.name));
                intent.setPackage(info.activityInfo.packageName);

                return firewall.checkStartActivity(intent, callingUid, callingPid, resolvedType, info.activityInfo.applicationInfo);
            }).collect(Collectors.toList()));

            return new ParceledListSlice<>(filtered);
        } catch (Exception ignored) {
            // ignore unknown exceptions
        }

        return result;
    }

    @Override
    public String[] getPackagesForUid(int uid) throws RemoteException {
        return original.getPackagesForUid(uid);
    }

    @Override
    public int checkComponentPermission(String permission, int pid, int uid, int owningUid, boolean exported) {
        return PackageManager.PERMISSION_GRANTED;
    }

    @Override
    public Object getAMSLock() {
        return this;
    }
}
