package com.github.shynixn.blockball.api.business.enumeration

/**
 * ParticleEffects compatible to BlockBall.
 * <p>
 * Version 1.2
 * <p>
 * MIT License
 * <p>
 * Copyright (c) 2018 by Shynixn
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
enum class ParticleType(
        /**
         * Particle id for 1.8 to 1.12.
         */
        val gameId_18: String,
        /**
         * Particle id for 1.13.
         */
        val gameId_113: String,

        /**
         * Minecraft Id for 1.12.
         */
        val minecraftId_112: String,

        /**
         * Version where this effect was added.
         */
        val sinceVersion: Version) {

    /**
     * No Particle.
     */
    NONE("none", "none", "none", Version.VERSION_1_8_R1),
    /**
     * Explosion.
     */
    EXPLOSION_NORMAL("explode", "poof", "explosion", Version.VERSION_1_8_R1),
    /**
     * Large explosion.
     */
    EXPLOSION_LARGE("largeexplode", "explosion", "large_explosion", Version.VERSION_1_8_R1),
    /**
     * Huge explosion.
     */
    EXPLOSION_HUGE("hugeexplosion", "explosion_emitter", "huge_explosion", Version.VERSION_1_8_R1),
    /**
     * Firework.
     */
    FIREWORKS_SPARK("fireworksSpark", "firework", "fireworks_spark", Version.VERSION_1_8_R1),
    /**
     * Water Bubble simple.
     */
    WATER_BUBBLE("bubble", "bubble", "water_bubble", Version.VERSION_1_8_R1),
    /**
     * Water Bubble up.
     */
    WATER_BUBBLE_UP("bubble_column_up", "bubble_column_up", "bubble_column_up", Version.VERSION_1_13_R1),
    /**
     * Water Bubble pop.
     */
    WATER_BUBBLE_POP("bubble_pop", "bubble_pop", "bubble_pop", Version.VERSION_1_13_R1),
    /**
     * Water Splash.
     */
    WATER_SPLASH("splash", "splash", "water_splash", Version.VERSION_1_8_R1),
    /**
     * Fishing effect.
     */
    WATER_WAKE("wake", "fishing", "water_wake", Version.VERSION_1_8_R1),
    /**
     * Underwater bubbles.
     */
    SUSPENDED("suspended", "underwater", "suspended", Version.VERSION_1_8_R1),
    /**
     * Unused effect.
     */
    SUSPENDED_DEPTH("depthsuspend", "depthsuspend", "suspended_depth", Version.VERSION_1_8_R1),
    /**
     * Critical damage.
     */
    CRIT("crit", "crit", "critical_hit", Version.VERSION_1_8_R1),
    /**
     * Critical magical damage.
     */
    CRIT_MAGIC("magicCrit", "enchanted_hit", "magic_critical_hit", Version.VERSION_1_8_R1),
    /**
     * Water effect.
     */
    CURRENTDOWN("current_down", "current_down", "current_down", Version.VERSION_1_13_R1),
    /**
     * Smoke.
     */
    SMOKE_NORMAL("smoke", "smoke", "smoke", Version.VERSION_1_8_R1),
    /**
     * Large Smoke.
     */
    SMOKE_LARGE("largesmoke", "large_smoke", "large_smoke", Version.VERSION_1_8_R1),
    /**
     * Spell.
     */
    SPELL("spell", "effect", "spell", Version.VERSION_1_8_R1),
    /**
     * Instant Spell.
     */
    SPELL_INSTANT("instantSpell", "instant_effect", "instant_spell", Version.VERSION_1_8_R1),
    /**
     * Mob Spell.
     */
    SPELL_MOB("mobSpell", "entity_effect", "instant_spell", Version.VERSION_1_8_R1),
    /**
     * Mob Ambient Spell.
     */
    SPELL_MOB_AMBIENT("mobSpellAmbient", "mob_spell", "mob_spell", Version.VERSION_1_8_R1),
    /**
     * Witch Spell.
     */
    SPELL_WITCH("witchMagic", "witch", "witch_spell", Version.VERSION_1_8_R1),
    /**
     * Drip water.
     */
    DRIP_WATER("dripWater", "dripping_water", "drip_water", Version.VERSION_1_8_R1),
    /**
     * Drip lava.
     */
    DRIP_LAVA("dripLava", "dripping_lava", "drip_lava", Version.VERSION_1_8_R1),
    /**
     * Angry villager.
     */
    VILLAGER_ANGRY("angryVillager", "angry_villager", "angry_villager", Version.VERSION_1_8_R1),
    /**
     * Happy villager.
     */
    VILLAGER_HAPPY("happyVillager", "happy_villager", "happy_villager", Version.VERSION_1_8_R1),
    /**
     * Mycelium.
     */
    TOWN_AURA("townaura", "mycelium", "town_aura", Version.VERSION_1_8_R1),
    /**
     * Note..
     */
    NOTE("note", "note", "note", Version.VERSION_1_8_R1),
    /**
     * Portal.
     */
    PORTAL("portal", "portal", "portal", Version.VERSION_1_8_R1),
    /**
     * Nautilus.
     */
    NAUTILUS("nautilus", "nautilus", "nautilus", Version.VERSION_1_13_R1),
    /**
     * Enchantment.
     */
    ENCHANTMENT_TABLE("enchantmenttable", "enchant", "enchanting_glyphs", Version.VERSION_1_8_R1),
    /**
     * Flame.
     */
    FLAME("flame", "flame", "flame", Version.VERSION_1_8_R1),
    /**
     * Lava.
     */
    LAVA("lava", "lava", "lava", Version.VERSION_1_8_R1),
    /**
     * Squid.
     */
    SQUID_INK("squid_ink", "squid_ink", "squid_ink", Version.VERSION_1_13_R1),
    /**
     * Footstep.
     */
    FOOTSTEP("footstep", "footstep", "footstep", Version.VERSION_1_8_R1),
    /**
     * Cloud.
     */
    CLOUD("cloud", "cloud", "cloud", Version.VERSION_1_8_R1),
    /**
     * Redstone.
     */
    REDSTONE("reddust", "dust", "redstone_dust", Version.VERSION_1_8_R1),
    /**
     * Snowball.
     */
    SNOWBALL("snowballpoof", "item_snowball", "snowball", Version.VERSION_1_8_R1),
    /**
     * Snowshovel.
     */
    SNOW_SHOVEL("snowshovel", "snowshovel", "snow_shovel", Version.VERSION_1_8_R1),
    /**
     * Slime.
     */
    SLIME("slime", "item_slime", "slime", Version.VERSION_1_8_R1),
    /**
     * Heart.
     */
    HEART("heart", "heart", "heart", Version.VERSION_1_8_R1),
    /**
     * Barrier.
     */
    BARRIER("barrier", "barrier", "barrier", Version.VERSION_1_8_R1),
    /**
     * ItemCrack.
     */
    ITEM_CRACK("iconcrack", "item", "item_crack", Version.VERSION_1_8_R1),
    /**
     * BlockCrack.
     */
    BLOCK_CRACK("blockcrack", "block", "block_crack", Version.VERSION_1_8_R1),
    /**
     * Blockdust.
     */
    BLOCK_DUST("blockdust", "block", "block_dust", Version.VERSION_1_8_R1),
    /**
     * Rain.
     */
    WATER_DROP("droplet", "rain", "water_drop", Version.VERSION_1_8_R1),
    /**
     * Unknown.
     */
    TEM_TAKE("take", "take", "instant_spell", Version.VERSION_1_8_R1),
    /**
     * Guardian scare.
     */
    MOB_APPEARANCE("mobappearance", "elder_guardian", "guardian_appearance", Version.VERSION_1_8_R1),
    /**
     * Dragon Breath.
     */
    DRAGON_BREATH("dragonbreath", "dragon_breath", "dragon_breath", Version.VERSION_1_9_R1),
    /**
     * End rod.
     */
    END_ROD("endRod", "end_rod", "end_rod", Version.VERSION_1_9_R1),
    /**
     * Damage Indicator.
     */
    DAMAGE_INDICATOR("damageIndicator", "damage_indicator", "damage_indicator", Version.VERSION_1_9_R1),
    /**
     * Sweep Attack.
     */
    SWEEP_ATTACK("sweepAttack", "sweep_attack", "sweep_attack", Version.VERSION_1_9_R1),
    /**
     * Falling Dust.
     */
    FALLING_DUST("fallingdust", "falling_dust", "falling_dust", Version.VERSION_1_10_R1),
    /**
     * Totem.
     */
    TOTEM("totem", "totem_of_undying", "instant_spell", Version.VERSION_1_11_R1),
    /**
     * Spit.
     */
    SPIT("spit", "spit", "instant_spell", Version.VERSION_1_11_R1);
}