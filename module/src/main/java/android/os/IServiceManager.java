package android.os;

public interface IServiceManager extends IInterface {
    IBinder getService(String name) throws RemoteException;

    IBinder checkService(String name) throws RemoteException;

    void addService(String name, IBinder service, boolean allowIsolated, int dumpFlags)
            throws RemoteException;

    String[] listServices(int dumpFlags) throws RemoteException;

    void setPermissionController(IPermissionController controller)
            throws RemoteException;
}
