package com.github.shynixn.blockball.lib;

import org.bukkit.Bukkit;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * Created by Shynixn
 */
@Deprecated
public final class ReflectionLib {

    public static String getServerVersion() {
        try {
            return Bukkit.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3];
        } catch (final Exception ex) {
            throw new RuntimeException("Version not found!");
        }
    }

    public static Class<?> getClassFromName(String name) {
        try {
            name = name.replace("VERSION", getServerVersion());
            return Class.forName(name);
        } catch (final Exception e) {
            throw new RuntimeException("Cannot find correct class.");
        }
    }

    public static <T> T createDefaultInstance(Class<T> clazz) {
        try {
            if (clazz == null)
                throw new RuntimeException("Clazz cannot be null!");
            return clazz.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {

        }
        return null;
    }

    public static Object invokeConstructor(Class<?> clazz, Object... params) {
        do {
            for (final Constructor constructor : clazz.getDeclaredConstructors()) {
                try {
                    constructor.setAccessible(true);
                    return constructor.newInstance(params);
                } catch (final Exception ex) {

                }
            }
            clazz = clazz.getSuperclass();
        } while (clazz != null);
        throw new RuntimeException("Cannot find correct constructor.");
    }

    public static Object invokeMethodByObject(Object object, String name, Object... params) {
        Class<?> clazz = object.getClass();
        do {
            for (final Method method : clazz.getDeclaredMethods()) {
                try {
                    if (method.getName().equalsIgnoreCase(name)) {
                        method.setAccessible(true);
                        return method.invoke(object, params);
                    }
                } catch (final Exception ex) {

                }
            }
            clazz = clazz.getSuperclass();
        } while (clazz != null);
        throw new RuntimeException("Cannot find correct method.");
    }

    @Deprecated
    public static Object invokeMethod(Object object, String name, Object... params) {
        Class<?> clazz = object.getClass();
        do {
            for (final Method method : clazz.getDeclaredMethods()) {
                try {
                    if (method.getName().equalsIgnoreCase(name)) {
                        method.setAccessible(true);
                        return method.invoke(object, params);
                    }
                } catch (final Exception ex) {

                }
            }
            clazz = clazz.getSuperclass();
        } while (clazz != null);
        throw new RuntimeException("Cannot find correct method.");
    }

    public static void setValueOfField(String fieldName, Object instance, Object object) {
        Class<?> clazz = object.getClass();
        while (clazz != null) {
            for (final Field field : clazz.getDeclaredFields()) {
                if (field.getName().equals(fieldName)) {
                    field.setAccessible(true);
                    try {
                        field.set(instance, object);
                    } catch (final IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
            }
            clazz = clazz.getSuperclass();
        }
    }

    @Deprecated
    public static Object getValueFromField(String fieldName, Object object) {
        Class<?> clazz = object.getClass();
        while (clazz != null) {
            for (final Field field : clazz.getDeclaredFields()) {
                if (field.getName().equals(fieldName)) {
                    field.setAccessible(true);
                    try {
                        return field.get(object);
                    } catch (final IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
            }
            clazz = clazz.getSuperclass();
        }
        return null;
    }

    public static Object getValueFromFieldByObject(String fieldName, Object object) {
        Class<?> clazz = object.getClass();
        while (clazz != null) {
            for (final Field field : clazz.getDeclaredFields()) {
                if (field.getName().equals(fieldName)) {
                    field.setAccessible(true);
                    try {
                        return field.get(object);
                    } catch (final IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
            }
            clazz = clazz.getSuperclass();
        }
        return null;
    }

    public static Object getValueFromFieldByClazz(String fieldName, Class<?> object) {
        Class<?> clazz = object;
        while (clazz != null) {
            for (final Field field : clazz.getDeclaredFields()) {
                if (field.getName().equals(fieldName)) {
                    field.setAccessible(true);
                    try {
                        return field.get(null);
                    } catch (final IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
            }
            clazz = clazz.getSuperclass();
        }
        return null;
    }

    public static Object getValueFromField(String fieldName, Class<?> object) {
        Class<?> clazz = object;
        while (clazz != null) {
            for (final Field field : clazz.getDeclaredFields()) {
                if (field.getName().equals(fieldName)) {
                    field.setAccessible(true);
                    try {
                        return field.get(null);
                    } catch (final IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
            }
            clazz = clazz.getSuperclass();
        }
        return null;
    }

    public static Object invokeMethodByClazz(Class<?> clazz, String name, Object... params) {
        do {
            for (final Method method : clazz.getDeclaredMethods()) {
                try {
                    if (method.getName().equalsIgnoreCase(name)) {
                        method.setAccessible(true);
                        return method.invoke(null, params);
                    }
                } catch (final Exception ex) {

                }
            }
            clazz = clazz.getSuperclass();
        } while (clazz != null);
        throw new RuntimeException("Cannot find correct method.");
    }

    public static Object invokeMethod(Class<?> clazz, String name, Object... params) {
        do {
            for (final Method method : clazz.getDeclaredMethods()) {
                try {
                    if (method.getName().equalsIgnoreCase(name)) {
                        method.setAccessible(true);
                        return method.invoke(null, params);
                    }
                } catch (final Exception ex) {

                }
            }
            clazz = clazz.getSuperclass();
        } while (clazz != null);
        throw new RuntimeException("Cannot find correct method.");
    }

    public static Method getMethodFromName(String name, Class<?> clazz) {
        for (final Method method : clazz.getDeclaredMethods()) {
            try {
                if (method.getName().equalsIgnoreCase(name)) {
                    return method;
                }
            } catch (final Exception ex) {

            }
        }
        return null;
    }

    @Deprecated
    public static Class<?> createClass(String classPath) {
        try {
            classPath = classPath.replace("VERSION", getServerVersion());
            return Class.forName(classPath);
        } catch (final ClassNotFoundException e) {
            e.printStackTrace();
        }
        throw new RuntimeException("Cannot find correct class.");
    }

    public static <T extends Annotation> T getAnnotation(Class<T> annotation, Class<?> clazz) {
        while (clazz != null) {
            for (final Annotation annot : clazz.getDeclaredAnnotations()) {
                if (annot.annotationType().getName().equalsIgnoreCase(annotation.getName())) {
                    return (T) annot;
                }
            }
            clazz = clazz.getSuperclass();
        }
        return null;
    }

    private static void printExceptions(Exception e, boolean showExceptions) {
        if (showExceptions)
            e.printStackTrace();
    }
}

