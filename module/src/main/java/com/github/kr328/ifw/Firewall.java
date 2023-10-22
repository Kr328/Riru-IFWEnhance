package com.github.kr328.ifw;

import android.app.ActivityManagerInternal;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.os.Binder;
import android.util.Log;

import com.android.server.LocalServices;
import com.github.kr328.magic.util.BinderUtils;
import com.github.kr328.magic.util.ClassLoaderUtils;

import java.lang.reflect.Field;
import java.util.List;
import java.util.stream.Collectors;

public final class Firewall {
    private static boolean initialized;
    private static IntentFirewall instance;

    private static void tryGetIntentFirewall() {
        Log.d(Main.TAG, "Try get intent firewall");

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

            final Field intentFirewall = Reflections.getDeclaredFieldHierarchy(ams.getClass(), "mIntentFirewall");
            intentFirewall.setAccessible(true);
            final Object firewall = intentFirewall.get(ams);

            final Class<?> wrapper = Firewall.class.getClassLoader()
                    .loadClass("com.github.kr328.ifw.Firewall$IntentFirewallWrapper");

            Firewall.instance = (IntentFirewall) wrapper.getConstructor(firewall.getClass())
                    .newInstance(firewall);

            Log.i(Main.TAG, "IntentFirewall of ActivityManagerService: " + firewall);
        } catch (final Throwable e) {
            Log.e(Main.TAG, "Get IntentFirewall of ActivityManagerService: " + e, e);
        }
    }

    public static synchronized IntentFirewall get() {
        if (!initialized) {
            tryGetIntentFirewall();
        }

        return instance;
    }

    public interface IntentFirewall {
        List<ResolveInfo> filterResult(
                List<ResolveInfo> result,
                FilterType type,
                Intent intent,
                String resolvedType
        );

        enum FilterType {
            ACTIVITY, SERVICE
        }
    }

    public static class IntentFirewallWrapper implements IntentFirewall {
        private final com.android.server.firewall.IntentFirewall impl;

        public IntentFirewallWrapper(final com.android.server.firewall.IntentFirewall impl) {
            this.impl = impl;
        }

        @Override
        public List<ResolveInfo> filterResult(
                final List<ResolveInfo> result,
                final FilterType type,
                final Intent intent,
                final String resolvedType
        ) {
            if (intent == null)
                return result;
            if (intent.getComponent() != null)
                return result;

            try {
                final int callingUid = Binder.getCallingUid();
                final int callingPid = Binder.getCallingPid();
                final String originalPackage = intent.getPackage();

                final List<ResolveInfo> filtered = BinderUtils.withEvaluated(() -> result.stream().filter((info) -> {
                    switch (type) {
                        case ACTIVITY:
                            intent.setComponent(ComponentName.createRelative(info.activityInfo.packageName, info.activityInfo.name));
                            intent.setPackage(info.activityInfo.packageName);

                            return impl.checkStartActivity(
                                    intent,
                                    callingUid,
                                    callingPid,
                                    resolvedType,
                                    info.activityInfo.applicationInfo
                            );
                        case SERVICE:
                            intent.setComponent(ComponentName.createRelative(info.serviceInfo.packageName, info.serviceInfo.name));
                            intent.setPackage(info.serviceInfo.packageName);

                            return impl.checkService(
                                    new ComponentName(info.serviceInfo.packageName, info.serviceInfo.name),
                                    intent,
                                    callingUid,
                                    callingPid,
                                    resolvedType,
                                    info.serviceInfo.applicationInfo
                            );
                    }

                    throw new IllegalArgumentException("unreachable");
                }).collect(Collectors.toList()));

                intent.setComponent(null);
                intent.setPackage(originalPackage);

                return filtered;
            } catch (final Exception e) {
                Log.w(Main.TAG, "Filter out intent: " + intent, e);
            }

            return result;
        }
    }
}
