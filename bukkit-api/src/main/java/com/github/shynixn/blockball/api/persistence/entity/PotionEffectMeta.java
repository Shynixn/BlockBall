package com.github.shynixn.blockball.api.persistence.entity;

import org.bukkit.entity.LivingEntity;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Collection;

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
public interface PotionEffectMeta extends Persistenceable<PotionEffectMeta> {
    /**
     * Applies the potioneffect to living entities
     *
     * @param entities entities
     */
    void apply(Collection<LivingEntity> entities);

    /**
     * Applies the potioneffect to living entities
     *
     * @param entities entities
     */
    void apply(LivingEntity... entities);

    /**
     * Sets the type of the potioneffect
     *
     * @param type type
     * @return builder
     */
    PotionEffectMeta setType(PotionEffectType type);

    /**
     * Sets the type of the potioneffect
     *
     * @param type type
     * @return builder
     */
    PotionEffectMeta setTypeId(int type);

    /**
     * Sets the amount of seconds the potioneffect is going to last
     *
     * @param seconds seconds
     * @return builder
     */
    PotionEffectMeta setSeconds(int seconds);

    /**
     * Sets the amount of ticks the potioneffect is going to last
     *
     * @param ticks ticks
     * @return builder
     */
    PotionEffectMeta setTicks(int ticks);

    /**
     * Sets the potioneffect strength
     *
     * @param strength strength
     * @return builder
     */
    PotionEffectMeta setStrength(int strength);

    /**
     * Sets the potioneffect amplifier
     *
     * @param amplifier amplifier
     * @return builder
     */
    PotionEffectMeta setAmplifier(int amplifier);

    /**
     * Sets if the potioneffect is ambient visible
     *
     * @param ambient ambient
     * @return builder
     */
    PotionEffectMeta setAmbientVisible(boolean ambient);

    /**
     * Sets if the potioneffect is particle visible
     *
     * @param visible visible
     * @return builder
     */
    PotionEffectMeta setParticleVisible(boolean visible);

    /**
     * Returns the potioneffect id
     *
     * @return id
     */
    int getTypeId();

    /**
     * Returns the type of the potioneffect
     *
     * @return type
     */
    PotionEffectType getType();

    /**
     * Returns the duration of the potioneffect
     *
     * @return duration
     */
    int getDuration();

    /**
     * Returns the strength of the potioneffect
     *
     * @return strength
     */
    int getStrength();

    /**
     * Returns the amplifier of the potioneffect
     *
     * @return amplifier
     */
    int getAmplifier();

    /**
     * Returns if the ambient is visible
     *
     * @return ambient
     */
    boolean isAmbientVisible();

    /**
     * Returns if the particle isvisible
     *
     * @return particle
     */
    boolean isParticleVisible();

    /**
     * Converts the builder to a bukkitPotionEffect
     *
     * @return bukkitPotionEffect
     */
    PotionEffect build();
}
