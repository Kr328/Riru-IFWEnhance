package com.github.kr328.ifw;

import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.IPackageManager;
import android.content.pm.ParceledListSlice;
import android.content.pm.ResolveInfo;
import android.os.Binder;
import android.os.RemoteException;

import com.github.kr328.magic.aidl.ServerProxy;
import com.github.kr328.magic.aidl.ServerProxyFactory;
import com.github.kr328.magic.aidl.TransactProxy;
import com.github.kr328.magic.util.BinderUtils;

import java.util.List;
import java.util.stream.Collectors;

public class Proxy extends IPackageManager.Stub {
    public static final ServerProxyFactory<IPackageManager, Proxy> FACTORY;

    static {
        try {
            FACTORY = ServerProxy.createFactory(IPackageManager.class, Proxy.class, false);
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }

    private final IPackageManager original;

    public Proxy(IPackageManager original) {
        this.original = original;
    }

    private static List<ResolveInfo> filterResolved(List<ResolveInfo> result, Intent intent, String resolvedType) {
        if (Firewall.get() == null)
            return result;
        if (intent == null)
            return result;
        if (intent.getComponent() != null)
            return result;

        try {
            int callingUid = Binder.getCallingUid();
            int callingPid = Binder.getCallingPid();
            String originalPackage = intent.getPackage();

            List<ResolveInfo> filtered = BinderUtils.withEvaluated(() -> result.stream().filter((info) -> {
                intent.setComponent(ComponentName.createRelative(info.activityInfo.packageName, info.activityInfo.name));
                intent.setPackage(info.activityInfo.packageName);

                return Firewall.get().checkStartActivity(intent, callingUid, callingPid, resolvedType, info.activityInfo.applicationInfo);
            }).collect(Collectors.toList()));

            intent.setComponent(null);
            intent.setPackage(originalPackage);

            return filtered;
        } catch (Exception ignored) {
            // ignore unknown exceptions
        }

        return result;
    }

    @Override
    @TransactProxy
    public ParceledListSlice<ResolveInfo> queryIntentActivities(Intent intent, String resolvedType, int flags, int userId) throws RemoteException {
        final ParceledListSlice<ResolveInfo> result = original.queryIntentActivities(intent, resolvedType, flags, userId);

        return new ParceledListSlice<>(filterResolved(result.getList(), intent, resolvedType));
    }

    @Override
    @TransactProxy
    public ParceledListSlice<ResolveInfo> queryIntentActivityOptions(ComponentName caller, Intent[] specifics, String[] specificTypes, Intent intent, String resolvedType, int flags, int userId) throws RemoteException {
        final ParceledListSlice<ResolveInfo> result = original.queryIntentActivityOptions(caller, specifics, specificTypes, intent, resolvedType, flags, userId);

        return new ParceledListSlice<>(filterResolved(result.getList(), intent, resolvedType));
    }
}
