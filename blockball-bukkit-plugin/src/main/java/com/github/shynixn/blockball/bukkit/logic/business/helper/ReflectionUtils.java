package com.github.shynixn.blockball.bukkit.logic.business.helper;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Copyright 2017 Shynixn
 * <p>
 * Do not remove this header!
 * <p>
 * Version 1.0
 * <p>
 * MIT License
 * <p>
 * Copyright (c) 2016
 * <p>
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * <p>
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * <p>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
public final class ReflectionUtils {
    /**
     * Initializes a new instance of reflectionUtils
     */
    private ReflectionUtils() {
        super();
    }

    /**
     * Returns the class from the name and converts VERSION to the current server version.
     *
     * @param name name
     * @return clazz
     * @throws ClassNotFoundException exception
     */
    public static Class<?> invokeClass(String name) throws ClassNotFoundException {
        if (name == null)
            throw new IllegalArgumentException("Name cannot be null!");
        return Class.forName(name);
    }

    /**
     * Creates a new instance of the given clazz with the default constructor
     *
     * @param clazz clazz
     * @param <T>   returnType
     * @return instance
     * @throws IllegalAccessException exception
     * @throws InstantiationException exceptionInstance
     */
    public static <T> T invokeDefaultConstructor(Class<T> clazz) throws IllegalAccessException, InstantiationException {
        if (clazz == null)
            throw new IllegalArgumentException("Class cannot be null!");
        return clazz.newInstance();
    }

    /**
     * Creates a new instance of the given clazz, paramTypes, params
     *
     * @param clazz      clazz
     * @param paramTypes paramTypes
     * @param params     params
     * @param <T>        classType
     * @return instance
     * @throws IllegalAccessException    exception
     * @throws InvocationTargetException exception
     * @throws InstantiationException    exception
     * @throws NoSuchMethodException     exception
     */
    public static <T> T invokeConstructor(Class<?> clazz, Class<?>[] paramTypes, Object[] params) throws IllegalAccessException, InvocationTargetException, InstantiationException, NoSuchMethodException {
        if (clazz == null)
            throw new IllegalArgumentException("Class cannot be null!");
        if (paramTypes == null)
            throw new IllegalArgumentException("ParamTypes cannot be null");
        if (params == null)
            throw new IllegalArgumentException("Params cannot be null!");
        final Constructor<?> constructor = clazz.getDeclaredConstructor(paramTypes);
        constructor.setAccessible(true);
        return (T) constructor.newInstance(params);
    }

    /**
     * Invokes the static method of the given clazz, name, paramTypes, params
     *
     * @param clazz      clazz
     * @param name       name
     * @param paramTypes paramTypes
     * @param params     params
     * @param <T>        returnType
     * @return returnValue
     * @throws NoSuchMethodException     exception
     * @throws InvocationTargetException exception
     * @throws IllegalAccessException    exception
     */
    public static <T> T invokeMethodByClass(Class<?> clazz, String name, Class<?>[] paramTypes, Object[] params) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        if (clazz == null)
            throw new IllegalArgumentException("Class cannot be null!");
        if (name == null)
            throw new IllegalArgumentException("Name cannot be null!");
        if (paramTypes == null)
            throw new IllegalArgumentException("ParamTypes cannot be null");
        if (params == null)
            throw new IllegalArgumentException("Params cannot be null!");
        final Method method = clazz.getDeclaredMethod(name, paramTypes);
        method.setAccessible(true);
        return (T) method.invoke(null, params);
    }

    /**
     * Invokes the method of the given instance, name, paramTypes, params
     *
     * @param instance   instance
     * @param name       name
     * @param paramTypes paramTypes
     * @param params     params
     * @param <T>        returnType
     * @return returnValue
     * @throws NoSuchMethodException     exception
     * @throws InvocationTargetException exception
     * @throws IllegalAccessException    exception
     */
    public static <T> T invokeMethodByObject(Object instance, String name, Class<?>[] paramTypes, Object[] params) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        return invokeMethodByObject(instance, name, paramTypes, params, instance.getClass());
    }

    /**
     * Invokes the method of the given instance, name, paramTypes, params
     *
     * @param instance   instance
     * @param name       name
     * @param paramTypes paramTypes
     * @param params     params
     * @param clazz      clazz
     * @param <T>        returnType
     * @return returnValue
     * @throws NoSuchMethodException     exception
     * @throws InvocationTargetException exception
     * @throws IllegalAccessException    exception
     */
    public static <T> T invokeMethodByObject(Object instance, String name, Class<?>[] paramTypes, Object[] params, Class<?> clazz) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        if (instance == null)
            throw new IllegalArgumentException("Instance cannot be null!");
        if (name == null)
            throw new IllegalArgumentException("Name cannot be null!");
        if (paramTypes == null)
            throw new IllegalArgumentException("ParamTypes cannot be null");
        if (params == null)
            throw new IllegalArgumentException("Params cannot be null!");
        if (clazz == null)
            throw new IllegalArgumentException("Class cannot be null!");
        final Method method = clazz.getDeclaredMethod(name, paramTypes);
        method.setAccessible(true);
        return (T) method.invoke(instance, params);
    }

    /**
     * Invokes the field of the given class and name
     *
     * @param clazz clazz
     * @param name  name
     * @param <T>   returnType
     * @return returnValue
     * @throws NoSuchFieldException   exception
     * @throws IllegalAccessException exception
     */
    public static <T> T invokeFieldByClass(Class<?> clazz, String name) throws NoSuchFieldException, IllegalAccessException {
        if (clazz == null)
            throw new IllegalArgumentException("Class cannot be null!");
        if (name == null)
            throw new IllegalArgumentException("Name cannot be null!");
        final Field field = clazz.getDeclaredField(name);
        field.setAccessible(true);
        return (T) field.get(null);
    }

    /**
     * Invokes the field of the given instance and name
     *
     * @param instance instance
     * @param name     name
     * @param <T>      returnType
     * @throws NoSuchFieldException exception
     * @throws IllegalAccessException exceptionInstance
     * @return returnValue
     */
    public static <T> T invokeFieldByObject(Object instance, String name) throws NoSuchFieldException, IllegalAccessException {
        return invokeFieldByObject(instance, name, instance.getClass());
    }

    /**
     * Invokes the field of the given instance and name
     *
     * @param instance instance
     * @param name     name
     * @param clazz    clazz
     * @param <T>      returnType
     * @throws NoSuchFieldException exception
     * @throws IllegalAccessException exceptionInstance
     * @return returnValue
     */
    public static <T> T invokeFieldByObject(Object instance, String name, Class<?> clazz) throws NoSuchFieldException, IllegalAccessException {
        if (instance == null)
            throw new IllegalArgumentException("Object cannot be null!");
        if (name == null)
            throw new IllegalArgumentException("Name cannot be null!");
        if (clazz == null)
            throw new IllegalArgumentException("Class cannot be null!");
        final Field field = clazz.getDeclaredField(name);
        field.setAccessible(true);
        return (T) field.get(instance);
    }

    /**
     * Invokes the annotation of the given clazz
     *
     * @param clazz           clazz
     * @param annotationClazz annotation
     * @param <T>             returnType
     * @return returnValue
     */
    public static <T extends Annotation> T invokeAnnotationByClass(Class<?> clazz, Class<T> annotationClazz) {
        if (clazz == null)
            throw new IllegalArgumentException("Class cannot be null!");
        if (annotationClazz == null)
            throw new IllegalArgumentException("AnnotationClass cannot be null!");
        for (final Annotation annotation : clazz.getDeclaredAnnotations()) {
            if (annotation.annotationType() == annotationClazz)
                return (T) annotation;
        }
        return null;
    }

    /**
     * Invokes the annotation of the given field
     *
     * @param field           field
     * @param annotationClazz annotation
     * @param <T>             returnType
     * @return returnValue
     */
    public static <T extends Annotation> T invokeAnnotationByField(Field field, Class<T> annotationClazz) {
        if (field == null)
            throw new IllegalArgumentException("Field cannot be null!");
        if (annotationClazz == null)
            throw new IllegalArgumentException("AnnotationClass cannot be null!");
        for (final Annotation annotation : field.getDeclaredAnnotations()) {
            if (annotation.annotationType() == annotationClazz)
                return (T) annotation;
        }
        return null;
    }

    /**
     * Invokes the annotation of the given method
     *
     * @param method          method
     * @param annotationClazz annotation
     * @param <T>             returnType
     * @return returnValue
     */
    public static <T extends Annotation> T invokeAnnotationByMethod(Method method, Class<T> annotationClazz) {
        if (method == null)
            throw new IllegalArgumentException("Method cannot be null!");
        if (annotationClazz == null)
            throw new IllegalArgumentException("AnnotationClass cannot be null!");
        for (final Annotation annotation : method.getDeclaredAnnotations()) {
            if (annotation.annotationType() == annotationClazz)
                return (T) annotation;
        }
        return null;
    }
}