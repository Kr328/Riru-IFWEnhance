package com.android.server.firewall;

import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.ApplicationInfo;

public class IntentFirewall {
    public boolean checkStartActivity(
            Intent intent,
            int callerUid,
            int callerPid,
            String resolvedType,
            ApplicationInfo resolvedApp
    ) {
        throw new IllegalArgumentException("Stub!");
    }

    public boolean checkService(
            ComponentName resolvedService,
            Intent intent,
            int callerUid,
            int callerPid,
            String resolvedType,
            ApplicationInfo resolvedApp
    ) {
        throw new IllegalArgumentException("Stub!");
    }
}
