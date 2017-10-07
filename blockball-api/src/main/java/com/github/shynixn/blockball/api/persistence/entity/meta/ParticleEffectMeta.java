package com.github.shynixn.blockball.api.persistence.entity.meta;

import com.github.shynixn.blockball.api.persistence.entity.Persistenceable;

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
public interface ParticleEffectMeta extends Persistenceable<ParticleEffectMeta> {
    /**
     * Sets the RGB colors of the particleEffect
     *
     * @param red   red
     * @param green green
     * @param blue  blue
     * @return builder
     */
    ParticleEffectMeta setColor(int red, int green, int blue);

    /**
     * Sets the color of the particleEffect
     *
     * @param particleColor particleColor
     * @return builder
     */
    ParticleEffectMeta setColor(ParticleColor particleColor);

    /**
     * Sets the color for note particleEffect
     *
     * @param color color
     * @return builder
     */
    ParticleEffectMeta setNoteColor(int color);

    /**
     * Sets the amount of particles of the particleEffect
     *
     * @param amount amount
     * @return builder
     */
    ParticleEffectMeta setAmount(int amount);

    /**
     * Sets the speed of the particleEffect
     *
     * @param speed speed
     * @return builder
     */
    ParticleEffectMeta setSpeed(double speed);

    /**
     * Sets the offsetX of the particleEffect
     *
     * @param offsetX offsetX
     * @return builder
     */
    ParticleEffectMeta setOffsetX(double offsetX);

    /**
     * Sets the offsetY of the particleEffect
     *
     * @param offsetY offsetY
     * @return builder
     */
    ParticleEffectMeta setOffsetY(double offsetY);

    /**
     * Sets the offsetZ of the particleEffect
     *
     * @param offsetZ offsetZ
     * @return builder
     */
    ParticleEffectMeta setOffsetZ(double offsetZ);

    /**
     * Sets the offset of the particleEffect
     *
     * @param offsetX offsetX
     * @param offsetY offsetY
     * @param offsetZ offsetZ
     * @return instance
     */
    ParticleEffectMeta setOffset(double offsetX, double offsetY, double offsetZ);

    /**
     * Sets the effectType of the particleEffect
     *
     * @param name name
     * @return builder
     */
    ParticleEffectMeta setEffectName(String name);

    /**
     * Sets the effectType of the particlEffect
     *
     * @param type type
     * @return builder
     */
    ParticleEffectMeta setEffectType(ParticleEffectType type);

    /**
     * Sets the blue of the RGB color
     *
     * @param blue blue
     * @return builder
     */
    ParticleEffectMeta setBlue(int blue);

    /**
     * Sets the red of the RGB color
     *
     * @param red red
     * @return builder
     */
    ParticleEffectMeta setRed(int red);

    /**
     * Sets the green of the RGB color
     *
     * @param green green
     * @return builder
     */
    ParticleEffectMeta setGreen(int green);

    /**
     * Sets the material of the particleEffect
     *
     * @param material material
     * @return builder
     */
    ParticleEffectMeta setMaterial(Object material);

    /**
     * Sets the data of the material of the particleEffect
     *
     * @param data data
     * @return builder
     */
    ParticleEffectMeta setData(Byte data);

    /**
     * Returns the effect of the particleEffect
     *
     * @return effectName
     */
    String getEffectName();

    /**
     * Returns the particleEffectType of the particleEffect
     *
     * @return effectType
     */
    ParticleEffectType getEffectType();

    /**
     * Returns the amount of particles of the particleEffect
     *
     * @return amount
     */
    int getAmount();

    /**
     * Returns the speed of the particleEffect
     *
     * @return speed
     */
    double getSpeed();

    /**
     * Returns the offsetX of the particleEffect
     *
     * @return offsetX
     */
    double getOffsetX();

    /**
     * Returns the offsetY of the particleEffect
     *
     * @return offsetY
     */
    double getOffsetY();

    /**
     * Returns the offsetZ of the particleEffect
     *
     * @return offsetZ
     */
    double getOffsetZ();

    /**
     * Returns the RGB color blue of the particleEffect
     *
     * @return blue
     */
    int getBlue();

    /**
     * Returns the RGB color red of the particleEffect
     *
     * @return red
     */
    int getRed();

    /**
     * Returns the RGB color green of the particleEffect
     *
     * @return green
     */
    int getGreen();

    /**
     * Returns the material of the particleEffect
     *
     * @return material
     */
    Object getMaterial();

    /**
     * Returns the data of the particleEffect
     *
     * @return data
     */
    Byte getData();

    /**
     * Copies the current builder
     *
     * @return copyOfBuilder
     */
    ParticleEffectMeta clone();

    /**
     * Returns if the particleEffect is a color particleEffect
     *
     * @return isColor
     */
    boolean isColorParticleEffect();

    /**
     * Returns if the particleEffect is a note particleEffect
     *
     * @return isNote
     */
    boolean isNoteParticleEffect();

    /**
     * Returns if the particleEffect is a materialParticleEffect
     *
     * @return isMaterial
     */
    boolean isMaterialParticleEffect();

    /**
     * Plays the effect at the given location to the given players.
     *
     * @param location location
     * @param players  players
     */
    void apply(Object location, Collection<Object> players);

    /**
     * Plays the effect at the given location to the given players.
     *
     * @param location location
     */
    void apply(Object location);

    /**
     * ParticleColors
     */
    enum ParticleColor {
        BLACK(0, 0, 0),
        DARK_BLUE(0, 0, 170),
        DARK_GREEN(0, 170, 0),
        DARK_AQUA(0, 170, 170),
        DARK_RED(170, 0, 0),
        DARK_PURPLE(170, 0, 170),
        GOLD(255, 170, 0),
        GRAY(170, 170, 170),
        DARK_GRAY(85, 85, 85),
        BLUE(85, 85, 255),
        GREEN(85, 255, 85),
        AQUA(85, 255, 255),
        RED(255, 85, 85),
        LIGHT_PURPLE(255, 85, 255),
        YELLOW(255, 255, 85),
        WHITE(255, 255, 255);

        private final int red;
        private final int green;
        private final int blue;

        /**
         * Initializes a new particleColor
         *
         * @param red   red
         * @param green green
         * @param blue  blue
         */
        ParticleColor(int red, int green, int blue) {
            this.red = red;
            this.green = green;
            this.blue = blue;
        }

        /**
         * Returns the RGB value red
         *
         * @return red
         */
        public int getRed() {
            return this.red;
        }

        /**
         * Returns the RGB value green
         *
         * @return green
         */
        public int getGreen() {
            return this.green;
        }

        /**
         * Returns the RGB value blue
         *
         * @return blue
         */
        public int getBlue() {
            return this.blue;
        }
    }

    /**
     * ParticleEffectTypes
     */
    enum ParticleEffectType {
        EXPLOSION_NORMAL("explode", 0),
        EXPLOSION_LARGE("largeexplode", 1),
        EXPLOSION_HUGE("hugeexplosion", 2),
        FIREWORKS_SPARK("fireworksSpark", 3),
        WATER_BUBBLE("bubble", 4),
        WATER_SPLASH("splash", 5),
        WATER_WAKE("wake", 6),
        SUSPENDED("suspended", 7),
        SUSPENDED_DEPTH("depthsuspend", 8),
        CRIT("crit", 9),
        CRIT_MAGIC("magicCrit", 10),
        SMOKE_NORMAL("smoke", 11),
        SMOKE_LARGE("largesmoke", 12),
        SPELL("spell", 13),
        SPELL_INSTANT("instantSpell", 14),
        SPELL_MOB("mobSpell", 15),
        SPELL_MOB_AMBIENT("mobSpellAmbient", 16),
        SPELL_WITCH("witchMagic", 17),
        DRIP_WATER("dripWater", 18),
        DRIP_LAVA("dripLava", 19),
        VILLAGER_ANGRY("angryVillager", 20),
        VILLAGER_HAPPY("happyVillager", 21),
        TOWN_AURA("townaura", 22),
        NOTE("note", 23),
        PORTAL("portal", 24),
        ENCHANTMENT_TABLE("enchantmenttable", 25),
        FLAME("flame", 26),
        LAVA("lava", 27),
        FOOTSTEP("footstep", 28),
        CLOUD("cloud", 29),
        REDSTONE("reddust", 30),
        SNOWBALL("snowballpoof", 31),
        SNOW_SHOVEL("snowshovel", 32),
        SLIME("slime", 33),
        HEART("heart", 34),
        BARRIER("barrier", 35),
        ITEM_CRACK("iconcrack", 36),
        BLOCK_CRACK("blockcrack", 37),
        BLOCK_DUST("blockdust", 38),
        WATER_DROP("droplet", 39),
        ITEM_TAKE("take", 40),
        MOB_APPEARANCE("mobappearance", 41),
        DRAGON_BREATH("dragonbreath", 42),
        END_ROD("endRod", 43),
        DAMAGE_INDICATOR("damageIndicator", 44),
        SWEEP_ATTACK("sweepAttack", 45),
        FALLING_DUST("fallingdust", 46),
        TOTEM("totem", 47),
        NONE("none", 102),
        SPIT("spit", 48);

        private final String simpleName;
        private final int id;

        /**
         * Initializes a new particleEffectType
         *
         * @param name name
         * @param id   id
         */
        ParticleEffectType(String name, int id) {
            this.simpleName = name;
            this.id = id;
        }

        /**
         * Returns the id of the particleEffectType
         *
         * @return id
         */
        public int getId() {
            return this.id;
        }

        /**
         * Returns the name of the particleEffectType
         *
         * @return name
         */
        public String getSimpleName() {
            return this.simpleName;
        }
    }
}
