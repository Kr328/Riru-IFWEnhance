package com.android.server.firewall;

import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.os.Handler;

public class IntentFirewall {
    public IntentFirewall(AMSInterface ams, Handler handler) {
        throw new IllegalArgumentException("Stub!");
    }

    public boolean checkStartActivity(Intent intent, int callerUid, int callerPid,
                                      String resolvedType, ApplicationInfo resolvedApp) {
        throw new IllegalArgumentException("Stub!");
    }

    public interface AMSInterface {
        int checkComponentPermission(String permission, int pid, int uid,
                                     int owningUid, boolean exported);

        Object getAMSLock();
    }
}
