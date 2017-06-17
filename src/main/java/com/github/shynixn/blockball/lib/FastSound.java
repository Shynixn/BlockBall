package com.github.shynixn.blockball.lib;

import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.LinkedHashMap;
import java.util.Map;

public class FastSound implements LightSound {
    private static final long serialVersionUID = 1L;
    private String sound;
    private double volume = 1.0;
    private double pitch = 1.0;

    public FastSound(String sound, double volume, double pitch) {
        super();
        this.sound = sound;
        this.volume = volume;
        this.pitch = pitch;
    }

    @Override
    public FastSound copy() {
        final FastSound sound = new FastSound();
        sound.sound = this.sound;
        sound.volume = this.volume;
        sound.pitch = this.pitch;
        return sound;
    }

    public FastSound(Map<String, Object> items) throws Exception {
        super();
        this.sound = (String) items.get("name");
        this.volume = (double) items.get("volume");
        this.pitch = (double) items.get("pitch");
    }

    public FastSound(String sound, double pitch) {
        this(sound, 1.0, pitch);
    }

    public FastSound(String sound) {
        this(sound, 1.0);
    }

    public FastSound() {
        super();
    }

    @Override
    public void play(Location location) throws InterPreter19Exception {
        this.play(location, location.getWorld().getPlayers().toArray(new Player[location.getWorld().getPlayers().size()]));
    }

    @Override
    public void play(Location location, Player... players) throws InterPreter19Exception {
        try {
            this.changeSound();
            for (final Player player : players) {
                player.playSound(location, getSoundFromName(this.sound), (float) this.volume, (float) this.pitch);
            }
        } catch (final Exception ex) {
            throw new InterPreter19Exception("Cannot parse sound!");
        }
    }

    @Override
    public void play(Player... players) throws InterPreter19Exception {
        try {
            this.changeSound();
            for (final Player player : players) {
                player.playSound(player.getLocation(), getSoundFromName(this.sound), (float) this.volume, (float) this.pitch);
            }
        } catch (final Exception ex) {
            throw new InterPreter19Exception("Cannot parse sound!");
        }
    }

    private void changeSound() {
        if (ReflectionLib.getServerVersion().equals("v1_9_R1")
                || ReflectionLib.getServerVersion().equals("v1_9_R2")
                || ReflectionLib.getServerVersion().equals("v1_10_R1")
                || ReflectionLib.getServerVersion().equals("v1_11_R1")
                || ReflectionLib.getServerVersion().equals("v1_12_R1")) {
            if (this.sound.equals("ZOMBIE_WOOD"))
                this.sound = "ENTITY_ZOMBIE_ATTACK_DOOR_WOOD";
            if (this.sound.equals("GHAST_FIREBALL"))
                this.sound = "ENTITY_GHAST_SHOOT";
            if (this.sound.equals("NOTE_BASS"))
                this.sound = "BLOCK_NOTE_BASS";
            if (this.sound.equals("NOTE_PLING"))
                this.sound = "BLOCK_NOTE_PLING";
        }
    }

    @Override
    public String getSound() {
        return this.sound;
    }

    @Override
    public void setSound(String sound) {
        this.sound = sound;
    }

    @Override
    public double getVolume() {
        return this.volume;
    }

    @Override
    public void setVolume(double volume) {
        this.volume = volume;
    }

    @Override
    public double getPitch() {
        return this.pitch;
    }

    @Override
    public String toString() {
        return "Name: " + this.sound + " Volume:" + this.volume + " Pitch:" + this.pitch;
    }

    @Override
    public void setPitch(double pitch) {
        this.pitch = pitch;
    }

    @Override
    public FastSound toFastSound() {
        return this;
    }

    public static String getSoundsText() {
        String s = "";
        for (final Sound sound : Sound.values()) {
            if (s.isEmpty())
                s += sound.name().toLowerCase();
            else
                s += ", " + sound.name().toLowerCase();
        }
        return s;
    }

    public static Sound getSoundFromName(String name) {
        for (final Sound sound : Sound.values()) {
            if (sound.name().equalsIgnoreCase(name))
                return sound;
        }
        return null;
    }

    @Override
    public Map<String, Object> serialize() {
        final Map<String, Object> map = new LinkedHashMap<>();
        map.put("name", this.sound);
        map.put("volume", this.volume);
        map.put("pitch", this.pitch);
        return map;
    }
}
