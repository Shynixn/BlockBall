package com.github.shynixn.blockball.business.logic.arena;

import com.github.shynixn.blockball.lib.SFileUtils;
import com.github.shynixn.blockball.api.entities.Arena;
import com.github.shynixn.blockball.api.entities.LobbyMeta;
import com.github.shynixn.blockball.lib.SLocation;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.configuration.MemorySection;

import java.io.Serializable;
import java.util.*;

/**
 * Created by Shynixn
 */
class LobbyMetaEntity implements LobbyMeta, Serializable
{
	private static final long serialVersionUID = 1L;
    private SLocation lobbySpawn;
    private List<SLocation> signLocations = new ArrayList<>();
	private List<SLocation> redteamsignLocations = new ArrayList<>();
	private List<SLocation> blueteamsignLocations = new ArrayList<>();
	private List<SLocation> leaveSignLocations = new ArrayList<>();
	private int countdown = 30;
	private int gameTime = 300;
	private String gameTitleMessage = ChatColor.GOLD + "Game";
	private String gamesubTitleMessage = ChatColor.YELLOW + "Starting in :countdown seconds.";

	protected transient Arena reference;

	public LobbyMetaEntity() {}

	public LobbyMetaEntity(Map<String, Object> items) throws Exception
	{
		if(items.get("spawnpoint") != null)
			this.lobbySpawn = new SLocation(((MemorySection) items.get("spawnpoint")).getValues(true));
		this.gameTime = (int) items.get("gameduration");
		this.countdown = (int) items.get("lobbyduration");
		for(int i = 0; i < 10000 && items.containsKey("signs.join." + i); i++)
			this.signLocations.add(new SLocation(((MemorySection) items.get("signs.join." + i)).getValues(true)));
		for(int i = 0; i < 10000 && items.containsKey("signs.leave." + i); i++)
			this.leaveSignLocations.add(new SLocation(((MemorySection) items.get("signs.leave." + i)).getValues(true)));
		for(int i = 0; i < 10000 && items.containsKey("signs.red." + i); i++)
			this.redteamsignLocations.add(new SLocation(((MemorySection) items.get("signs.red." + i)).getValues(true)));
		for(int i = 0; i < 10000 && items.containsKey("signs.blue." + i); i++)
			this.blueteamsignLocations.add(new SLocation(((MemorySection) items.get("signs.blue." + i)).getValues(true)));
		this.gameTitleMessage = (String) items.get("messages.countdown-title");
		this.gamesubTitleMessage = (String) items.get("messages.countdown-subtitle");
	}

    public List<Location> getSignLocations()
	{
		ArrayList<Location> locations = new ArrayList<>();
		for(SLocation location : signLocations)
		{
			locations.add(location.getLocation());
		}
		return locations;
	}

	@Override
	public String getGamesubTitleMessage()
	{
		return gamesubTitleMessage;
	}

	@Override
	public void setGamesubTitleMessage(String gamesubTitleMessage)
	{
		this.gamesubTitleMessage = ChatColor.translateAlternateColorCodes('&',gamesubTitleMessage);
	}

	@Override
	public String getGameTitleMessage()
	{
		return gameTitleMessage;
	}

	@Override
	public void setGameTitleMessage(String gameTitleMessage)
	{
		this.gameTitleMessage =  ChatColor.translateAlternateColorCodes('&',gameTitleMessage);
	}

	@Override
	public void removeSignLocation(int positon)
	{
		signLocations.remove(positon);
	}

	@Override
	public void addSignLocation(Location location)
	{
		if(location != null)
		{
			this.signLocations.add(new SLocation(location));
		}
	}

	@Override
	public List<Location> getRedTeamSignLocations()
	{
		saveTy();
		ArrayList<Location> locations = new ArrayList<>();
		for(SLocation location : redteamsignLocations)
		{
			locations.add(location.getLocation());
		}
		return locations;
	}

	@Override
	public void removeRedTeamSignLocation(int positon)
	{
		saveTy();
		redteamsignLocations.remove(positon);
	}

	@Override
	public void addRedTeamSignLocation(Location location)
	{
		saveTy();
		if(location != null)
		{
			this.redteamsignLocations.add(new SLocation(location));
		}
	}

	@Override
	public List<Location> getBlueTeamSignLocations()
	{
		saveTy();
		ArrayList<Location> locations = new ArrayList<>();
		for(SLocation location : blueteamsignLocations)
		{
			locations.add(location.getLocation());
		}
		return locations;
	}

	@Override
	public void removeBlueTeamSignLocation(int positon)
	{
		saveTy();
		blueteamsignLocations.remove(positon);
	}

	@Override
	public void addBlueTeamSignLocation(Location location)
	{
		if(location != null)
		{
			saveTy();
			this.blueteamsignLocations.add(new SLocation(location));
		}
	}

	@Override
	public List<Location> getLeaveSignLocations()
	{
		saveTy();
		ArrayList<Location> locations = new ArrayList<>();
		for(SLocation location : leaveSignLocations)
		{
			locations.add(location.getLocation());
		}
		return locations;
	}

	@Override
	public void removeLeaveSignLocation(int positon)
	{
		saveTy();
		leaveSignLocations.remove(positon);
	}

	@Override
	public void addLeaveignLocation(Location location)
	{
		if(location != null)
		{
			saveTy();
			this.leaveSignLocations.add(new SLocation(location));
		}
	}

	private void saveTy()
	{
		if(redteamsignLocations == null)
			redteamsignLocations = new ArrayList<>();
		if(blueteamsignLocations == null)
			blueteamsignLocations = new ArrayList<>();
		if(leaveSignLocations == null)
			leaveSignLocations = new ArrayList<>();
	}

	@Override
	public Location getLobbyLeave()
	{
		return reference.getTeamMeta().getGameEndSpawnpoint();
	}

	public Location getLobbySpawn()
    {
		if(lobbySpawn == null)
			return null;
        return this.lobbySpawn.getLocation();
    }

	@Override
	public void setLobbySpawnpoint(Location lobbySpawnpoint)
	{
		this.lobbySpawn = new SLocation(lobbySpawnpoint);
	}

	@Override
	public void setLobbyLeave(Location location)
	{
		reference.getTeamMeta().setGameEndSpawnpoint(location);
	}

	@Override
	public void setMinPlayers(int minPlayers)
	{
		reference.getTeamMeta().setTeamMinSize(minPlayers);
	}

	@Override
	public void setMaxPlayers(int maxPlayers)
	{
		reference.getTeamMeta().setTeamMaxSize(maxPlayers);
	}

	@Override
	public void setGameTime(int gameTime)
	{
		this.gameTime = gameTime;
	}

	@Override
	public int getGameTime()
	{
		return gameTime;
	}

	@Override
	public int getMinPlayers()
	{
		return reference.getTeamMeta().getTeamMinSize()*2;
	}

	@Override
	public int getMaxPlayers()
	{
		return reference.getTeamMeta().getTeamMaxSize()*2;
	}

	@Override
	public void setCountDown(int countDown)
	{
		this.countdown = countDown;
	}

	@Override
	public int getCountDown()
	{
		return countdown;
	}

	@Override
	public Map<String, Object> serialize()
	{
		Map<String, Object> map = new LinkedHashMap<>();
		Map<String, Object> tmp1 = new LinkedHashMap<>();
		Map<String, Object> tmp2 = new LinkedHashMap<>();
		map.put("spawnpoint", SFileUtils.serialize(lobbySpawn));
		map.put("gameduration", gameTime);
		map.put("lobbyduration", countdown);
		for(int i = 0; i < signLocations.size(); i++)
			tmp2.put("join." + i,signLocations.get(i).serialize());
		for(int i = 0; i < leaveSignLocations.size(); i++)
			tmp2.put("leave." + i,leaveSignLocations.get(i).serialize());
		for(int i = 0; i < redteamsignLocations.size(); i++)
			tmp2.put("red." + i,redteamsignLocations.get(i).serialize());
		for(int i = 0; i < blueteamsignLocations.size(); i++)
			tmp2.put("blue." + i,blueteamsignLocations.get(i).serialize());
		map.put("signs", tmp2);

		tmp1.put("countdown-title", gameTitleMessage);
		tmp1.put("countdown-subtitle", gamesubTitleMessage);

		map.put("messages", tmp1);
		return map;
	}
}
