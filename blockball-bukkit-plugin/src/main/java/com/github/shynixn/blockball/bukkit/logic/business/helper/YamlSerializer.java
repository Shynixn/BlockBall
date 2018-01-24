package com.github.shynixn.blockball.bukkit.logic.business.helper;

import org.bukkit.configuration.MemorySection;
import org.bukkit.configuration.serialization.ConfigurationSerializable;

import java.lang.annotation.*;
import java.lang.reflect.*;
import java.util.*;

/**
 * Copyright 2017 Shynixn
 * <p>
 * Do not remove this header!
 * <p>
 * Version 1.0
 * <p>
 * MIT License
 * <p>
 * Copyright (c) 2017
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
public final class YamlSerializer {
    /**
     * Initializes a new private instance
     */
    private YamlSerializer() {
        super();
    }

    /**
     * Annotation for fields to get serialized and deSerialized
     */
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.FIELD)
    public @interface YamlSerialize {
        String value();

        int orderNumber() default 0;
    }

    /**
     * Internal handler class
     */
    private static class AnnotationWrapper {
        final YamlSerialize annotation;
        final Field field;

        /**
         * Initializes a new annotationWrapper
         *
         * @param annotation annotation
         * @param field      field
         */
        AnnotationWrapper(YamlSerialize annotation, Field field) {
            super();
            this.annotation = annotation;
            this.field = field;
        }
    }

    /**
     * Serializes the given object regardless if it's an object, array, collection or map
     *
     * @param object object
     * @return serializedContent
     * @throws IllegalAccessException exception
     */
    public static Map<String, Object> serialize(Object object) throws IllegalAccessException {
        if (object.getClass().isArray()) {
            return serializeArray((Object[]) object);
        } else if (Collection.class.isAssignableFrom(object.getClass())) {
            return serializeCollection((Collection<?>) object);
        } else if (Map.class.isAssignableFrom(object.getClass())) {
            return serializeMap((Map<?, ?>) object);
        } else {
            return serializeObject(object);
        }
    }

    /**
     * Serializes the given map
     *
     * @param objects objects
     * @return serializedContent
     * @throws IllegalAccessException exception
     */
    public static Map<String, Object> serializeMap(Map<?, ?> objects) throws IllegalAccessException {
        final Map<String, Object> data = new LinkedHashMap<>();
        for (final Object key : objects.keySet()) {
            if (!isPrimitive(key.getClass()))
                throw new IllegalArgumentException("Cannot map non simple Map.");
            data.put(String.valueOf(key), serializeObject(objects.get(key)));
        }
        return data;
    }

    /**
     * Serializes the given array
     *
     * @param objects objects
     * @return serializedContent
     * @throws IllegalAccessException exception
     */
    public static Map<String, Object> serializeArray(Object[] objects) throws IllegalAccessException {
        return serializeCollection(Arrays.asList(objects));
    }

    /**
     * Serializes the given collection
     *
     * @param objects objects
     * @return serializedContent
     * @throws IllegalAccessException exception
     */
    public static Map<String, Object> serializeCollection(Collection<?> objects) throws IllegalAccessException {
        final Map<String, Object> data = new LinkedHashMap<>();
        int i = 1;
        for (final Object object : objects) {
            if(object != null)
            {
                if (ConfigurationSerializable.class.isAssignableFrom(object.getClass())) {
                    data.put(String.valueOf(i), ((ConfigurationSerializable) object).serialize());
                } else {
                    data.put(String.valueOf(i), serializeObject(object));
                }
            }
            i++;
        }
        return data;
    }

    /**
     * Serializes the given object
     *
     * @param object object
     * @return serializedContent
     * @throws IllegalAccessException exception
     */
    public static Map<String, Object> serializeObject(Object object) throws IllegalAccessException {
        if (object == null)
            return null;
        final Map<String, Object> data = new LinkedHashMap<>();
        for (final AnnotationWrapper annotationWrapper : getOrderedAnnotations(object.getClass())) {
            final Field field = annotationWrapper.field;
            final YamlSerialize yamlAnnotation = annotationWrapper.annotation;
            field.setAccessible(true);
            if (field.get(object) == null) {
            } else if (isPrimitive(field.getType())) {
                data.put(yamlAnnotation.value(), field.get(object));
            } else if (field.getType().isEnum()) {
                data.put(yamlAnnotation.value(), ((Enum) field.get(object)).name().toUpperCase());
            } else if (field.getType().isArray()) {
                data.put(yamlAnnotation.value(), serializeArray((Object[]) field.get(object)));
            } else if (Collection.class.isAssignableFrom(field.getType())) {
                if (getTypeFromHeavyField(field, 0) == String.class) {
                    data.put(yamlAnnotation.value(), field.get(object));
                } else {
                    data.put(yamlAnnotation.value(), serializeCollection((Collection<?>) field.get(object)));
                }
            } else if (Map.class.isAssignableFrom(field.getType())) {
                data.put(yamlAnnotation.value(), serializeMap((Map<?, ?>) field.get(object)));
            } else if (field.get(object) != null && ConfigurationSerializable.class.isAssignableFrom(field.get(object).getClass())) {
                data.put(yamlAnnotation.value(), ((ConfigurationSerializable) field.get(object)).serialize());
            } else {
                data.put(yamlAnnotation.value(), serializeObject(field.get(object)));
            }
        }
        return data;
    }

    /**
     * DeSerializes the given dataSource to a map
     *
     * @param clazz      type of the mapValue
     * @param mapClazz   type of the map
     * @param dataSource dataSource like map or fileConfiguration
     * @param <T>        type of the map
     * @param <E>        type of the mapValue
     * @return deSerialized map
     * @throws InstantiationException exception
     * @throws IllegalAccessException exception
     */
    public static <T extends Map, E> T deserializeMap(Class<E> clazz, Class<T> mapClazz, Object dataSource) throws InstantiationException, IllegalAccessException {
        final Map<String, Object> data = getDataFromSource(dataSource);
        Class<?> instanceClass = mapClazz;
        if (instanceClass == Map.class)
            instanceClass = HashMap.class;
        final T map = (T) instanceClass.newInstance();
        for (final String key : data.keySet()) {
            map.put(key, deserializeObject(clazz, ((MemorySection) data.get(key)).getValues(false)));
        }
        return map;
    }

    /**
     * DeSerializes the given dataSource to an array
     *
     * @param clazz      type of the object
     * @param dataSource dataSource like map or fileConfiguration
     * @param <T>        type of the object
     * @return deSerialized array
     * @throws InstantiationException exception
     * @throws IllegalAccessException exception
     */
    public static <T> T[] deserializeArray(Class<T> clazz, Object dataSource) throws InstantiationException, IllegalAccessException {
        final Map<String, Object> data = getDataFromSource(dataSource);
        final T[] objects = (T[]) Array.newInstance(clazz, data.size());
        int i = 0;
        for (final String key : data.keySet()) {
            objects[i] = deserializeObject(clazz, ((MemorySection) data.get(key)).getValues(false));
            i++;
        }
        return objects;
    }

    /**
     * DeSerializes the given dataSource to a collection
     *
     * @param clazz           type of the object
     * @param collectionClazz type of the collection
     * @param dataSource      dataSource like map or fileConfiguration
     * @param <T>             ype of the object
     * @param <E>             type of the collection
     * @return deSerialized collection
     * @throws IllegalAccessException exception
     * @throws InstantiationException exception
     */
    public static <T extends Collection, E> T deserializeCollection(Class<E> clazz, Class<T> collectionClazz, Object dataSource) throws IllegalAccessException, InstantiationException {
        Class<?> instanceClass = collectionClazz;
        if (instanceClass == List.class)
            instanceClass = ArrayList.class;
        else if (instanceClass == Set.class)
            instanceClass = HashSet.class;
        final T collection = (T) instanceClass.newInstance();
        return deserializeHeavyCollection(clazz, collection, dataSource);
    }

    private static <T extends Collection, E> T deserializeHeavyCollection(Class<E> clazz, T collection, Object dataSource) throws InstantiationException, IllegalAccessException {
        final Map<String, Object> data = getDataFromSource(dataSource);
        for (final String key : data.keySet()) {
            collection.add(deserializeObject(clazz, ((MemorySection) data.get(key)).getValues(false)));
        }
        return collection;
    }

    /**
     * DeSerializes the given dataSource to an object
     *
     * @param clazz      type of the object
     * @param dataSource dataSource like map or fileConfiguration
     * @param <T>        type of the object
     * @return deSerialized object
     * @throws IllegalAccessException exception
     * @throws InstantiationException exception
     */
    public static <T> T deserializeObject(Class<T> clazz, Object dataSource) throws IllegalAccessException, InstantiationException {
        if (clazz.isInterface()) {
            throw new IllegalArgumentException("Cannot instantiate interface. Change your object fields!");
        }
        final Map<String, Object> data = getDataFromSource(dataSource);
        try {
            final Constructor map = clazz.getConstructor(Map.class);
            return (T) map.newInstance(data);
        } catch (NoSuchMethodException | InvocationTargetException e) {
            final T object = clazz.newInstance();
            return heavyDeserialize(object, clazz, data);
        }
    }

    private static <T> T heavyDeserialize(T object, Class<?> clazz, Map<String, Object> data) throws IllegalAccessException, InstantiationException {
        Class<?> clazzQuery = clazz;
        while (clazzQuery != null) {
            for (final Field field : clazzQuery.getDeclaredFields()) {
                for (final Annotation annotation : field.getAnnotations()) {
                    if (annotation.annotationType() == YamlSerialize.class) {
                        final YamlSerialize yamlAnnotation = (YamlSerialize) annotation;
                        field.setAccessible(true);
                        if (data.containsKey(yamlAnnotation.value())) {
                            if (isPrimitive(field.getType())) {
                                field.set(object, data.get(yamlAnnotation.value()));
                            } else if (field.getType().isEnum()) {
                                field.set(object, Enum.valueOf((Class) field.getType(), data.get(yamlAnnotation.value()).toString()));
                            } else if (field.getType().isArray()) {
                                field.set(object, deserializeArray(clazzQuery, ((MemorySection) data.get(yamlAnnotation.value())).getValues(false)));
                            } else if (Collection.class.isAssignableFrom(field.getType())) {
                                if (field.get(object) != null) {
                                    ((Collection) field.get(object)).clear();
                                    if (data.get(yamlAnnotation.value()) instanceof Collection) {
                                        ((Collection) field.get(object)).addAll((Collection) data.get(yamlAnnotation.value()));
                                    } else {
                                        deserializeHeavyCollection(getTypeFromHeavyField(field, 0), ((Collection) field.get(object)), ((MemorySection) data.get(yamlAnnotation.value())).getValues(false));
                                    }
                                } else {
                                    field.set(object, deserializeCollection(getTypeFromHeavyField(field, 0), (Class<Collection>) field.getType(), ((MemorySection) data.get(yamlAnnotation.value())).getValues(false)));
                                }
                            } else if (Map.class.isAssignableFrom(field.getType())) {
                                field.set(object, deserializeMap(getTypeFromHeavyField(field, 1), (Class<Map>) field.getType(), ((MemorySection) data.get(yamlAnnotation.value())).getValues(false)));
                            } else {
                                if (field.get(object) != null) {
                                    heavyDeserialize(field.get(object), field.getType(), getDataFromSource(((MemorySection) data.get(yamlAnnotation.value())).getValues(false)));
                                } else {
                                    field.set(object, deserializeObject(field.getType(), ((MemorySection) data.get(yamlAnnotation.value())).getValues(false)));
                                }
                            }
                        }
                    }
                }
            }
            clazzQuery = clazzQuery.getSuperclass();
        }
        return object;
    }

    /**
     * Returns all annotationWrappers from a class to resolve the order
     *
     * @param sourceClass sourceClass
     * @return annotationWrappers
     */
    private static List<AnnotationWrapper> getOrderedAnnotations(Class<?> sourceClass) {
        final List<AnnotationWrapper> annotationWrappers = new ArrayList<>();
        Class<?> clazz = sourceClass;
        while (clazz != null) {
            for (final Field field : clazz.getDeclaredFields()) {
                for (final Annotation annotation : field.getAnnotations()) {
                    if (annotation.annotationType() == YamlSerialize.class) {
                        annotationWrappers.add(new AnnotationWrapper((YamlSerialize) annotation, field));
                    }
                }
            }
            clazz = clazz.getSuperclass();
        }
        annotationWrappers.sort((o1, o2) -> {
            if (o1.annotation.orderNumber() > o2.annotation.orderNumber())
                return 1;
            else if (o1.annotation.orderNumber() < o2.annotation.orderNumber())
                return -1;
            return 0;
        });
        return annotationWrappers;
    }

    /**
     * Checks if the given type is primitive
     *
     * @param clazz class
     * @return primitive
     */
    private static boolean isPrimitive(Class<?> clazz) {
        return clazz.isPrimitive() || clazz == String.class || clazz == Integer.class || clazz == Double.class || clazz == Long.class || clazz == Float.class;
    }

    /**
     * Returns the data from the dataSource
     *
     * @param dataSource dataSource
     * @return data
     */
    private static Map<String, Object> getDataFromSource(Object dataSource) {
        if (dataSource instanceof Map)
            return (Map<String, Object>) dataSource;
        else
            return ((MemorySection) dataSource).getValues(false);
    }

    /**
     * Returns the type from an heavy field
     *
     * @param field  field
     * @param number number
     * @return type
     */
    private static Class<?> getTypeFromHeavyField(Field field, int number) {
        return (Class<?>) ((ParameterizedType) field.getGenericType()).getActualTypeArguments()[number];
    }
}