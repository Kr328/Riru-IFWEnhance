package com.github.kr328.ifw;

import android.app.ActivityManagerInternal;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.util.Log;

import com.android.server.LocalServices;
import com.github.kr328.magic.util.ClassLoaderUtils;

import java.lang.reflect.Field;

public final class Firewall {
    private static boolean initialized;
    private static IntentFirewall instance;

    private static void tryGetIntentFirewall() {
        final Object instance = LocalServices.getService(ActivityManagerInternal.class);
        if (instance == null) {
            return;
        }

        Firewall.initialized = true;

        try {
            ClassLoaderUtils.replaceParentClassLoader(
                    Firewall.class.getClassLoader(),
                    instance.getClass().getClassLoader()
            );

            final Field this$0 = instance.getClass().getDeclaredField("this$0");
            this$0.setAccessible(true);
            final Object ams = this$0.get(instance);

            final Field intentFirewall = ams.getClass().getDeclaredField("mIntentFirewall");
            intentFirewall.setAccessible(true);
            final Object firewall = intentFirewall.get(ams);

            final Class<?> wrapper = Firewall.class.getClassLoader().loadClass("com.github.kr328.ifw.Firewall$IntentFirewallWrapper");

            Firewall.instance = (IntentFirewall) wrapper.getConstructor(firewall.getClass()).newInstance(firewall);

            Log.i(Injector.TAG, "IntentFirewall of ActivityManagerService: " + firewall);
        } catch (Throwable e) {
            Log.e(Injector.TAG, "Get IntentFirewall of ActivityManagerService: " + e, e);
        }
    }

    public static synchronized IntentFirewall get() {
        if (!initialized) {
            tryGetIntentFirewall();
        }

        return instance;
    }

    public interface IntentFirewall {
        boolean checkStartActivity(
                Intent intent,
                int callerUid, int callerPid,
                String resolvedType,
                ApplicationInfo resolvedApp
        );
    }

    public static class IntentFirewallWrapper implements IntentFirewall {
        private final com.android.server.firewall.IntentFirewall impl;

        public IntentFirewallWrapper(com.android.server.firewall.IntentFirewall impl) {
            this.impl = impl;
        }

        @Override
        public boolean checkStartActivity(Intent intent, int callerUid, int callerPid, String resolvedType, ApplicationInfo resolvedApp) {
            return impl.checkStartActivity(intent, callerUid, callerPid, resolvedType, resolvedApp);
        }
    }
}
