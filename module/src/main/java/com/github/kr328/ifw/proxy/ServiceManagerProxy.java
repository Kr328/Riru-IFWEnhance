package com.github.kr328.ifw.proxy;

import android.os.*;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Objects;

public class ServiceManagerProxy implements IServiceManager {
    public interface Callback {
        IBinder addService(String name, IBinder original);
        IBinder getService(String name, IBinder original);
        IBinder checkService(String name, IBinder original);
    }

    private IServiceManager original;
    private Callback callback;

    public ServiceManagerProxy(IServiceManager original, Callback callback) {
        this.original = original;
        this.callback = callback;
    }

    public static IServiceManager getOriginalIServiceManager() throws ReflectiveOperationException {
        Method method = ServiceManager.class.getDeclaredMethod("getIServiceManager");
        method.setAccessible(true);
        return Objects.requireNonNull((IServiceManager) method.invoke(null));
    }

    public static void setDefaultServiceManager(IServiceManager serviceManager) throws ReflectiveOperationException {
        Field field = ServiceManager.class.getDeclaredField("sServiceManager");
        field.setAccessible(true);
        field.set(null, serviceManager);
    }

    @Override
    public IBinder getService(String name) throws RemoteException {
        return callback.getService(name, original.getService(name));
    }

    @Override
    public IBinder checkService(String name) throws RemoteException {
        return callback.checkService(name, original.checkService(name));
    }

    @Override
    public void addService(String name, IBinder service, boolean allowIsolated, int dumpFlags) throws RemoteException {
        original.addService(name, callback.addService(name, service), allowIsolated, dumpFlags);
    }

    @Override
    public String[] listServices(int dumpFlags) throws RemoteException {
        return original.listServices(dumpFlags);
    }

    @Override
    public void setPermissionController(IPermissionController controller) throws RemoteException {
        original.setPermissionController(controller);
    }

    @Override
    public IBinder asBinder() {
        return original.asBinder();
    }
}
