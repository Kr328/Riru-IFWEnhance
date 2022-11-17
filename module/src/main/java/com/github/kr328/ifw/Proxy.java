package com.github.kr328.ifw;

import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.IPackageManager;
import android.content.pm.ParceledListSlice;
import android.content.pm.ResolveInfo;
import android.os.RemoteException;

import com.github.kr328.magic.aidl.ServerProxy;
import com.github.kr328.magic.aidl.ServerProxyFactory;
import com.github.kr328.magic.aidl.TransactProxy;

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

    @Override
    @TransactProxy
    public ParceledListSlice<ResolveInfo> queryIntentActivities(
            Intent intent,
            String resolvedType,
            int flags,
            int userId
    ) throws RemoteException {
        final ParceledListSlice<ResolveInfo> result = original.queryIntentActivities(
                intent,
                resolvedType,
                flags,
                userId
        );

        if (Firewall.get() == null) {
            return result;
        }

        return new ParceledListSlice<>(
                Firewall.get().filterResult(
                        result.getList(),
                        Firewall.IntentFirewall.FilterType.ACTIVITY,
                        intent,
                        resolvedType
                )
        );
    }

    @Override
    @TransactProxy
    public ParceledListSlice<ResolveInfo> queryIntentActivities(
            Intent intent,
            String resolvedType,
            long flags,
            int userId
    ) throws RemoteException {
        final ParceledListSlice<ResolveInfo> result = original.queryIntentActivities(
                intent,
                resolvedType,
                flags,
                userId
        );

        if (Firewall.get() == null) {
            return result;
        }

        return new ParceledListSlice<>(
                Firewall.get().filterResult(
                        result.getList(),
                        Firewall.IntentFirewall.FilterType.ACTIVITY,
                        intent,
                        resolvedType
                )
        );
    }

    @Override
    @TransactProxy
    public ParceledListSlice<ResolveInfo> queryIntentActivityOptions(
            ComponentName caller,
            Intent[] specifics,
            String[] specificTypes,
            Intent intent,
            String resolvedType,
            int flags,
            int userId
    ) throws RemoteException {
        final ParceledListSlice<ResolveInfo> result = original.queryIntentActivityOptions(
                caller,
                specifics,
                specificTypes,
                intent,
                resolvedType,
                flags,
                userId
        );

        if (Firewall.get() == null) {
            return result;
        }

        return new ParceledListSlice<>(
                Firewall.get().filterResult(
                        result.getList(),
                        Firewall.IntentFirewall.FilterType.ACTIVITY,
                        intent,
                        resolvedType
                )
        );
    }

    @Override
    @TransactProxy
    public ParceledListSlice<ResolveInfo> queryIntentActivityOptions(
            ComponentName caller,
            Intent[] specifics,
            String[] specificTypes,
            Intent intent,
            String resolvedType,
            long flags,
            int userId
    ) throws RemoteException {
        final ParceledListSlice<ResolveInfo> result = original.queryIntentActivityOptions(
                caller,
                specifics,
                specificTypes,
                intent,
                resolvedType,
                flags,
                userId
        );

        if (Firewall.get() == null) {
            return result;
        }

        return new ParceledListSlice<>(
                Firewall.get().filterResult(
                        result.getList(),
                        Firewall.IntentFirewall.FilterType.ACTIVITY,
                        intent,
                        resolvedType
                )
        );
    }


    @Override
    @TransactProxy
    public ParceledListSlice<ResolveInfo> queryIntentServices(
            Intent intent,
            String resolvedType,
            int flags,
            int userId
    ) throws RemoteException {
        final ParceledListSlice<ResolveInfo> result = original.queryIntentServices(
                intent,
                resolvedType,
                flags,
                userId
        );

        if (Firewall.get() == null) {
            return result;
        }

        return new ParceledListSlice<>(
                Firewall.get().filterResult(
                        result.getList(),
                        Firewall.IntentFirewall.FilterType.SERVICE,
                        intent,
                        resolvedType
                )
        );
    }

    @Override
    @TransactProxy
    public ParceledListSlice<ResolveInfo> queryIntentServices(
            Intent intent,
            String resolvedType,
            long flags,
            int userId
    ) throws RemoteException {
        final ParceledListSlice<ResolveInfo> result = original.queryIntentServices(
                intent,
                resolvedType,
                flags,
                userId
        );

        if (Firewall.get() == null) {
            return result;
        }

        return new ParceledListSlice<>(
                Firewall.get().filterResult(
                        result.getList(),
                        Firewall.IntentFirewall.FilterType.SERVICE,
                        intent,
                        resolvedType
                )
        );
    }
}
