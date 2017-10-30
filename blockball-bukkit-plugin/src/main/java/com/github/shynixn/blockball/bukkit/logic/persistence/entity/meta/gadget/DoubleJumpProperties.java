package com.github.shynixn.blockball.bukkit.logic.persistence.entity.meta.gadget;

import com.github.shynixn.blockball.api.persistence.entity.meta.gadget.DoubleJumpMeta;
import com.github.shynixn.blockball.api.persistence.entity.meta.effect.ParticleEffectMeta;
import com.github.shynixn.blockball.api.persistence.entity.meta.effect.SoundEffectMeta;
import com.github.shynixn.blockball.bukkit.logic.persistence.entity.PersistenceObject;
import com.github.shynixn.blockball.bukkit.logic.persistence.entity.meta.effect.ParticleEffectBuilder;
import com.github.shynixn.blockball.bukkit.logic.persistence.entity.meta.effect.SoundBuilder;
import com.github.shynixn.blockball.lib.YamlSerializer;

/**
 * Created by Shynixn 2017.
 * <p>
 * Version 1.1
 * <p>
 * MIT License
 * <p>
 * Copyright (c) 2017 by Shynixn
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
public class DoubleJumpProperties extends PersistenceObject<DoubleJumpMeta> implements DoubleJumpMeta {

    @YamlSerializer.YamlSerialize(orderNumber = 1, value = "enabled")
    private boolean enabled = true;
    @YamlSerializer.YamlSerialize(orderNumber = 2, value = "strength.horizontal")
    private double horizontalStrength = 2.6;
    @YamlSerializer.YamlSerialize(orderNumber = 3, value = "strength.vertical")
    private double verticalStrength = 1.0;
    @YamlSerializer.YamlSerialize(orderNumber = 4, value = "cooldown")
    private int cooldown;

    @YamlSerializer.YamlSerialize(orderNumber = 5, value = "sound")
    private final SoundEffectMeta soundMeta = new SoundBuilder()
            .setName("GHAST_FIREBALL")
            .setVolume(100)
            .setPitch(1.0);
    @YamlSerializer.YamlSerialize(orderNumber = 6, value = "particleEffect")
    private final ParticleEffectMeta particleEffectMeta = new ParticleEffectBuilder()
    .setEffectType(ParticleEffectMeta.ParticleEffectType.EXPLOSION_NORMAL)
    .setAmount(4)
    .setSpeed(0.0002)
    .setOffset(2, 2, 2);

    /**
     * Enables the double jump
     *
     * @param enabled enabled
     */
    @Override
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    /**
     * Checks if the double jump is enabled
     *
     * @return enabled
     */
    @Override
    public boolean isEnabled() {
        return this.enabled;
    }

    /**
     * Sets the horizontal strength of the double jump
     *
     * @param strength strength
     */
    @Override
    public void setHorizontalStrength(double strength) {
        this.horizontalStrength = strength;
    }

    /**
     * Returns the horizontal strength of the double jump
     *
     * @return strength
     */
    @Override
    public double getHorizontalStrength() {
        return this.horizontalStrength;
    }

    /**
     * Sets the cooldown in seconds between each jump.
     *
     * @param amount amount
     */
    @Override
    public void setCooldownInSeconds(int amount) {
        this.cooldown = amount;
    }

    /**
     * Returns the cooldown in seconds between each jump.
     *
     * @return amount
     */
    @Override
    public int getCooldownInSeconds() {
        return this.cooldown;
    }

    /**
     * Sets the vertical strength of the double jump
     *
     * @param strength strength
     */
    @Override
    public void setVerticalStrength(double strength) {
        this.verticalStrength = strength;
    }

    /**
     * Returns the vertical strength of the double jump
     *
     * @return strength
     */
    @Override
    public double getVerticalStrength() {
        return this.verticalStrength;
    }

    /**
     * Returns the particleEffect of the double jump
     *
     * @return particleEffect
     */
    @Override
    public ParticleEffectMeta getParticleEffect() {
        return this.particleEffectMeta;
    }

    /**
     * Returns the soundEffect of the double jump
     *
     * @return soundEffect
     */
    @Override
    public SoundEffectMeta getSoundEffect() {
        return this.soundMeta;
    }
}
