package android.content.pm;

import android.content.ComponentName;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.RemoteException;

public interface IPackageManager extends IInterface {
    ParceledListSlice<ResolveInfo> queryIntentActivities(
            Intent intent,
            String resolvedType,
            int flags,
            int userId
    ) throws RemoteException;

    ParceledListSlice<ResolveInfo> queryIntentActivities(
            Intent intent,
            String resolvedType,
            long flags,
            int userId
    ) throws RemoteException;

    ParceledListSlice<ResolveInfo> queryIntentActivityOptions(
            ComponentName caller,
            Intent[] specifics,
            String[] specificTypes,
            Intent intent,
            String resolvedType,
            int flags,
            int userId
    ) throws RemoteException;

    ParceledListSlice<ResolveInfo> queryIntentActivityOptions(
            ComponentName caller,
            Intent[] specifics,
            String[] specificTypes,
            Intent intent,
            String resolvedType,
            long flags,
            int userId
    ) throws RemoteException;

    ParceledListSlice<ResolveInfo> queryIntentServices(
            Intent intent,
            String resolvedType,
            int flags,
            int userId
    ) throws RemoteException;

    ParceledListSlice<ResolveInfo> queryIntentServices(
            Intent intent,
            String resolvedType,
            long flags,
            int userId
    ) throws RemoteException;

    abstract class Stub extends Binder implements IPackageManager {
        public static IPackageManager asInterface(IBinder binder) {
            throw new IllegalArgumentException("Stub!");
        }

        @Override
        public IBinder asBinder() {
            throw new IllegalArgumentException("Stub!");
        }
    }
}
