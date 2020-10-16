package com.github.kr328.ifw;

import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.IPackageManager;
import android.content.pm.ParceledListSlice;
import android.content.pm.ResolveInfo;
import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;
import android.util.Log;

import java.io.FileDescriptor;
import java.lang.reflect.Field;
import java.util.ArrayList;

public class PackageProxy extends Binder {
    private static final int codeQueryIntentActivity;

    static {
        int code = -1;

        try {
            final Field field = IPackageManager.Stub.class
                    .getDeclaredField("TRANSACTION_queryIntentActivities");

            field.setAccessible(true);

            code = (int) field.get(null);
        } catch (ReflectiveOperationException e) {
            Log.e(Injector.TAG, "Query transact code failure", e);
        }

        codeQueryIntentActivity = code;
    }

    private final IBinder original;
    private final IPackageManager originalManager;
    private final IntentFirewall firewall;
    private final IPackageManager.Stub parser = new IPackageManager.Stub() {
        @Override
        public ParceledListSlice<ResolveInfo> queryIntentActivities(Intent intent, String resolvedType, int flags, int userId) throws RemoteException {
            if (firewall == null)
                return originalManager.queryIntentActivities(intent, resolvedType, flags, userId);
            if (intent == null)
                return originalManager.queryIntentActivities(null, resolvedType, flags, userId);
            if (intent.getComponent() != null)
                return originalManager.queryIntentActivities(intent, resolvedType, flags, userId);

            ParceledListSlice<ResolveInfo> or = originalManager.queryIntentActivities(intent, resolvedType, flags, userId);

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
            return originalManager.getPackagesForUid(uid);
        }
    };

    PackageProxy(IBinder original, IPackageManager originalManager, IntentFirewall firewall) {
        this.original = original;
        this.originalManager = originalManager;
        this.firewall = firewall;
    }

    @Override
    protected boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
        if (codeQueryIntentActivity == code) {
            return parser.transact(code, data, reply, flags);
        }

        return original.transact(code, data, reply, flags);
    }

    @Override
    public String getInterfaceDescriptor() {
        try {
            return original.getInterfaceDescriptor();
        } catch (RemoteException e) {
            throw new IllegalArgumentException(e);
        }
    }

    @Override
    public boolean pingBinder() {
        return original.pingBinder();
    }

    @Override
    public boolean isBinderAlive() {
        return original.isBinderAlive();
    }

    @Override
    public IInterface queryLocalInterface(String descriptor) {
        return null;
    }

    @Override
    public void attachInterface(IInterface owner, String descriptor) {
        super.attachInterface(owner, descriptor);
    }

    @Override
    public void dump(FileDescriptor fd, String[] args) {
        try {
            original.dump(fd, args);
        } catch (RemoteException e) {
            throw new IllegalArgumentException(e);
        }
    }

    @Override
    public void dumpAsync(FileDescriptor fd, String[] args) {
        try {
            original.dumpAsync(fd, args);
        } catch (RemoteException e) {
            throw new IllegalArgumentException(e);
        }
    }

    @Override
    public void linkToDeath(DeathRecipient recipient, int flags) {
        try {
            original.linkToDeath(recipient, flags);
        } catch (RemoteException e) {
            throw new IllegalArgumentException(e);
        }
    }

    @Override
    public boolean unlinkToDeath(DeathRecipient recipient, int flags) {
        return original.unlinkToDeath(recipient, flags);
    }
}
