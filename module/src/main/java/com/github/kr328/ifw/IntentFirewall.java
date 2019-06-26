package com.github.kr328.ifw;

import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.util.Log;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Objects;

public class IntentFirewall {
    private Object intentFirewall;
    private Method checkStartActivity;

    public static IntentFirewall fromActivityManager(Object activityManager) throws ReflectiveOperationException {
        IntentFirewall result = new IntentFirewall();

        Field field = activityManager.getClass().getDeclaredField("mIntentFirewall");
        field.setAccessible(true);
        result.intentFirewall = field.get(activityManager);

        if ( result.intentFirewall == null )
            throw new NoSuchFieldException("Unable to get IntentFireWall");

        result.checkStartActivity = result.intentFirewall.getClass().getDeclaredMethod("checkStartActivity",
                Intent.class, int.class, int.class, String.class, ApplicationInfo.class);

        if ( result.checkStartActivity == null )
            throw new NoSuchMethodException("Unable to get checkStartActivity");

        return result;
    }

    public boolean checkStartActivity(Intent intent, int callerUid, int callerPid, String resolvedType, ApplicationInfo resolvedApp) {
        try {
            return (boolean) this.checkStartActivity.invoke(intentFirewall, intent, callerUid, callerPid, resolvedType, resolvedApp);
        } catch (IllegalAccessException | InvocationTargetException e) {
            Log.e(Constants.TAG, "Call IntentFirewall failure", e);
            return true;
        }
    }
}
