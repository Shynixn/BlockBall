package com.github.shynixn.blockball.bukkit.logic.business.entity;

/*
import com.github.shynixn.blockball.api.entities.Arena;
import com.github.shynixn.blockball.api.entities.Game;
import com.github.shynixn.blockball.api.entities.items.BoostItem;
import com.github.shynixn.blockball.api.entities.items.BoostItemHandler;
import com.github.shynixn.blockball.api.entities.items.Spawnrate;
import com.github.shynixn.blockball.bukkit.BlockBallPlugin;
import org.bukkit.Location;
import org.bukkit.configuration.MemorySection;
import org.bukkit.entity.Item;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;

public class ItemSpawner implements BoostItemHandler {
    private static final long serialVersionUID = 1L;
    private transient Random random;
    private transient int bumper = 20;
    private transient Map<Item, BoostItem> droppedItems;

    private Spawnrate rate = Spawnrate.NONE;
    private final List<BoostItem> items = new ArrayList<>();
    private final Plugin plugin = JavaPlugin.getPlugin(BlockBallPlugin.class);

    public ItemSpawner() {
        super();
    }

    public ItemSpawner(Map<String, Object> items) throws Exception {
        super();
        this.rate = Spawnrate.getSpawnrateFromName((String) items.get("spawn-rate"));
        for (int i = 0; i < 10000 && items.containsKey("items." + i); i++)
            this.items.add(new SpawnItem(((MemorySection) items.get("items." + i)).getValues(true)));
    }

    private Random getRandom() {
        if (this.random == null)
            this.random = new Random();
        return this.random;
    }

    private Map<Item, BoostItem> getLDroppedItems() {
        if (this.droppedItems == null)
            this.droppedItems = new HashMap<>();
        return this.droppedItems;
    }

    public void clearGroundItems() {
        for (final Item item : this.getLDroppedItems().keySet()) {
            if (!item.isDead())
                item.remove();
        }
        this.getLDroppedItems().clear();
    }

    @Override
    public void run(Game game) {
        final Arena arena = game.getArena();
        if (this.rate == Spawnrate.NONE)
            return;
        if (this.bumper <= 0) {
            if (game.getPlayers().isEmpty()) {
                this.clearGroundItems();
                this.bumper = 40;
                return;
            }
            for (final Item item : this.getLDroppedItems().keySet().toArray(new Item[this.getLDroppedItems().size()])) {
                if (item.isDead() || item.getTicksLived() > 1000) {
                    item.remove();
                    this.getLDroppedItems().remove(item);
                }
            }
            if (this.getRandom().nextInt(100) < this.rate.getSpawnChance()) {
                if (this.getLDroppedItems().size() < this.rate.getMaxAmount()) {
                    this.spawnRandomItem(arena);
                }
            }
            this.bumper = 40;
        }
        this.bumper--;
    }

    @Override
    public void clear() {
        this.rate = Spawnrate.NONE;
        this.items.clear();
    }

    @Override
    public void removeItem(Item item) {
        if (this.getLDroppedItems().containsKey(item))
            this.getLDroppedItems().remove(item);
    }

    @Override
    public Item[] getDroppedItems() {
        return this.getLDroppedItems().keySet().toArray(new Item[this.getLDroppedItems().size()]);
    }

    @Override
    public BoostItem getBoostFromItem(Item item) {
        if (this.getLDroppedItems().containsKey(item))
            return this.getLDroppedItems().get(item);
        return null;
    }

    private void spawnRandomItem(final Arena arena) {
        final BoostItem[] boostItems = this.items.toArray(new BoostItem[this.items.size()]);
        this.plugin.getServer().getScheduler().runTaskAsynchronously(this.plugin, () -> {
            final List<BoostItem> tmp = new ArrayList<>();
            for (final BoostItem item : boostItems) {
                for (int i = 0; i < item.getSpawnrate().getSpawnChance(); i++) {
                    tmp.add(item);
                }
            }
            Collections.shuffle(tmp);
            if (!tmp.isEmpty()) {
                final BoostItem item = tmp.get(0);
                ItemSpawner.this.plugin.getServer().getScheduler().runTask(ItemSpawner.this.plugin, () -> ItemSpawner.this.spawnItem(arena, item));
            }
        });
    }

    private void spawnItem(Arena arena, BoostItem item) {
        final Location location = arena.getRandomFieldPosition(this.getRandom());
        final Item item1 = location.getWorld().dropItem(location, item.generate());
        item1.setCustomNameVisible(true);
        item1.setCustomName(item.getDisplayName());
        this.getLDroppedItems().put(item1, item);
    }

    @Override
    public void setBoostItem(BoostItem boostItem) {
        if (!this.items.contains(boostItem))
            this.items.add(boostItem);
    }

    @Override
    public void removeBoostItem(BoostItem boostItem) {
        if (this.items.contains(boostItem))
            this.items.remove(boostItem);
    }

    @Override
    public List<BoostItem> getBoostItems() {
        return Collections.unmodifiableList(this.items);
    }

    @Override
    public Spawnrate getRate() {
        return this.rate;
    }

    @Override
    public void setSpawnRate(Spawnrate rate) {
        this.rate = rate;
    }

    public static BoostItem createBoostItem() {
        return new SpawnItem();
    }

    @Override
    public Map<String, Object> serialize() {
        final Map<String, Object> map = new HashMap<>();
        map.put("spawn-rate", this.rate.name().toUpperCase());
        for (int i = 0; i < this.items.size(); i++)
            map.put("items." + i, this.items.get(i).serialize());
        return map;
    }
}
*/