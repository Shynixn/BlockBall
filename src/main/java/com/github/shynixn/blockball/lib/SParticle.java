package com.github.shynixn.blockball.lib;

import java.io.Serializable;
import java.util.*;
import java.util.logging.Level;

import com.github.shynixn.blockball.business.Config;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class SParticle implements Serializable, LightParticle {
    private static final long serialVersionUID = 1L;
    private ParticleEffect effect;
    private int amount;
    private double x;
    private double y;
    private double z;
    private double speed;
    private boolean isEnabled = true;

    //not common
    private Integer blue;
    private Integer red;
    private Integer green;

    private Material material;
    private Byte data;

    public SParticle(Map<String, Object> items) throws Exception {
        super();
        this.effect = SParticle.getParticleEffectFromName((String) items.get("effect"));
        this.isEnabled = (boolean) items.get("enabled");
        this.amount = (int) items.get("amount");
        this.speed = (double) items.get("speed");
        this.x = (double) items.get("size.x");
        this.y = (double) items.get("size.y");
        this.z = (double) items.get("size.z");
        this.blue = (Integer) items.get("color.blue");
        this.red = (Integer) items.get("color.red");
        this.green = (Integer) items.get("color.green");
        if (items.containsKey("block.material"))
            this.material = Material.getMaterial((Integer) items.get("block.material"));
        if (items.containsKey("block.damage"))
            this.data = (Byte) items.get("block.damage");
    }

    public SParticle(ParticleEffect effect, int amount, double speed, double x, double y, double z) {
        super();
        this.effect = effect;
        this.amount = amount;
        this.x = x;
        this.y = y;
        this.z = z;
        this.speed = speed;
        this.blue = null;
        this.red = null;
        this.green = null;
        this.material = null;
        this.data = null;
    }

    @Override
    public LightParticle copy() {
        final SParticle particle = new SParticle();
        particle.effect = this.effect;
        particle.amount = this.amount;
        particle.x = this.x;
        particle.y = this.y;
        particle.z = this.z;
        particle.speed = this.speed;
        particle.blue = this.blue;
        particle.red = this.red;
        particle.green = this.green;
        particle.material = this.material;
        particle.data = this.data;
        return particle;
    }

    @Override
    public SParticle setColors(int red, int green, int blue) {
        this.blue = blue;
        this.red = red;
        this.green = green;
        return this;
    }

    @Override
    public SParticle setNoteColor(int color) {
        if (color > 20 || color < 0)
            color = 5;
        this.red = color;
        return this;
    }

    @Override
    public boolean isEnabled() {
        return this.isEnabled;
    }

    public void setIsEnabled(boolean isEnabled) {
        this.isEnabled = isEnabled;
    }

    @Override
    public int getAmount() {
        return this.amount;
    }

    @Override
    public void setAmount(int amount) {
        this.amount = amount;
    }

    @Override
    public double getX() {
        return this.x;
    }

    @Override
    public void setX(double x) {
        this.x = x;
    }

    @Override
    public double getY() {
        return this.y;
    }

    @Override
    public void setY(double y) {
        this.y = y;
    }

    @Override
    public double getZ() {
        return this.z;
    }

    @Override
    public void setZ(double z) {
        this.z = z;
    }

    @Override
    public double getSpeed() {
        return this.speed;
    }

    @Override
    public void setSpeed(double speed) {
        this.speed = speed;
    }

    @Override
    public ParticleEffect getEffect() {
        return this.effect;
    }

    @Override
    public void setEffect(ParticleEffect effect) {
        this.effect = effect;
        this.blue = null;
        this.red = null;
        this.green = null;
        this.material = null;
        this.data = null;
    }

    @Override
    public Integer getBlue() {
        return this.blue;
    }

    @Override
    public void setBlue(Integer blue) {
        this.blue = blue;
    }

    @Override
    public void setEnabled(boolean enabled) {
        this.isEnabled = enabled;
    }

    @Override
    public Integer getRed() {
        return this.red;
    }

    @Override
    public void setRed(Integer red) {
        this.red = red;
    }

    @Override
    public Integer getGreen() {
        return this.green;
    }

    @Override
    public void setGreen(Integer green) {
        this.green = green;
    }

    @Override
    public Material getMaterial() {
        return this.material;
    }

    @Override
    public void setMaterial(Material material) {
        this.material = material;
    }

    @Override
    public Byte getData() {
        return this.data;
    }

    @Override
    public void setData(Byte data) {
        this.data = data;
    }

    public SParticle() {
        super();
    }

    @Override
    public void play(Location location) {
        this.play(location, location.getWorld().getPlayers().toArray(new Player[location.getWorld().getPlayers().size()]));
    }

    private List<Player> getAllowedPlayers(Player[] players) {
        final List<Player> players1 = new ArrayList<>();
        for (final Player player : players) {
            if (Config.getInstance().isParticleVisibleForAll() || (Config.getInstance().getParticlePermission() != null && player.hasPermission(Config.getInstance().getParticlePermission())))
                players1.add(player);
        }
        return players1;
    }

    @Override
    public void play(final Location location, final Player... players) {
        AsyncRunnable.toAsynchroneThread(new AsyncRunnable() {
            @Override
            public void run() {
                if (SParticle.this.isEnabled()) {
                    try {
                        if (SParticle.this.effect == ParticleEffect.SPELL_MOB || SParticle.this.effect == ParticleEffect.SPELL_MOB_AMBIENT || SParticle.this.effect == ParticleEffect.REDSTONE)
                            SParticle.this.effect.display(new ParticleEffect.OrdinaryColor(SParticle.this.red, SParticle.this.green, SParticle.this.blue), location, SParticle.this.getAllowedPlayers(players));
                        else if (SParticle.this.effect == ParticleEffect.NOTE)
                            SParticle.this.effect.display(new ParticleEffect.NoteColor(SParticle.this.red), location, SParticle.this.getAllowedPlayers(players));
                        else if (SParticle.this.effect == ParticleEffect.BLOCK_CRACK || SParticle.this.effect == ParticleEffect.BLOCK_DUST)
                            SParticle.this.effect.display(new ParticleEffect.BlockData(SParticle.this.material, SParticle.this.data), (float) SParticle.this.x, (float) SParticle.this.y, (float) SParticle.this.z, (float) SParticle.this.speed, SParticle.this.amount, location, SParticle.this.getAllowedPlayers(players));
                        else if (SParticle.this.effect == ParticleEffect.ITEM_CRACK)
                            SParticle.this.effect.display(new ParticleEffect.ItemData(SParticle.this.material, SParticle.this.data), (float) SParticle.this.x, (float) SParticle.this.y, (float) SParticle.this.z, (float) SParticle.this.speed, SParticle.this.amount, location, SParticle.this.getAllowedPlayers(players));
                        else
                            SParticle.this.effect.display((float) SParticle.this.x, (float) SParticle.this.y, (float) SParticle.this.z, (float) SParticle.this.speed, SParticle.this.amount, location, SParticle.this.getAllowedPlayers(players));
                    } catch (final Exception e) {
                        Bukkit.getLogger().log(Level.WARNING, "Cannot execute particle effect. Configuration contains an error!", e);
                    }
                }
            }
        });
    }

    @SuppressWarnings("deprecation")
    @Override
    public String toString() {
        String sdefault = "Name:" + this.effect.getName() + " Amount:" + this.amount + " Speed:" + this.speed + " OffsetX: " + this.x + " OffsetY: " + this.y + " OffsetZ" + this.z;
        if (this.blue != null)
            sdefault += " Red:" + this.red + " Green:" + this.green + " Blue:" + this.blue;
        if (this.material != null)
            sdefault += " Material:" + this.material.getId() + " Data:" + this.data;
        return sdefault;
    }

    @Override
    public boolean isColorParticleEffect() {
        return this.effect == ParticleEffect.SPELL_MOB || this.effect == ParticleEffect.SPELL_MOB_AMBIENT || this.effect == ParticleEffect.REDSTONE || this.effect == ParticleEffect.NOTE;
    }

    @Override
    public boolean isNoteParticleEffect() {
        return this.effect == ParticleEffect.NOTE;
    }

    @Override
    public boolean isMaterialParticleEffect() {
        return this.effect == ParticleEffect.BLOCK_CRACK || this.effect == ParticleEffect.BLOCK_DUST || this.effect == ParticleEffect.ITEM_CRACK;
    }

    public static String getParticlesText() {
        String s = "";
        for (final ParticleEffect particleEffect : ParticleEffect.values()) {
            if (s.isEmpty())
                s += particleEffect.getName();
            else
                s += ", " + particleEffect.getName();
        }
        return s;
    }

    public static ParticleEffect getParticleEffectFromName(String name) {
        for (final ParticleEffect particleEffect : ParticleEffect.values())
            if (particleEffect.getName().equalsIgnoreCase(name))
                return particleEffect;
        return null;
    }

    @Override
    public Map<String, Object> serialize() {
        final Map<String, Object> map = new LinkedHashMap<>();
        map.put("effect", this.effect.getName().toUpperCase());
        map.put("enabled", this.isEnabled);
        map.put("amount", this.amount);
        map.put("speed", this.speed);
        final Map<String, Object> tmp3 = new LinkedHashMap<>();
        tmp3.put("x", this.x);
        tmp3.put("y", this.y);
        tmp3.put("z", this.z);
        map.put("size", tmp3);
        final Map<String, Object> tmp = new LinkedHashMap<>();
        tmp.put("blue", this.blue);
        tmp.put("red", this.red);
        tmp.put("green", this.green);
        map.put("color", tmp);
        final Map<String, Object> tmp2 = new LinkedHashMap<>();
        if (this.material != null)
            tmp2.put("material", this.material.getId());
        else
            tmp2.put("material", null);
        tmp2.put("damage", this.data);
        map.put("block", tmp2);
        return map;
    }
}
