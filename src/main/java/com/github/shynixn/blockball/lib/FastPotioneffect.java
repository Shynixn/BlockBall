package com.github.shynixn.blockball.lib;

import org.bukkit.entity.LivingEntity;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.LinkedHashMap;
import java.util.Map;

public class FastPotioneffect implements LightPotioneffect {
    private static final long serialVersionUID = 1L;
    private int type = PotionEffectType.ABSORPTION.getId();
    private int duration;
    private int strength;
    private boolean ambient;
    private boolean particles;

    public FastPotioneffect() {
        super();
    }

    public FastPotioneffect(Map<String, Object> items) throws Exception {
        super();
        this.type = (int) items.get("type");
        this.duration = (int) items.get("duration");
        this.strength = (int) items.get("strength");
        this.ambient = (boolean) items.get("ambient");
        this.particles = (boolean) items.get("particles");
    }

    @Override
    public LightPotioneffect setType(int type) {
        this.type = type;
        return this;
    }

    @Override
    public LightPotioneffect setSeconds(int seconds) {
        this.duration = seconds * 20;
        return this;
    }

    @Override
    public LightPotioneffect setTicks(int ticks) {
        this.duration = ticks;
        return this;
    }

    @Override
    public LightPotioneffect setStrength(int strength) {
        this.strength = strength;
        return this;
    }

    @Override
    public LightPotioneffect setAmbientVisible(boolean ambient) {
        this.ambient = ambient;
        return this;
    }

    @Override
    public LightPotioneffect setParticleVisible(boolean visible) {
        this.particles = visible;
        return this;
    }

    @Override
    public int getType() {
        return this.type;
    }

    @Override
    public int getDuration() {
        return this.duration;
    }

    @Override
    public int getStrength() {
        return this.strength;
    }

    @Override
    public boolean isAmbientVisible() {
        return this.ambient;
    }

    @Override
    public boolean isParticleVisible() {
        return this.particles;
    }

    @Override
    public void apply(LivingEntity entity) {
        if (this.getFromId() != null && entity.hasPotionEffect(this.getFromId())) {
            entity.removePotionEffect(this.getFromId());
        }
        entity.addPotionEffect(new PotionEffect(this.getFromId(), this.duration, this.strength, this.ambient));
    }

    private PotionEffectType getFromId() {
        return PotionEffectType.getById(this.type);
    }

    private static Map<String, PotionEffectType> getPos() {
        return (Map<String, PotionEffectType>) ReflectionLib.getValueFromField("byName", PotionEffectType.class);
    }

    public static String getPotionEffectsText() {
        String s = "";
        for (final String a : getPos().keySet()) {
            if (s.isEmpty())
                s += a;
            else
                s += ", " + a;
        }
        return s;
    }

    public static PotionEffectType getPotionEffectFromName(String name) {
        for (final String a : getPos().keySet()) {
            if (a.equalsIgnoreCase(name))
                return getPos().get(a);
        }
        return null;
    }

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
}
