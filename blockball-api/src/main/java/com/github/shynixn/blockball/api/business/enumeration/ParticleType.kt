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
        val minecraftId_112: String) {

    /**
     * No Particle.
     */
    NONE("none", "none", "none"),
    /**
     * Explosion.
     */
    EXPLOSION_NORMAL("explode", "poof", "explosion"),
    /**
     * Large explosion.
     */
    EXPLOSION_LARGE("largeexplode", "explosion", "large_explosion"),
    /**
     * Huge explosion.
     */
    EXPLOSION_HUGE("hugeexplosion", "explosion_emitter", "huge_explosion"),
    /**
     * Firework.
     */
    FIREWORKS_SPARK("fireworksSpark", "firework", "fireworks_spark"),
    /**
     * Water Bubble simple.
     */
    WATER_BUBBLE("bubble", "bubble", "water_bubble"),
    /**
     * Water Bubble up.
     */
    WATER_BUBBLE_UP("bubble_column_up", "bubble_column_up", "bubble_column_up"),
    /**
     * Water Bubble pop.
     */
    WATER_BUBBLE_POP("bubble_pop", "bubble_pop", "bubble_pop"),
    /**
     * Water Splash.
     */
    WATER_SPLASH("splash", "splash", "water_splash"),
    /**
     * Fishing effect.
     */
    WATER_WAKE("wake", "fishing", "water_wake"),
    /**
     * Underwater bubbles.
     */
    SUSPENDED("suspended", "underwater", "suspended"),
    /**
     * Unused effect.
     */
    SUSPENDED_DEPTH("depthsuspend", "depthsuspend", "suspended_depth"),
    /**
     * Critical damage.
     */
    CRIT("crit", "crit", "critical_hit"),
    /**
     * Critical magical damage.
     */
    CRIT_MAGIC("magicCrit", "enchanted_hit", "magic_critical_hit"),
    /**
     * Water effect.
     */
    CURRENTDOWN("current_down", "current_down", "current_down"),
    /**
     * Smoke.
     */
    SMOKE_NORMAL("smoke", "smoke", "smoke"),
    /**
     * Large Smoke.
     */
    SMOKE_LARGE("largesmoke", "large_smoke", "large_smoke"),
    /**
     * Spell.
     */
    SPELL("spell", "effect", "spell"),
    /**
     * Instant Spell.
     */
    SPELL_INSTANT("instantSpell", "instant_effect", "instant_spell"),
    /**
     * Mob Spell.
     */
    SPELL_MOB("mobSpell", "entity_effect", "instant_spell"),
    /**
     * Mob Ambient Spell.
     */
    SPELL_MOB_AMBIENT("mobSpellAmbient", "mob_spell", "mob_spell"),
    /**
     * Witch Spell.
     */
    SPELL_WITCH("witchMagic", "witch", "witch_spell"),
    /**
     * Drip water.
     */
    DRIP_WATER("dripWater", "dripping_water", "drip_water"),
    /**
     * Drip lava.
     */
    DRIP_LAVA("dripLava", "dripping_lava", "drip_lava"),
    /**
     * Angry villager.
     */
    VILLAGER_ANGRY("angryVillager", "angry_villager", "angry_villager"),
    /**
     * Happy villager.
     */
    VILLAGER_HAPPY("happyVillager", "happy_villager", "happy_villager"),
    /**
     * Mycelium.
     */
    TOWN_AURA("townaura", "mycelium", "town_aura"),
    /**
     * Note..
     */
    NOTE("note", "note", "note"),
    /**
     * Portal.
     */
    PORTAL("portal", "portal", "portal"),
    /**
     * Nautilus.
     */
    NAUTILUS("nautilus", "nautilus", "nautilus"),
    /**
     * Enchantment.
     */
    ENCHANTMENT_TABLE("enchantmenttable", "enchant", "enchanting_glyphs"),
    /**
     * Flame.
     */
    FLAME("flame", "flame", "flame"),
    /**
     * Lava.
     */
    LAVA("lava", "lava", "lava"),
    /**
     * Squid.
     */
    SQUID_INK("squid_ink", "squid_ink", "squid_ink"),
    /**
     * Footstep.
     */
    FOOTSTEP("footstep", "footstep", "footstep"),
    /**
     * Cloud.
     */
    CLOUD("cloud", "cloud", "cloud"),
    /**
     * Redstone.
     */
    REDSTONE("reddust", "dust", "redstone_dust"),
    /**
     * Snowball.
     */
    SNOWBALL("snowballpoof", "item_snowball", "snowball"),
    /**
     * Snowshovel.
     */
    SNOW_SHOVEL("snowshovel", "snowshovel", "snow_shovel"),
    /**
     * Slime.
     */
    SLIME("slime", "item_slime", "slime"),
    /**
     * Heart.
     */
    HEART("heart", "heart", "heart"),
    /**
     * Barrier.
     */
    BARRIER("barrier", "barrier", "barrier"),
    /**
     * ItemCrack.
     */
    ITEM_CRACK("iconcrack", "item", "item_crack"),
    /**
     * BlockCrack.
     */
    BLOCK_CRACK("blockcrack", "block", "block_crack"),
    /**
     * Blockdust.
     */
    BLOCK_DUST("blockdust", "block", "block_dust"),
    /**
     * Rain.
     */
    WATER_DROP("droplet", "rain", "water_drop"),
    /**
     * Unknown.
     */
    TEM_TAKE("take", "take", "instant_spell"),
    /**
     * Guardian scare.
     */
    MOB_APPEARANCE("mobappearance", "elder_guardian", "guardian_appearance"),
    /**
     * Dragon Breath.
     */
    DRAGON_BREATH("dragonbreath", "dragon_breath", "dragon_breath"),
    /**
     * End rod.
     */
    END_ROD("endRod", "end_rod", "end_rod"),
    /**
     * Damage Indicator.
     */
    DAMAGE_INDICATOR("damageIndicator", "damage_indicator", "damage_indicator"),
    /**
     * Sweep Attack.
     */
    SWEEP_ATTACK("sweepAttack", "sweep_attack", "sweep_attack"),
    /**
     * Falling Dust.
     */
    FALLING_DUST("fallingdust", "falling_dust", "falling_dust"),
    /**
     * Totem.
     */
    TOTEM("totem", "totem_of_undying", "instant_spell"),
    /**
     * Spit.
     */
    SPIT("spit", "spit", "instant_spell");
}
