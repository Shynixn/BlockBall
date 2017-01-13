package com.github.shynixn.blockball.lib;

import static java.lang.annotation.ElementType.CONSTRUCTOR;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.bukkit.plugin.java.JavaPlugin;

public final class SPluginLoader {
    public static void load(JavaPlugin plugin, Class<?>... classes) {
        for (Class<?> tClass : classes) {
            do {
                try {
                    for (final Field field : tClass.getDeclaredFields()) {
                        if (field.isAnnotationPresent(PluginLoader.class)) {
                            field.setAccessible(true);
                            field.set(null, plugin);
                        }
                    }
                    for (final Method method : tClass.getDeclaredMethods()) {
                        if (method.isAnnotationPresent(PluginLoader.class)) {
                            method.setAccessible(true);
                            method.invoke(null, plugin);
                        }
                    }
                } catch (IllegalArgumentException | IllegalAccessException | InvocationTargetException e) {
                    e.printStackTrace();
                }
                tClass = tClass.getSuperclass();
            } while (tClass != null);
        }
    }

    public static void unload(JavaPlugin plugin, Class<?>... classes) {
        for (Class<?> tClass : classes) {
            do {
                try {
                    for (final Method method : tClass.getDeclaredMethods()) {
                        if (method.isAnnotationPresent(PluginUnloader.class)) {
                            method.setAccessible(true);
                            method.invoke(null, plugin);
                        }
                    }
                } catch (IllegalArgumentException | IllegalAccessException | InvocationTargetException e) {
                    e.printStackTrace();
                }
                tClass = tClass.getSuperclass();
            } while (tClass != null);
        }
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target({CONSTRUCTOR, FIELD, METHOD})
    public @interface PluginLoader {
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target(METHOD)
    public @interface PluginUnloader {
    }
}
