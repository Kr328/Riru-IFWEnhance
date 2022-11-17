package com.github.kr328.ifw;

import java.lang.reflect.Field;

public final class Reflections {
    public static Field getDeclaredFieldHierarchy(Class<?> clazz, String name) throws NoSuchFieldException {
        while (clazz != null) {
            try {
                return clazz.getDeclaredField(name);
            } catch (NoSuchFieldException e) {
                clazz = clazz.getSuperclass();
            }
        }

        throw new NoSuchFieldException("No field " + name + " in class " + clazz + " hierarchy");
    }
}
