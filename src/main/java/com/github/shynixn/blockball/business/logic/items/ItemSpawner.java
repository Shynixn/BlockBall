package com.github.shynixn.blockball.business.logic.items;

import com.github.shynixn.blockball.api.entities.items.BoostItemHandler;
import com.github.shynixn.blockball.api.entities.items.Spawnrate;
import com.github.shynixn.blockball.api.entities.Arena;
import com.github.shynixn.blockball.api.entities.Game;
import com.github.shynixn.blockball.api.entities.items.BoostItem;
import com.github.shynixn.blockball.lib.AsyncRunnable;
import org.bukkit.Location;
import org.bukkit.configuration.MemorySection;
import org.bukkit.entity.Item;

import java.util.*;

/**
 * Created by Shynixn
 */
public class ItemSpawner implements BoostItemHandler {
    private static final long serialVersionUID = 1L;
    private transient Random random;
    private transient int secondbumer = 20;
    private transient Map<Item, BoostItem> droppedItems;

    private Spawnrate rate = Spawnrate.NONE;
    private List<BoostItem> items = new ArrayList<>();

    public ItemSpawner() {
    }

    public ItemSpawner(Map<String, Object> items) throws Exception {
        this.rate = Spawnrate.getSpawnrateFromName((String) items.get("spawn-rate"));
        for (int i = 0; i < 10000 && items.containsKey("items." + i); i++)
            this.items.add(new SpawnItem(((MemorySection) items.get("items." + i)).getValues(true)));
    }

    private Random getRandom() {
        if (random == null)
            random = new Random();
        return random;
    }

    private Map<Item, BoostItem> getLDroppedItems() {
        if (droppedItems == null)
            droppedItems = new HashMap<>();
        return droppedItems;
    }

    public void clearGroundItems() {
        for (Item item : getLDroppedItems().keySet()) {
            if (!item.isDead())
                item.remove();
        }
        getLDroppedItems().clear();
    }

    public void run(Game game) {
        Arena arena = game.getArena();
        if (rate == Spawnrate.NONE)
            return;
        if (secondbumer <= 0) {
            if (game.getPlayers().size() == 0) {
                clearGroundItems();
                secondbumer = 40;
                return;
            }
            for (Item item : getLDroppedItems().keySet().toArray(new Item[0])) {
                if (item.isDead() || item.getTicksLived() > 1000) {
                    item.remove();
                    getLDroppedItems().remove(item);
                }
            }
            if (getRandom().nextInt(100) < rate.getSpawnChance()) {
                if (getLDroppedItems().size() < rate.getMaxAmount()) {
                    spawnRandomItem(arena);
                }
            }
            secondbumer = 40;
        }
        secondbumer--;
    }

    public void clear() {
        rate = Spawnrate.NONE;
        items.clear();
    }

    public void removeItem(Item item) {
        if (getLDroppedItems().containsKey(item))
            getLDroppedItems().remove(item);
    }

    public Item[] getDroppedItems() {
        return getLDroppedItems().keySet().toArray(new Item[0]);
    }

    public BoostItem getBoostFromItem(Item item) {
        if (getLDroppedItems().containsKey(item))
            return getLDroppedItems().get(item);
        return null;
    }

    private void spawnRandomItem(final Arena arena) {
        final BoostItem[] boostItems = items.toArray(new BoostItem[0]);
        AsyncRunnable.toAsynchroneThread(new AsyncRunnable() {
            @Override
            public void run() {
                List<BoostItem> tmp = new ArrayList<>();
                for (BoostItem item : boostItems) {
                    for (int i = 0; i < item.getSpawnrate().getSpawnChance(); i++) {
                        tmp.add(item);
                    }
                }
                Collections.shuffle(tmp);
                if (tmp.size() > 0) {
                    final BoostItem item = tmp.get(0);
                    AsyncRunnable.toSynchroneThread(new AsyncRunnable() {
                        @Override
                        public void run() {
                            spawnItem(arena, item);
                        }
                    });
                }
            }
        });
    }

    private void spawnItem(Arena arena, BoostItem item) {
        Location location = arena.getRandomFieldPosition(getRandom());
        Item item1 = location.getWorld().dropItem(location, item.generate());
        item1.setCustomNameVisible(true);
        item1.setCustomName(item.getDisplayName());
        getLDroppedItems().put(item1, item);
    }

    public void setBoostItem(BoostItem boostItem) {
        if (!items.contains(boostItem))
            items.add(boostItem);
    }

    public void removeBoostItem(BoostItem boostItem) {
        if (items.contains(boostItem))
            items.remove(boostItem);
    }

    public List<BoostItem> getBoostItems() {
        return Collections.unmodifiableList(this.items);
    }

    public Spawnrate getRate() {
        return rate;
    }

    public void setSpawnRate(Spawnrate rate) {
        this.rate = rate;
    }

    public static BoostItem createBoostItem() {
        return new SpawnItem();
    }

    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> map = new HashMap<>();
        map.put("spawn-rate", rate.name().toUpperCase());
        for (int i = 0; i < items.size(); i++)
            map.put("items." + i, items.get(i).serialize());
        return map;
    }
}
