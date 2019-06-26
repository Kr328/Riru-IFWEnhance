package com.github.kr328.ifw.proxy;

import android.os.Binder;
import android.os.Parcel;
import android.os.RemoteException;

public class ProxyBinder extends Binder {
    private Binder original;
    private Callback callback;

    public ProxyBinder(Binder original, Callback callback) {
        this.original = original;
        this.callback = callback;
    }

    @Override
    protected boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
        if ( callback.onTransact(original, code, data, reply, flags) )
            return true;

        return original.transact(code, data, reply, flags);
    }

    @Override
    public void linkToDeath(DeathRecipient deathRecipient, int i) {
        original.linkToDeath(deathRecipient, i);
    }

    @Override
    public boolean unlinkToDeath(DeathRecipient deathRecipient, int i) {
        return original.unlinkToDeath(deathRecipient, i);
    }

    public interface Callback {
        boolean onTransact(Binder original, int code, Parcel data, Parcel reply, int flags) throws RemoteException;
    }
}
