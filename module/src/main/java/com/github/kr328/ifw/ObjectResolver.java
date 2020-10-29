package com.github.kr328.ifw;

import java.lang.reflect.Field;
import java.util.HashSet;

public final class ObjectResolver {
    private static final ClassLoader bootstrap = String.class.getClassLoader();

    public static Object resolve(Object root, String className, int maxDeep) throws ReflectiveOperationException {
        return resolve(root, className, maxDeep, new HashSet<>());
    }

    private static Object resolve(Object root, String className, int deep, HashSet<Object> resolved) throws ReflectiveOperationException {
        if (className.equals(root.getClass().getName()))
            return root;

        if (deep <= 0)
            return null;

        if (root.getClass().getClassLoader() == bootstrap)
            return null;

        if (resolved.contains(root))
            return null;

        resolved.add(root);

        Class<?> clazz = root.getClass();
        while (clazz != null && clazz.getClassLoader() != bootstrap) {
            for (Field field : clazz.getDeclaredFields()) {
                field.setAccessible(true);

                Object obj = field.get(root);
                if (obj == null) {
                    continue;
                }

                Object r = resolve(obj, className, deep - 1, resolved);
                if (r != null)
                    return r;
            }

            clazz = clazz.getSuperclass();
        }

        return null;
    }
}
