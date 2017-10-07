package com.github.shynixn.blockball.bukkit.logic.persistence.entity;


import com.github.shynixn.blockball.api.persistence.entity.Arena;
import com.github.shynixn.blockball.api.persistence.entity.BallMeta;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.MemorySection;
import org.bukkit.entity.Player;

import java.util.*;

public class ArenaEntity2 extends SelectedArea implements Arena {
    private static final String[] A = new String[0];

    private String name;
    private GoalEntity redGoal;
    private GoalEntity blueGoal;
    private SLocation ballSpawnLocation;
    private BallMetaEntity2 properties = new BallMetaEntity2();
    private TeamMetaEntity properties2 = new TeamMetaEntity();
    private EventMetaEntity properties3 = new EventMetaEntity();

    private LobbyMetaEntity lobbyMetaEntity = new LobbyMetaEntity();
    private GameType gameType = GameType.LOBBY;

    private boolean isEnabled = true;
    private String alias;

    private BoostItemHandler boostItemHandler;
    private List<String> bounce_types;

    private LightParticle playerTeamBlueHitParticle = new SParticle(ParticleEffect.REDSTONE, 1, 1, 0, 0, 1).setColors(0, 0, 255);
    private LightParticle playerTeamRedHitParticle = new SParticle(ParticleEffect.REDSTONE, 1, 1, 0, 0, 0).setColors(255, 0, 0);
    public ArenaEntity2() {
        super();
    }

    public ArenaEntity2(Map<String, Object> items, List<String> wallBouncing) throws Exception {
        super();
        this.setName(String.valueOf(items.get("id")));
        this.setCornerLocations(new SLocation(((MemorySection) items.get("corner-1")).getValues(true)).toLocation(), new SLocation(((MemorySection) items.get("corner-2")).getValues(true)).toLocation());
        this.alias = (String) items.get("name");
        this.isEnabled = (boolean) items.get("enabled");
        this.gameType = GameType.getGameTypeFromName((String) items.get("gamemode"));
        this.redGoal = new GoalEntity(((MemorySection) items.get("goals.red")).getValues(true));
        this.blueGoal = new GoalEntity(((MemorySection) items.get("goals.blue")).getValues(true));
        this.ballSpawnLocation = new SLocation(((MemorySection) items.get("ball.spawn")).getValues(true));
        this.properties = new BallMetaEntity2(((MemorySection) items.get("ball.properties")).getValues(true));
        this.lobbyMetaEntity = new LobbyMetaEntity(((MemorySection) items.get("lobby")).getValues(true));
        if (items.get("event") != null)
            this.properties3 = new EventMetaEntity(((MemorySection) items.get("event")).getValues(true));

        final Map<String, Object> data = ((MemorySection) items.get("properties")).getValues(true);
        this.properties2 = new TeamMetaEntity(data);

        this.bounce_types = new ArrayList<>(wallBouncing);
        if (data.containsKey("boost-items"))
            this.boostItemHandler = new ItemSpawner(((MemorySection) data.get("boost-items")).getValues(true));
    }

    public void setBallMeta(BallMetaEntity2 meta)
    {
        this.properties = meta;
    }

    @Override
    public void addBounceType(String type) {
        if (!this.getBounce_types().contains(type))
            this.getBounce_types().add(type);
    }

    @Override
    public void removeBounceType(String type) {
        if (this.getBounce_types().contains(type))
            this.getBounce_types().remove(type);
    }

    @Override
    public List<String> getBounceTypes() {
        return Arrays.asList(this.getBounce_types().toArray(A));
    }

    private List<String> getBounce_types() {
        if (this.bounce_types == null)
            this.bounce_types = new ArrayList<>();
        return this.bounce_types;
    }

    @Override
    public BallMeta getBallMeta() {
        return this.properties;
    }

    @Override
    public TeamMeta getTeamMeta() {
        return this.properties2;
    }

    @Override
    public LobbyMeta getLobbyMeta() {
        if (this.lobbyMetaEntity == null)
            this.lobbyMetaEntity = new LobbyMetaEntity();
        if (this.lobbyMetaEntity.reference == null)
            this.lobbyMetaEntity.reference = this;
        return this.lobbyMetaEntity;
    }

    @Override
    public boolean isEnabled() {
        return this.isEnabled;
    }

    @Override
    public String getAlias() {
        if (this.alias != null)
            return ChatColor.translateAlternateColorCodes('&', this.alias) + ChatColor.RESET;
        return null;
    }

    @Override
    public void setAlias(String name) {
        this.alias = name;
    }

    @Override
    public void setIsEnabled(boolean isEnabled) {
        this.isEnabled = isEnabled;
    }

    @Override
    public Location getBallSpawnLocation() {
        return this.ballSpawnLocation.getLocation();
    }

    @Override
    public void setBallSpawnLocation(Location ballSpawnLocation) {
        this.ballSpawnLocation = new SLocation(ballSpawnLocation);
    }

    @Override
    public GameType getGameType() {
        return this.gameType;
    }

    @Override
    public void setGameType(GameType type) {
        this.gameType = type;
    }

    @Override
    public boolean isValid() {
        return this.redGoal != null && this.blueGoal != null && this.ballSpawnLocation != null && this.getCenter() != null;
    }

    @Override
    public void setGoal(Team team, Location right, Location left) {
        if (team == Team.RED) {
            this.redGoal = new GoalEntity(right, left);
        } else {
            this.blueGoal = new GoalEntity(right, left);
        }
    }

    @Override
    public boolean isLocationInGoal(Location location) {
        return this.getTeamFromGoal(location) != null;
    }

    @Override
    public Team getTeamFromGoal(Location location) {
        if (this.redGoal.isLocationInArea(location))
            return Team.RED;
        else if (this.blueGoal.isLocationInArea(location))
            return Team.BLUE;
        return null;
    }

    void showPlayer(Player player) {
        if (this.getAlias() != null)
            player.sendMessage("Id: " + this.getId() + " Name: " + this.getAlias());
        else
            player.sendMessage("Id: " + this.getId());
        if (this.getCenter() != null && this.getCenter().getWorld() != null)
            player.sendMessage("Location: " + new SLocation(this.getCenter()).toString());
        else
            player.sendMessage("Location: " + "none");
        if (this.redGoal != null && this.redGoal.getCenter().getWorld() != null)
            player.sendMessage("Goal 1: " + new SLocation(this.redGoal.getCenter()).toString());
        else
            player.sendMessage("Goal 1: " + "none");
        if (this.blueGoal != null && this.blueGoal.getCenter().getWorld() != null)
            player.sendMessage("Goal 2: " + new SLocation(this.blueGoal.getCenter()).toString());
        else
            player.sendMessage("Goal 2: " + "none");
        if (this.ballSpawnLocation != null && this.ballSpawnLocation.getWorld() != null)
            player.sendMessage("Ballspawn: " + new SLocation(this.getBallSpawnLocation()).toString());
        else
            player.sendMessage("Ballspawn: " + "none");
    }

    @Override
    public Location getRandomFieldPosition(Random random) {
        boolean accepted;
        int maxRounds = 0;
        int x;
        int y;
        int z;
        do {
            accepted = true;
            x = this.getDownCornerLocation().getBlockX() + random.nextInt(this.getXWidth()) - 2;
            z = this.getDownCornerLocation().getBlockZ() + random.nextInt(this.getZWidth()) - 2;
            if (x < this.getDownCornerLocation().getBlockX())
                x += 4;
            if (z < this.getDownCornerLocation().getBlockZ())
                z += 4;
            for (y = this.getDownCornerLocation().getBlockY(); accepted && new Location(this.getDownCornerLocation().getWorld(), x, y, z).getBlock().getType() != Material.AIR; y++) {
                if (y > this.getUpCornerLocation().getBlockY()) {
                    accepted = false;
                }
            }
            maxRounds++;
            if (maxRounds > 10) {
                Bukkit.getServer().getConsoleSender().sendMessage(BlockBallPlugin.PREFIX_CONSOLE + ChatColor.RED + "Warning! The item spawner " + this.getId() + " takes too long to calculate a valid position!");
                throw new RuntimeException("Cannot calculate item position!");
            }
        } while (!accepted);
        return new Location(this.getDownCornerLocation().getWorld(), x, y, z);
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return this.name;
    }


    @Override
    public BoostItemHandler getBoostItemHandler() {
        if (this.boostItemHandler == null)
            this.boostItemHandler = new ItemSpawner();
        return this.boostItemHandler;
    }

    @Override
    public int getId() {
        return Integer.parseInt(this.getName());
    }

    @Override
    public EventMeta getEventMeta() {
        return this.properties3;
    }

    @Override
    public Map<String, Object> serialize() {
        final Map<String, Object> items = new LinkedHashMap<>();
        items.put("id", this.getId());
        items.put("name", this.alias);
        items.put("enabled", this.isEnabled());
        items.put("gamemode", this.gameType.name().toUpperCase());
        items.put("corner-1", this.getDownCornerLocation().serialize());
        items.put("corner-2", this.getUpCornerLocation().serialize());
        items.put("lobby", this.lobbyMetaEntity.serialize());
        items.put("goals.red", this.redGoal.serialize());
        items.put("goals.blue", this.blueGoal.serialize());
        items.put("ball.spawn", this.ballSpawnLocation.serialize());
        items.put("ball.properties", this.properties.serialize());
        items.put("event", this.properties3.serialize());
        final Map<String, Object> data = this.properties2.serialize();
        data.put("wall-bouncing", this.bounce_types);
        data.put("boost-items", this.getBoostItemHandler().serialize());
        items.put("properties", data);
        return items;
    }
}
