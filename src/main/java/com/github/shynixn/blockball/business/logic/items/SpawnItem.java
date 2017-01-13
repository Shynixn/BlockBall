package com.github.shynixn.blockball.business.logic.items;

import com.github.shynixn.blockball.api.entities.items.Spawnrate;
import com.github.shynixn.blockball.lib.FastPotioneffect;
import com.github.shynixn.blockball.lib.SSKulls;
import com.github.shynixn.blockball.api.entities.items.BoostItem;
import com.github.shynixn.blockball.lib.LightPotioneffect;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.MemorySection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Shynixn
 */
class SpawnItem implements BoostItem {
    private static final long serialVersionUID = 1L;
    private int id = 260;
    private int damage;
    private String owner;
    private Spawnrate spawnrate = Spawnrate.MEDIUM;
    private String displayName;
    private Map<Integer, LightPotioneffect> potioneffectList = new HashMap<>();

    SpawnItem() {
    }

    SpawnItem(Map<String, Object> items) throws Exception {
        this.id = (int) items.get("id");
        this.damage = (int) items.get("damage");
        this.owner = (String) items.get("owner");
        this.displayName = (String) items.get("name");
        this.spawnrate = Spawnrate.getSpawnrateFromName((String) items.get("rate"));
        for (PotionEffectType potionEffectType : PotionEffectType.values()) {
            if (potionEffectType != null && items.containsKey("potioneffects." + potionEffectType.getId())) {
                this.potioneffectList.put(potionEffectType.getId(), new FastPotioneffect(((MemorySection) items.get("potioneffects." + potionEffectType.getId())).getValues(true)));
            }
        }
    }

    public ItemStack generate() {
        ItemStack itemStack = new ItemStack(Material.getMaterial(id), 1, (short) damage);
        if (id == Material.SKULL_ITEM.getId() && damage == 3) {
            if (owner != null && owner.contains("textures.minecraft")) {
                itemStack = SSKulls.activateHeadByURL(owner, itemStack);
            } else if (owner != null) {
                itemStack = SSKulls.activateHeadByName(owner, itemStack);
            }
        }
        return itemStack;
    }


    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = ChatColor.translateAlternateColorCodes('&', displayName);
    }

    @Override
    public void apply(Player player) {
        for (LightPotioneffect lightPotioneffect : potioneffectList.values()) {
            lightPotioneffect.apply(player);
        }
    }

    @Override
    public void setPotionEffect(LightPotioneffect lightPotioneffect) {
        potioneffectList.put(lightPotioneffect.getType(), lightPotioneffect);
    }

    @Override
    public void removePotionEffect(PotionEffectType type) {
        if (potioneffectList.containsKey(type.getId()))
            potioneffectList.remove(type.getId());
    }

    @Override
    public LightPotioneffect getPotionEffect(PotionEffectType type) {
        if (potioneffectList.containsKey(type.getId()))
            return potioneffectList.get(type.getId());
        return null;
    }

    @Override
    public LightPotioneffect[] getPotionEffects() {
        return potioneffectList.values().toArray(new LightPotioneffect[0]);
    }

    public Spawnrate getSpawnrate() {
        return spawnrate;
    }

    public void setSpawnrate(Spawnrate spawnrate) {
        this.spawnrate = spawnrate;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getDamage() {
        return damage;
    }

    public void setDamage(int damage) {
        this.damage = damage;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> map = new HashMap<>();
        map.put("id", id);
        map.put("damage", damage);
        map.put("owner", owner);
        map.put("name", displayName);
        map.put("rate", spawnrate.name().toUpperCase());
        for (Integer integer : potioneffectList.keySet()) {
            map.put("potioneffects." + integer, potioneffectList.get(integer).serialize());
        }
        return map;
    }
}
