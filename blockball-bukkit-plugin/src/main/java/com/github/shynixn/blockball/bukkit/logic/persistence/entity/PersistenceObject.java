package com.github.shynixn.blockball.bukkit.logic.persistence.entity;

import com.github.shynixn.blockball.api.persistence.entity.Persistenceable;
import com.github.shynixn.blockball.bukkit.logic.business.helper.YamlSerializer;
import org.bukkit.configuration.serialization.ConfigurationSerializable;

import java.lang.reflect.Field;
import java.util.Map;

public abstract class PersistenceObject<T> implements Persistenceable<T>, ConfigurationSerializable {
    long id;

    /**
     * Returns the id of the object
     *
     * @return id
     */
    @Override
    public long getId() {
        return this.id;
    }

    /**
     * Sets the id of the object
     *
     * @param id id
     */
    public void setId(long id) {
        this.id = id;
    }

    /**
     * Clones the current object
     *
     * @return object
     */
    @Override
    public T clone() {
        try {
            final T item = (T) this.getClass().newInstance();
            Class<?> clazz = this.getClass();
            while (clazz != null) {
                for (final Field field : clazz.getDeclaredFields()) {
                    field.setAccessible(true);
                    field.set(item, field.get(this));
                }
                clazz = clazz.getSuperclass();
            }
            return item;
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Resets the object to the default values
     */
    @Override
    public void reset(T object) {
        try {
            Class<?> clazz = this.getClass();
            while (clazz != null) {
                for (final Field field : clazz.getDeclaredFields()) {
                    field.setAccessible(true);
                    field.set(this, field.get(object));
                }
                clazz = clazz.getSuperclass();
            }
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Serializes the given object.
     *
     * @return serializedContent
     */
    @Override
    public Map<String, Object> serialize() {
        try {
            return YamlSerializer.serialize(this);
        } catch (final IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
