package com.github.shynixn.blockball.bukkit.logic.persistence.entity.meta.effect;

import com.github.shynixn.blockball.api.persistence.entity.meta.effect.PotionEffectMeta;
import com.github.shynixn.blockball.bukkit.logic.persistence.entity.PersistenceObject;
import org.bukkit.Bukkit;
import org.bukkit.entity.LivingEntity;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.logging.Level;

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
public class PotionEffectBuilder extends PersistenceObject implements PotionEffectMeta {
    private int type = PotionEffectType.ABSORPTION.getId();
    private int duration;
    private int strength;
    private boolean ambient;
    private boolean particles;

    /**
     * Initializes a new PotionEffectBuilder
     */
    public PotionEffectBuilder() {
        super();
    }

    /**
     * Initializes a new PotionEffectBuilder with the given params
     *
     * @param type      type
     * @param duration  duration
     * @param amplifier amplifier
     */
    public PotionEffectBuilder(PotionEffectType type, int duration, int amplifier) {
        this(type, duration, amplifier, false, false);
    }

    /**
     * Initializes a new PotionEffectBuilder with the given params
     *
     * @param type      type
     * @param duration  duration
     * @param amplifier amplifier
     * @param ambient   ambient
     * @param particles particles
     */
    public PotionEffectBuilder(PotionEffectType type, int duration, int amplifier, boolean ambient, boolean particles) {
        super();
        if (type == null)
            throw new IllegalArgumentException("Type cannot be null!");
        this.type = type.getId();
        this.duration = duration;
        this.strength = amplifier;
        this.ambient = ambient;
        this.particles = particles;
    }

    /**
     * Initializes a new PotionEffectBuilder from the given BukkitPotionEffect
     *
     * @param potionEffect potionEffect
     */
    public PotionEffectBuilder(PotionEffect potionEffect) {
        super();
        if (potionEffect == null)
            throw new IllegalArgumentException("Potioneffect cannot be null!");
        this.type = potionEffect.getType().getId();
        this.duration = potionEffect.getDuration();
        this.strength = potionEffect.getAmplifier();
        this.ambient = potionEffect.isAmbient();
        this.particles = potionEffect.hasParticles();
    }

    /**
     * Parses the potioneffect out of the map
     *
     * @param items items
     * @throws Exception mapParseException
     */
    public PotionEffectBuilder(Map<String, Object> items) throws Exception {
        super();
        if (items == null)
            throw new IllegalArgumentException("Items cannot be null!");
        this.type = (int) items.get("type");
        this.duration = (int) items.get("duration");
        this.strength = (int) items.get("strength");
        this.ambient = (boolean) items.get("ambient");
        this.particles = (boolean) items.get("particles");
    }

    /**
     * Applies the potioneffect to living entities
     *
     * @param entities entities
     */
    public void apply(LivingEntity... entities) {
        if (entities == null)
            throw new IllegalArgumentException("Entities cannot be null!");
        for (final LivingEntity entity : entities) {
            if (this.getFromId() != null && entity.hasPotionEffect(this.getFromId())) {
                entity.removePotionEffect(this.getFromId());
            }
            entity.addPotionEffect(new PotionEffect(this.getFromId(), this.duration, this.strength, this.ambient));
        }
    }

    /**
     * Sets the type of the potioneffect
     *
     * @param type type
     * @return builder
     */
    public PotionEffectBuilder setType(PotionEffectType type) {
        if (type == null)
            throw new IllegalArgumentException("Type cannot be null!");
        this.type = type.getId();
        return this;
    }

    /**
     * Applies the potioneffect to living entities
     *
     * @param entities entities
     */
    @Override
    public void apply(Collection<Object> entities) {
        this.apply(entities.toArray(new LivingEntity[entities.size()]));
    }

    /**
     * Sets the type of the potioneffect
     *
     * @param type type
     * @return builder
     */
    @Override
    public PotionEffectBuilder setTypeId(int type) {
        this.type = type;
        return this;
    }

    /**
     * Sets the amount of seconds the potioneffect is going to last
     *
     * @param seconds seconds
     * @return builder
     */
    @Override
    public PotionEffectBuilder setSeconds(int seconds) {
        if (seconds < 0)
            throw new IllegalArgumentException("Time cannot be lower than 0");
        this.duration = seconds * 20;
        return this;
    }

    /**
     * Sets the amount of ticks the potioneffect is going to last
     *
     * @param ticks ticks
     * @return builder
     */
    @Override
    public PotionEffectBuilder setTicks(int ticks) {
        if (ticks < 0)
            throw new IllegalArgumentException("Time cannot be lower than 0");
        this.duration = ticks;
        return this;
    }

    /**
     * Sets the potioneffect strength
     *
     * @param strength strength
     * @return builder
     */
    @Override
    public PotionEffectBuilder setStrength(int strength) {
        this.strength = strength - 1;
        return this;
    }

    /**
     * Sets the potioneffect amplifier
     *
     * @param amplifier amplifier
     * @return builder
     */
    @Override
    public PotionEffectBuilder setAmplifier(int amplifier) {
        this.strength = amplifier;
        return this;
    }

    /**
     * Sets if the potioneffect is ambient visible
     *
     * @param ambient ambient
     * @return builder
     */
    @Override
    public PotionEffectBuilder setAmbientVisible(boolean ambient) {
        this.ambient = ambient;
        return this;
    }

    /**
     * Sets if the potioneffect is particle visible
     *
     * @param visible visible
     * @return builder
     */
    @Override
    public PotionEffectBuilder setParticleVisible(boolean visible) {
        this.particles = visible;
        return this;
    }

    /**
     * Returns the potioneffect id
     *
     * @return id
     */
    @Override
    public int getTypeId() {
        return this.type;
    }

    /**
     * Returns the type of the potioneffect
     *
     * @return type
     */
    public PotionEffectType getType() {
        return this.getFromId();
    }

    /**
     * Returns the duration of the potioneffect
     *
     * @return duration
     */
    @Override
    public int getDuration() {
        return this.duration;
    }

    /**
     * Returns the strength of the potioneffect
     *
     * @return strength
     */
    @Override
    public int getStrength() {
        return this.strength + 1;
    }

    /**
     * Returns the amplifier of the potioneffect
     *
     * @return amplifier
     */
    @Override
    public int getAmplifier() {
        return this.strength;
    }

    /**
     * Returns if the ambient is visible
     *
     * @return ambient
     */
    @Override
    public boolean isAmbientVisible() {
        return this.ambient;
    }

    /**
     * Returns if the particle isvisible
     *
     * @return particle
     */
    @Override
    public boolean isParticleVisible() {
        return this.particles;
    }

    /**
     * Converts the builder to a bukkitPotionEffect
     *
     * @return bukkitPotionEffect
     */
    public PotionEffect build() {
        return new PotionEffect(PotionEffectType.getById(this.type), this.duration, this.strength, this.ambient, this.particles);
    }

    /**
     * Checks if 2 builders are equal
     *
     * @param arg0 secondBuilder
     * @return isSame
     */
    @Override
    public boolean equals(Object arg0) {
        if (arg0 != null && arg0 instanceof PotionEffectBuilder) {
            final PotionEffectBuilder builder = (PotionEffectBuilder) arg0;
            if (builder.type == this.type
                    && builder.duration == this.duration
                    && builder.strength == this.strength
                    && builder.ambient == this.ambient
                    && builder.particles == this.particles)
                return true;
        }
        return false;
    }

    /**
     * Displays the builder as string
     *
     * @return string
     */
    @Override
    public String toString() {
        return "effect {" + "id " + this.type + " duration " + this.duration + " amplifier " + this.strength + "}";
    }

    /**
     * Serializes the potionEffect data to be stored to the filesystem
     *
     * @return serializedContent
     */
    @Override
    public Map<String, Object> serialize() {
        final Map<String, Object> map = new LinkedHashMap<>();
        map.put("type", this.type);
        map.put("duration", this.duration);
        map.put("strength", this.strength);
        map.put("ambient", this.ambient);
        map.put("particles", this.particles);
        return map;
    }

    /**
     * Returns a text of all potionEffects to let the user easily view them
     *
     * @return potionEffects
     */
    public static String getPotionEffectsText() {
        final StringBuilder s = new StringBuilder();
        for (final String a : getPos().keySet()) {
            if (s.length() != 0) {
                s.append(", ");
            }
            s.append(a);
        }
        return s.toString();
    }

    /**
     * Returns the potionEffectType from name
     *
     * @param name name
     * @return potionEffectType
     */
    public static PotionEffectType getPotionEffectFromName(String name) {
        for (final String a : getPos().keySet()) {
            if (name != null && a.equalsIgnoreCase(name))
                return getPos().get(a);
        }
        return null;
    }

    /**
     * Returns the type from the id
     *
     * @return type
     */
    private PotionEffectType getFromId() {
        return PotionEffectType.getById(this.type);
    }


    /**
     * Clones the current object
     *
     * @return object
     */
    @Override
    public PotionEffectMeta clone() {
        try {
            return new PotionEffectBuilder(this.serialize());
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Returns all potionEffect types correctly
     *
     * @return types
     */
    private static Map<String, PotionEffectType> getPos() {
        final Field field;
        try {
            field = PotionEffectType.class.getDeclaredField("byName");
            field.setAccessible(true);
            return (Map<String, PotionEffectType>) field.get(null);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            Bukkit.getLogger().log(Level.WARNING, "Cannot access potion effect types.", e);
            return new HashMap<>();
        }
    }
}