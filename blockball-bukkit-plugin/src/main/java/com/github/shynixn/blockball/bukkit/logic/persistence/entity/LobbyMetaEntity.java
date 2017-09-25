package com.github.shynixn.blockball.bukkit.logic.persistence.entity;

import com.github.shynixn.blockball.api.entities.IPosition;
import com.github.shynixn.blockball.lib.SFileUtils;
import com.github.shynixn.blockball.api.entities.Arena;
import com.github.shynixn.blockball.api.entities.LobbyMeta;
import com.github.shynixn.blockball.lib.SLocation;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.configuration.MemorySection;

import java.util.*;

class LobbyMetaEntity implements LobbyMeta {
    private IPosition lobbySpawn;
    private final List<IPosition> signLocations = new ArrayList<>();
    private List<IPosition> redTeamSignLocations = new ArrayList<>();
    private List<IPosition> blueTeamSignLocations = new ArrayList<>();
    private List<IPosition> leaveSignLocations = new ArrayList<>();
    private int countdown = 30;
    private int gameTime = 300;
    private String gameTitleMessage = ChatColor.GOLD + "Game";
    private String gameSubTitleMessage = ChatColor.YELLOW + "Starting in :countdown seconds.";

    transient Arena reference;

    LobbyMetaEntity() {
        super();
    }

    LobbyMetaEntity(Map<String, Object> items) throws Exception {
        super();
        if (items.get("spawnpoint") != null)
            this.lobbySpawn = new SLocation(((MemorySection) items.get("spawnpoint")).getValues(true));
        this.gameTime = (int) items.get("gameduration");
        this.countdown = (int) items.get("lobbyduration");
        for (int i = 0; i < 10000 && items.containsKey("signs.join." + i); i++)
            this.signLocations.add(new SLocation(((MemorySection) items.get("signs.join." + i)).getValues(true)));
        for (int i = 0; i < 10000 && items.containsKey("signs.leave." + i); i++)
            this.leaveSignLocations.add(new SLocation(((MemorySection) items.get("signs.leave." + i)).getValues(true)));
        for (int i = 0; i < 10000 && items.containsKey("signs.red." + i); i++)
            this.redTeamSignLocations.add(new SLocation(((MemorySection) items.get("signs.red." + i)).getValues(true)));
        for (int i = 0; i < 10000 && items.containsKey("signs.blue." + i); i++)
            this.blueTeamSignLocations.add(new SLocation(((MemorySection) items.get("signs.blue." + i)).getValues(true)));
        this.gameTitleMessage = (String) items.get("messages.countdown-title");
        this.gameSubTitleMessage = (String) items.get("messages.countdown-subtitle");
    }

    /**
     * Returns the positions of each sign
     *
     * @return positions
     */
    @Override
    public List<IPosition> getSignPositions() {
        return Collections.unmodifiableList(this.signLocations);
    }

    /**
     * Removes the sign-position
     *
     * @param position position
     */
    @Override
    public void removeSignPosition(IPosition position) {
        if (this.signLocations.contains(position)) {
            this.signLocations.remove(position);
        }
    }

    @Override
    public List<Location> getSignLocations() {
        final List<Location> locations = new ArrayList<>();
        for (final IPosition location : this.signLocations) {
            locations.add(location.toLocation());
        }
        return locations;
    }

    @Override
    public String getGameSubTitleMessage() {
        return this.gameSubTitleMessage;
    }

    @Override
    public void setGameSubTitleMessage(String message) {
        this.gameSubTitleMessage = ChatColor.translateAlternateColorCodes('&', message);
    }

    @Override
    public String getGameTitleMessage() {
        return this.gameTitleMessage;
    }

    @Override
    public void setGameTitleMessage(String gameTitleMessage) {
        this.gameTitleMessage = ChatColor.translateAlternateColorCodes('&', gameTitleMessage);
    }

    @Override
    public void removeSignLocation(int positon) {
        this.signLocations.remove(positon);
    }

    @Override
    public void addSignLocation(Location location) {
        if (location != null) {
            this.signLocations.add(new SLocation(location));
        }
    }

    /**
     * Returns the positions of each sign
     *
     * @return positions
     */
    @Override
    public List<IPosition> getRedTeamSignPositions() {
        return Collections.unmodifiableList(this.redTeamSignLocations);
    }

    /**
     * Removes the sign-position
     *
     * @param position position
     */
    @Override
    public void removeRedTeamSignPosition(IPosition position) {
        if (this.redTeamSignLocations.contains(position)) {
            this.redTeamSignLocations.remove(position);
        }
    }

    @Override
    public List<Location> getRedTeamSignLocations() {
        this.saveTy();
        final List<Location> locations = new ArrayList<>();
        for (final IPosition location : this.redTeamSignLocations) {
            locations.add(location.toLocation());
        }
        return locations;
    }

    @Override
    public void removeRedTeamSignLocation(int position) {
        this.saveTy();
        this.redTeamSignLocations.remove(position);
    }

    @Override
    public void addRedTeamSignLocation(Location location) {
        this.saveTy();
        if (location != null) {
            this.redTeamSignLocations.add(new SLocation(location));
        }
    }

    /**
     * Returns the positions of each sign
     *
     * @return positions
     */
    @Override
    public List<IPosition> getBlueTeamSignPositions() {
        return Collections.unmodifiableList(this.blueTeamSignLocations);
    }

    /**
     * Removes the sign-position
     *
     * @param position position
     */
    @Override
    public void removeBlueTeamSignPosition(IPosition position) {
        if (this.blueTeamSignLocations.contains(position)) {
            this.blueTeamSignLocations.remove(position);
        }
    }

    @Override
    public List<Location> getBlueTeamSignLocations() {
        this.saveTy();
        final List<Location> locations = new ArrayList<>();
        for (final IPosition location : this.blueTeamSignLocations) {
            locations.add(location.toLocation());
        }
        return locations;
    }

    @Override
    public void removeBlueTeamSignLocation(int position) {
        this.saveTy();
        this.blueTeamSignLocations.remove(position);
    }

    @Override
    public void addBlueTeamSignLocation(Location location) {
        if (location != null) {
            this.saveTy();
            this.blueTeamSignLocations.add(new SLocation(location));
        }
    }

    @Override
    public List<Location> getLeaveSignLocations() {
        this.saveTy();
        final List<Location> locations = new ArrayList<>();
        for (final IPosition location : this.leaveSignLocations) {
            locations.add(location.toLocation());
        }
        return locations;
    }

    @Override
    public void removeLeaveSignLocation(int position) {
        this.saveTy();
        this.leaveSignLocations.remove(position);
    }

    @Override
    public void addLeaveignLocation(Location location) {
        if (location != null) {
            this.saveTy();
            this.leaveSignLocations.add(new SLocation(location));
        }
    }

    private void saveTy() {
        if (this.redTeamSignLocations == null)
            this.redTeamSignLocations = new ArrayList<>();
        if (this.blueTeamSignLocations == null)
            this.blueTeamSignLocations = new ArrayList<>();
        if (this.leaveSignLocations == null)
            this.leaveSignLocations = new ArrayList<>();
    }

    @Override
    public Location getLobbyLeave() {
        return this.reference.getTeamMeta().getGameEndSpawnpoint();
    }

    @Override
    public Location getLobbySpawn() {
        if (this.lobbySpawn == null)
            return null;
        return this.lobbySpawn.toLocation();
    }

    @Override
    public void setLobbySpawnpoint(Location lobbySpawnpoint) {
        this.lobbySpawn = new SLocation(lobbySpawnpoint);
    }

    @Override
    public void setLobbyLeave(Location location) {
        this.reference.getTeamMeta().setGameEndSpawnpoint(location);
    }

    @Override
    public void setMinPlayers(int minPlayers) {
        this.reference.getTeamMeta().setTeamMinSize(minPlayers);
    }

    @Override
    public void setMaxPlayers(int maxPlayers) {
        this.reference.getTeamMeta().setTeamMaxSize(maxPlayers);
    }

    @Override
    public void setGameTime(int gameTime) {
        this.gameTime = gameTime;
    }

    @Override
    public int getGameTime() {
        return this.gameTime;
    }

    @Override
    public int getMinPlayers() {
        return this.reference.getTeamMeta().getTeamMinSize();
    }

    @Override
    public int getMaxPlayers() {
        return this.reference.getTeamMeta().getTeamMaxSize();
    }

    @Override
    public void setCountDown(int countDown) {
        this.countdown = countDown;
    }

    @Override
    public int getCountDown() {
        return this.countdown;
    }

    @Override
    public Map<String, Object> serialize() {
        final Map<String, Object> map = new LinkedHashMap<>();
        final Map<String, Object> tmp1 = new LinkedHashMap<>();
        final Map<String, Object> tmp2 = new LinkedHashMap<>();
        map.put("spawnpoint", SFileUtils.serialize(this.lobbySpawn));
        map.put("gameduration", this.gameTime);
        map.put("lobbyduration", this.countdown);
        for (int i = 0; i < this.signLocations.size(); i++)
            tmp2.put("join." + i, this.signLocations.get(i).serialize());
        for (int i = 0; i < this.leaveSignLocations.size(); i++)
            tmp2.put("leave." + i, this.leaveSignLocations.get(i).serialize());
        for (int i = 0; i < this.redTeamSignLocations.size(); i++)
            tmp2.put("red." + i, this.redTeamSignLocations.get(i).serialize());
        for (int i = 0; i < this.blueTeamSignLocations.size(); i++)
            tmp2.put("blue." + i, this.blueTeamSignLocations.get(i).serialize());
        map.put("signs", tmp2);

        tmp1.put("countdown-title", this.gameTitleMessage);
        tmp1.put("countdown-subtitle", this.gameSubTitleMessage);

        map.put("messages", tmp1);
        return map;
    }
}
