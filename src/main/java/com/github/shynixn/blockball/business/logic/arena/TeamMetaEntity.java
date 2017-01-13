package com.github.shynixn.blockball.business.logic.arena;

import com.github.shynixn.blockball.api.entities.IPosition;
import com.github.shynixn.blockball.lib.*;
import com.github.shynixn.blockball.api.entities.TeamMeta;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.MemorySection;
import org.bukkit.inventory.ItemStack;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

class TeamMetaEntity implements TeamMeta, Serializable
{
	private static final long serialVersionUID = 1L;
	private String redTeamName =  "&cTeam Red";
	private String blueTeamName = "&9Team Blue";
	private int teamMaxSize = 5;
	private int teamMinSize = 0;
	private String redColor = "&c";
	private String blueColor = "&9";

	private boolean disableDamage = false;
	
	private String[] blueItems = initalize(Color.BLUE);
	private String[] redItems = initalize(Color.RED);
	
	private String joinMessage = "You joined the game.";
	private String leaveMessage = "You left the game.";
	private String howToJoinMessage = "Type ':red' to join the :red team or type ':blue' to join the :blue team. 'Cancel' to exit.";
	private String teamFullMessage = "You cannot join. Team is full.";
	
	private String redtitleScoreMessage = ":redcolor:redscore : :bluecolor:bluescore";
	private String redsubtitleMessage = ":redcolor:player scored for :red";	
	private String bluetitleScoreMessage = ":bluecolor:bluescore : :redcolor:redscore";
	private String bluesubtitleMessage = ":bluecolor:player scored for :blue";	
	private String redwinnerTitleMessage = ":redcolor:red";
	private String bluewinnerTitleMessage = ":bluecolor:blue";
	private String redwinnerSubtitleMessage = "&a&lWinner";
	private String bluewinnerSubtitleMessage = "&a&lWinner";
	
	private String bossBarMessage = ":red :redcolor:redscore : :bluecolor:bluescore :blue";
	private boolean bossBarEnabled = false;

	private LightBossBar bossBarLight = new FastBossBar(":red :redcolor:redscore : :bluecolor:bluescore :blue");
	
	private SLocation leaveSpawnpoint = null;
	
	private int maxScore = 100;
	private boolean autoTeamJoin = false;
	private boolean fastJoin = false;
	private boolean emptyReset = false;
	
	private boolean allowDoubleJump = true;
	private LightParticle doubleJumpParticle = new SParticle(ParticleEffect.EXPLOSION_NORMAL,4, 0.0002, 2, 2, 2);
	private LightSound doubleJumpSound = new FastSound("GHAST_FIREBALL", 100.0, 1.0);
	
	private IPosition blueSpawnPoint;
	private IPosition redSpawnPoint;

	private boolean specatormessages = false;
	private int specatorradius = 0;

	private int rewardGoals = 0;
	private int rewardGames = 0;
	private int rewardWinning = 0;

	private String winCommand;
	private String gamendCommand;

	private LightScoreboard scoreboard;

	private String hologramText = "[ :redcolor:redscore : :bluecolor:bluescore ]";
	private IPosition hologramPosition;
	private boolean hologramEnabled = false;

	public TeamMetaEntity() {}

	public TeamMetaEntity(Map<String, Object> items) throws Exception
	{
		this.maxScore = (int) items.get("generic.max-score");
		this.autoTeamJoin = (boolean) items.get("generic.auto-team-join");
		this.fastJoin = (boolean) items.get("generic.instant-join");
		this.emptyReset = (boolean) items.get("generic.reset-on-empty");
		this.allowDoubleJump = (boolean) items.get("generic.double-jump");
		this.teamMinSize = (int) items.get("generic.min-size");
		this.teamMaxSize = (int) items.get("generic.max-size");
		this.disableDamage = ! (boolean) items.get("generic.take-damage");
		if(items.get("generic.leave-spawnpoint") != null)
			this.leaveSpawnpoint = new SLocation(((MemorySection) items.get("generic.leave-spawnpoint")).getValues(true));

		this.redTeamName = (String) items.get("red.name");
		this.redColor = (String) items.get("red.color");
		if(items.get("red.spawnpoint") != null)
			this.redSpawnPoint = new SLocation(((MemorySection)items.get("red.spawnpoint")).getValues(true));
		this.redItems = ((List<String>) items.get("red.armor")).toArray(new String[0]);

		this.blueTeamName = (String) items.get("blue.name");
		this.blueColor = (String) items.get("blue.color");
		if(items.get("blue.spawnpoint") != null)
			this.blueSpawnPoint = new SLocation(((MemorySection)items.get("blue.spawnpoint")).getValues(true));
		this.blueItems = ((List<String>) items.get("blue.armor")).toArray(new String[0]);

		this.specatormessages = (boolean) items.get("spectators.enabled");
		this.specatorradius = (int) items.get("spectators.radius");

		this.bossBarLight.setEnabled((Boolean) items.get("bossbar.enabled"));
		this.bossBarLight.setMessage((String) items.get("bossbar.text"));
		this.bossBarLight.setColor((Integer) items.get("bossbar.color"));
		this.bossBarLight.setStyle((Integer) items.get("bossbar.style"));
		this.bossBarLight.setFlag((Integer) items.get("bossbar.flag"));

		this.getScoreboard().setEnabled((Boolean) items.get("scoreboard.enabled"));
		this.getScoreboard().setTitle((String) items.get("scoreboard.title"));
		this.getScoreboard().setTeamRed((String) items.get("scoreboard.items.red"));
		this.getScoreboard().setTeamBlue((String) items.get("scoreboard.items.blue"));
		this.getScoreboard().setTime((String) items.get("scoreboard.items.time"));

		this.joinMessage = (String) items.get("messages.join");
		this.leaveMessage = (String) items.get("messages.leave");
		this.howToJoinMessage = (String) items.get("messages.how-to-join");
		this.teamFullMessage = (String) items.get("messages.team-full");

		if(items.containsKey("commands.game-win"))
			this.winCommand = (String) items.get("commands.game-win");
		if(items.containsKey("commands.game-end"))
			this.gamendCommand = (String) items.get("commands.game-end");

		this.redtitleScoreMessage = (String) items.get("messages.red-score-title");
		this.redsubtitleMessage = (String) items.get("messages.red-score-subtitle");
		this.redwinnerTitleMessage = (String) items.get("messages.red-win-title");
		this.redwinnerSubtitleMessage = (String) items.get("messages.red-win-subtitle");
		this.bluetitleScoreMessage = (String) items.get("messages.blue-score-title");
		this.bluesubtitleMessage = (String) items.get("messages.blue-score-subtitle");
		this.bluewinnerTitleMessage = (String) items.get("messages.blue-win-title");
		this.bluewinnerSubtitleMessage = (String) items.get("messages.blue-win-subtitle");

		this.doubleJumpParticle = new SParticle(((MemorySection) items.get("double-jump.particle")).getValues(true));
		this.doubleJumpSound = new FastSound(((MemorySection) items.get("double-jump.sound")).getValues(true));

		this.rewardGoals = (int) items.get("dependencies.vault.rewards-per-goal");
		this.rewardGames = (int) items.get("dependencies.vault.rewards-per-game");
		this.rewardWinning = (int) items.get("dependencies.vault.rewards-per-winning-game");
		this.bossBarEnabled = (boolean) items.get("dependencies.bossbarapi.enabled");
		this.bossBarMessage = (String) items.get("dependencies.bossbarapi.text");

		if(items.get("hologram.enabled") != null)
			this.hologramEnabled = (boolean) items.get("hologram.enabled");
		if(items.get("hologram.text") != null)
			this.hologramText = (String) items.get("hologram.text");
		if(items.get("hologram.position") != null)
			this.hologramPosition = new SLocation(((MemorySection)items.get("hologram.position")).getValues(true));
	}

	private static String[] initalize(Color color)
	{		
		String[] itemStacks = new String[4];			
		itemStacks[0] = SItemStackUtils.serialize(SItemStackUtils.setColor(new ItemStack(Material.LEATHER_BOOTS), color));
		itemStacks[1] = SItemStackUtils.serialize(SItemStackUtils.setColor(new ItemStack(Material.LEATHER_LEGGINGS), color));
		itemStacks[2] = SItemStackUtils.serialize(SItemStackUtils.setColor(new ItemStack(Material.LEATHER_CHESTPLATE), color));			
		return itemStacks;
	}	
	
	public void copy(TeamMetaEntity entity)
	{		 
		entity.redTeamName = this.redTeamName;
		entity.blueTeamName = this.blueTeamName;
		entity.teamMaxSize = this.teamMaxSize;
		entity.redColor = this.redColor;
		entity.blueColor = this.blueColor;
		
		entity.blueItems = this.blueItems.clone();
		entity.redItems = this.redItems.clone();
		
		entity.joinMessage = this.joinMessage;
		entity.leaveMessage = this.leaveMessage;
		entity.howToJoinMessage = this.howToJoinMessage;
		entity.teamFullMessage = this.teamFullMessage;
		
		entity.redtitleScoreMessage = this.redtitleScoreMessage;
		entity.redsubtitleMessage = this.redsubtitleMessage;
		entity.bluetitleScoreMessage = this.bluetitleScoreMessage;
		entity.bluesubtitleMessage = this.bluesubtitleMessage;
		entity.redwinnerTitleMessage = this.redwinnerTitleMessage;
		entity.redwinnerSubtitleMessage = this.redwinnerSubtitleMessage;
		entity.bluewinnerTitleMessage = this.bluewinnerTitleMessage;
		entity.bluewinnerSubtitleMessage = this.bluewinnerSubtitleMessage;

		entity.rewardGames = this.rewardGames;
		entity.rewardGoals = this.rewardGoals;
		entity.rewardWinning = this.rewardWinning;
		entity.specatormessages = this.specatormessages;
		entity.specatorradius = this.specatorradius;
		
		entity.maxScore = this.maxScore;
		entity.autoTeamJoin = this.autoTeamJoin;
		
		entity.allowDoubleJump = this.allowDoubleJump;
		entity.doubleJumpParticle = this.doubleJumpParticle.copy();
		entity.doubleJumpSound = this.doubleJumpSound.copy();	
	}

	@Override
	public Location getGameEndSpawnpoint() 
	{
		if(leaveSpawnpoint != null)
			return leaveSpawnpoint.getLocation();
		return null;
	}

	public String getWinCommand()
	{
		return winCommand;
	}

	public void setWinCommand(String winCommand)
	{
		this.winCommand = winCommand;
	}

	public String getGamendCommand()
	{
		return gamendCommand;
	}

	public void setGamendCommand(String gamendCommand)
	{
		this.gamendCommand = gamendCommand;
	}

	@Override
	public int getSpecatorradius()
	{
		return specatorradius;
	}

	@Override
	public void setSpecatorradius(int specatorradius)
	{
		this.specatorradius = specatorradius;
	}

	@Override
	public int getRewardGoals()
	{
		return rewardGoals;
	}

	@Override
	public boolean isSpectatorMessagesEnabled()
	{
		return specatormessages;
	}

	@Override
	public void setSpecatorMessages(boolean enabled)
	{
		this.specatormessages = enabled;
	}

	@Override
	public void setRewardGoals(int rewardGoals)
	{
		this.rewardGoals = rewardGoals;
	}

	@Override
	public int getRewardGames()
	{
		return rewardGames;
	}

	@Override
	public void setRewardGames(int rewardGames)
	{
		this.rewardGames = rewardGames;
	}

	@Override
	public int getRewardWinning()
	{
		return rewardWinning;
	}

	@Override
	public void setRewardWinning(int rewardWinning)
	{
		this.rewardWinning = rewardWinning;
	}

	@Override
	public void setGameEndSpawnpoint(Location location)
	{
		if(location != null)
			this.leaveSpawnpoint = new SLocation(location);
		else
			this.leaveSpawnpoint = null;
	}

	@Override
	public void resetArmor()
	{
		blueItems = initalize(Color.BLUE);
		redItems = initalize(Color.RED);
	}
	
	@Override
	public void reset()
	{
		this.redtitleScoreMessage = ":redcolor:redscore : :bluecolor:bluescore"; 
		this.redsubtitleMessage = ":redcolor:player scored for :red";	
		this.bluetitleScoreMessage = ":bluecolor:bluescore : :redcolor:redscore";
		this.bluesubtitleMessage = ":bluecolor:player scored for :blue";	
		this.redwinnerTitleMessage = ":redcolor:red";
		this.bluewinnerTitleMessage = ":bluecolor:blue";
		this.redwinnerSubtitleMessage = "&a&lWinner";
		this.bluewinnerSubtitleMessage = "&a&lWinner";	
	}
	
	@Override
	public String getTeamFullMessage() {
		return teamFullMessage;
	}

	@Override
	public void setTeamFullMessage(String teamFullMessage) {
		this.teamFullMessage = teamFullMessage;
	}

	@Override
	public String getRedtitleScoreMessage() 
	{
		return redtitleScoreMessage;
	}

	@Override
	public void setRedtitleScoreMessage(String redtitleScoreMessage)
	{
		this.redtitleScoreMessage = redtitleScoreMessage;
	}

	@Override
	public String getRedsubtitleMessage()
	{
		return redsubtitleMessage;
	}

	@Override
	public void setRedsubtitleMessage(String redsubtitleMessage)
	{
		this.redsubtitleMessage = redsubtitleMessage;
	}

	@Override
	public String getBluetitleScoreMessage() 
	{
		return bluetitleScoreMessage;
	}

	@Override
	public void setBluetitleScoreMessage(String bluetitleScoreMessage) 
	{
		this.bluetitleScoreMessage = bluetitleScoreMessage;
	}

	@Override
	public String getBluesubtitleMessage() 
	{
		return bluesubtitleMessage;
	}

	@Override
	public void setBluesubtitleMessage(String bluesubtitleMessage)
	{
		this.bluesubtitleMessage = bluesubtitleMessage;
	}

	@Override
	public String getRedwinnerTitleMessage() 
	{
		return redwinnerTitleMessage;
	}
	
	@Override
	public void setRedwinnerTitleMessage(String redwinnerTitleMessage)
	{
		this.redwinnerTitleMessage = redwinnerTitleMessage;
	}

	@Override
	public String getBluewinnerTitleMessage() 
	{
		return bluewinnerTitleMessage;
	}

	@Override
	public void setBluewinnerTitleMessage(String bluewinnerTitleMessage) 
	{
		this.bluewinnerTitleMessage = bluewinnerTitleMessage;
	}

	@Override
	public String getRedwinnerSubtitleMessage() 
	{
		return redwinnerSubtitleMessage;
	}

	@Override
	public void setRedwinnerSubtitleMessage(String redwinnerSubtitleMessage) 
	{
		this.redwinnerSubtitleMessage = redwinnerSubtitleMessage;
	}

	@Override
	public String getBluewinnerSubtitleMessage() 
	{
		return bluewinnerSubtitleMessage;
	}

	@Override
	public void setBluewinnerSubtitleMessage(String bluewinnerSubtitleMessage)
	{
		this.bluewinnerSubtitleMessage = bluewinnerSubtitleMessage;
	}

	@Override
	public int getMaxScore()
	{
		return maxScore;
	}

	@Override
	public void setMaxScore(int maxScore) 
	{
		this.maxScore = maxScore;
	}
		
	@Override
	public LightParticle getDoubleJumpParticle()
	{
		return doubleJumpParticle;
	}

	@Override
	public void setDoubleJumpParticle(LightParticle doubleJumpParticle)
	{
		this.doubleJumpParticle = doubleJumpParticle;
	}

	@Override
	public LightSound getDoubleJumpSound()
	{
		return doubleJumpSound;
	}

	@Override
	public void setDoubleJumpSound(LightSound doubleJumpSound)
	{
		this.doubleJumpSound = doubleJumpSound;
	}

	@Override
	public Location getBlueSpawnPoint()
	{
		if(blueSpawnPoint == null)
			return null;
		return blueSpawnPoint.toLocation();
	}

	@Override
	public Location getHologramLocation()
	{
		if(hologramPosition == null)
			return null;
		return hologramPosition.toLocation();
	}

	@Override
	public void setHologramLocation(Location location)
	{
		if(location != null)
			this.hologramPosition = new SLocation(location);
	}

	@Override
	public String getHologramText()
	{
		if(hologramText != null)
			return ChatColor.translateAlternateColorCodes('&', hologramText);
		return hologramText;
	}

	@Override
	public void setHologramText(String text)
	{
		this.hologramText = text;
	}

	@Override
	public void setHologramEnabled(boolean enabled)
	{
		this.hologramEnabled = enabled;
	}

	@Override
	public boolean isHologramEnabled()
	{
		return hologramEnabled;
	}

	@Override
	public void setBlueSpawnPoint(Location blueSpawnPoint)
	{
		if(blueSpawnPoint != null)
			this.blueSpawnPoint = new SLocation(blueSpawnPoint);
		else
			this.blueSpawnPoint = null;
	}

	@Override
	public Location getRedSpawnPoint() 
	{
		if(redSpawnPoint == null)
			return null;
		return redSpawnPoint.toLocation();
	}

	@Override
	public void setRedSpawnPoint(Location redSpawnPoint) 
	{
		if(redSpawnPoint != null)
			this.redSpawnPoint = new SLocation(redSpawnPoint);
		else
			this.redSpawnPoint = null;
	}

	@Override
	public String getRedTeamName()
	{
		return ChatColor.translateAlternateColorCodes('&',redTeamName);
	}

	@Override
	public void setRedTeamName(String redTeamName)
	{
		this.redTeamName = redTeamName;
	}

	@Override
	public String getBlueTeamName() 
	{
		return ChatColor.translateAlternateColorCodes('&',blueTeamName);
	}

	@Override
	public void setBlueTeamName(String blueTeamName) 
	{
		this.blueTeamName = blueTeamName;
	}

	@Override
	public int getTeamMaxSize() 
	{
		return teamMaxSize;
	}

	@Override
	public void setTeamMaxSize(int teamMaxSize) 
	{
		this.teamMaxSize = teamMaxSize;
	}

	@Override
	public String getRedColor() 
	{
		return ChatColor.translateAlternateColorCodes('&', redColor);
	}

	@Override
	public void setRedColor(String redColor) 
	{
		this.redColor = redColor;
	}

	@Override
	public String getBlueColor() 
	{
		return ChatColor.translateAlternateColorCodes('&', blueColor);
	}

	@Override
	public void setBlueColor(String blueColor)
	{
		this.blueColor = blueColor;
	}

	@Override
	public ItemStack[] getBlueItems()
	{
		ItemStack[] itemStack = new ItemStack[4];		
		itemStack[0] = SItemStackUtils.deserialize(blueItems[0]);
		itemStack[1] = SItemStackUtils.deserialize(blueItems[1]);
		itemStack[2] = SItemStackUtils.deserialize(blueItems[2]);
		itemStack[3] = SItemStackUtils.deserialize(blueItems[3]);			
		return itemStack;
	}

	@Override
	public boolean isDamageEnabled()
	{
		return !disableDamage;
	}

	@Override
	public void setDamage(boolean enabled)
	{
		this.disableDamage = !enabled;
	}

	@Override
	public void setBlueItems(ItemStack[] itemStacks)
	{
		this.blueItems = new String[4];
		this.blueItems[0] = SItemStackUtils.serialize(itemStacks[0]);
		this.blueItems[1] = SItemStackUtils.serialize(itemStacks[1]);
		this.blueItems[2] = SItemStackUtils.serialize(itemStacks[2]);
		this.blueItems[3] = SItemStackUtils.serialize(itemStacks[3]);
	}

	@Override
	public ItemStack[] getRedItems() 
	{
		ItemStack[] itemStack = new ItemStack[4];
		itemStack[0] = SItemStackUtils.deserialize(redItems[0]);
		itemStack[1] = SItemStackUtils.deserialize(redItems[1]);
		itemStack[2] = SItemStackUtils.deserialize(redItems[2]);
		itemStack[3] = SItemStackUtils.deserialize(redItems[3]);			
		return itemStack;
	}


	@Override
	public void setRedItems(ItemStack[] itemStacks) 
	{
		this.redItems = new String[4];
		this.redItems[0] = SItemStackUtils.serialize(itemStacks[0]);
		this.redItems[1] = SItemStackUtils.serialize(itemStacks[1]);
		this.redItems[2] = SItemStackUtils.serialize(itemStacks[2]);
		this.redItems[3] = SItemStackUtils.serialize(itemStacks[3]);
	}

	@Override
	public String getJoinMessage() 
	{
		return joinMessage;
	}

	@Override
	public void setJoinMessage(String joinMessage)
	{
		this.joinMessage = joinMessage;
	}

	@Override
	public String getLeaveMessage() 
	{
		return leaveMessage;
	}

	@Override
	public void setLeaveMessage(String leaveMessage) 
	{
		this.leaveMessage = leaveMessage;
	}

	@Override
	public void setHowToJoinMessage(String message)
	{
		this.howToJoinMessage = message;
	}

	@Override
	public String getHowToJoinMessage() 
	{
		return howToJoinMessage;
	}

	@Override
	public boolean isAllowDoubleJump() 
	{
		return allowDoubleJump;
	}

	@Override
	public void setAllowDoubleJump(boolean allowDoubleJump) 
	{
		this.allowDoubleJump = allowDoubleJump;
	}

	@Override
	public boolean isTeamAutoJoin() 
	{
		return autoTeamJoin;
	}

	@Override
	public void setTeamAutoJoin(boolean autoJoin)
	{
		this.autoTeamJoin = autoJoin;		
	}

	@Override
	public boolean isFastJoin()
	{
		return fastJoin;
	}

	@Override
	public void setFastJoin(boolean enable) 
	{
		this.fastJoin = enable;
	}

	@Override
	public int getTeamMinSize() 
	{
		return teamMinSize;
	}

	@Override
	public void setTeamMinSize(int teamMinSize) 
	{
		this.teamMinSize = teamMinSize;
	}

	@Override
	public boolean isEmtptyReset() 
	{
		return emptyReset;
	}

	@Override
	public void setEmptyReset(boolean enabled) 
	{
		this.emptyReset = enabled;
	}

	@Deprecated
	public String getBossBarMessage()
	{
		return bossBarMessage;
	}

	@Deprecated
	public boolean isBossBarEnabled()
	{
		return bossBarEnabled;
	}

	@Deprecated
	public void setBossBarMessage(String message) 
	{
		this.bossBarMessage = message;
	}

	@Deprecated
	public void setBossBarEnabled(boolean enable) 
	{
		this.bossBarEnabled = enable;
	}

	@Override
	public String getBossBarPluginMessage()
	{
		return bossBarMessage;
	}

	@Override
	public boolean isBossBarPluginEnabled()
	{
		return bossBarEnabled;
	}

	@Override
	public void setBossBarPluginMessage(String message)
	{
		this.bossBarMessage = message;
	}

	@Override
	public void setBossBarPluginEnabled(boolean enable)
	{
		this.bossBarEnabled = enable;
	}

	@Override
	public LightBossBar getBossBar()
	{
		return bossBarLight;
	}

	@Override
	public LightScoreboard getScoreboard()
	{
		if(scoreboard == null)
			scoreboard = new FastScoreboard();
		return scoreboard;
	}

	public void setBossBarLight(LightBossBar bossBarLight)
	{
		this.bossBarLight = bossBarLight;
	}

	@Override
	public Map<String, Object> serialize()
	{
		Map<String, Object> map = new LinkedHashMap<>();
		Map<String, Object> tmp1 = new LinkedHashMap<>();
		Map<String, Object> tmp2 = new LinkedHashMap<>();
		Map<String, Object> tmp3 = new LinkedHashMap<>();
		Map<String, Object> tmp4 = new LinkedHashMap<>();
		Map<String, Object> tmp5 = new LinkedHashMap<>();
		Map<String, Object> tmp6 = new LinkedHashMap<>();
		Map<String, Object> tmp7 = new LinkedHashMap<>();
		Map<String, Object> tmp8 = new LinkedHashMap<>();
		Map<String, Object> tmp9 = new LinkedHashMap<>();
		Map<String, Object> tmp10 = new LinkedHashMap<>();
		Map<String, Object> tmp11 = new LinkedHashMap<>();
		Map<String, Object> tmp12 = new LinkedHashMap<>();
		Map<String, Object> tmp14 = new LinkedHashMap<>();
		Map<String, Object> tmp0 = new LinkedHashMap<>();

		tmp1.put("max-score", maxScore);
		tmp1.put("auto-team-join", autoTeamJoin);
		tmp1.put("instant-join", fastJoin);
		tmp1.put("reset-on-empty", emptyReset);
		tmp1.put("double-jump", allowDoubleJump);
		tmp1.put("min-size", teamMinSize);
		tmp1.put("max-size", teamMaxSize);
		tmp1.put("take-damage", !disableDamage);
		tmp1.put("leave-spawnpoint", SFileUtils.serialize(leaveSpawnpoint));
		map.put("generic", tmp1);

		tmp0.put("game-win", winCommand);
		tmp0.put("game-end", gamendCommand);
		map.put("commands", tmp0);

		tmp4.put("enabled", specatormessages);
		tmp4.put("radius", specatorradius);
		map.put("spectators", tmp4);

		tmp5.put("enabled", bossBarLight.isEnabled());
		tmp5.put("text", bossBarLight.getMessage());
		tmp5.put("color", bossBarLight.getColor());
		tmp5.put("style", bossBarLight.getStyle());
		tmp5.put("flag", bossBarLight.getFlag());
		map.put("bossbar", tmp5);

		tmp6.put("enabled",getScoreboard().isEnabled());
		tmp6.put("title", getScoreboard().getTitle());
		tmp11.put("red", getScoreboard().getTeamRed());
		tmp11.put("blue", getScoreboard().getTeamBlue());
		tmp11.put("time", getScoreboard().getTime());
		tmp6.put("items", tmp11);
		map.put("scoreboard", tmp6);

		tmp7.put("join", joinMessage);
		tmp7.put("leave", leaveMessage);
		tmp7.put("how-to-join", howToJoinMessage);
		tmp7.put("team-full", teamFullMessage);
		tmp7.put("red-score-title", redtitleScoreMessage);
		tmp7.put("red-score-subtitle", redsubtitleMessage);
		tmp7.put("red-win-title", redwinnerTitleMessage);
		tmp7.put("red-win-subtitle",redwinnerSubtitleMessage);
		tmp7.put("blue-score-title", bluetitleScoreMessage);
		tmp7.put("blue-score-subtitle", bluesubtitleMessage);
		tmp7.put("blue-win-title", bluewinnerTitleMessage);
		tmp7.put("blue-win-subtitle", bluewinnerSubtitleMessage);
		map.put("messages", tmp7);

		tmp8.put("rewards-per-goal", rewardGoals);
		tmp8.put("rewards-per-game", rewardGames);
		tmp8.put("rewards-per-winning-game", rewardWinning);
		tmp10.put("enabled", bossBarEnabled);
		tmp10.put("text", bossBarMessage);
		tmp9.put("vault", tmp8);
		tmp9.put("bossbarapi", tmp10);
		map.put("dependencies", tmp9);

		tmp2.put("name", redTeamName);
		tmp2.put("color", redColor);
		tmp2.put("spawnpoint", SFileUtils.serialize(redSpawnPoint));
		tmp2.put("armor", redItems);
		map.put("red", tmp2);

		tmp3.put("name", blueTeamName);
		tmp3.put("color", blueColor);
		tmp3.put("spawnpoint", SFileUtils.serialize(blueSpawnPoint));
		tmp3.put("armor", blueItems);
		map.put("blue",tmp3);

		tmp12.put("particle", SFileUtils.serialize(doubleJumpParticle));
		tmp12.put("sound", SFileUtils.serialize(doubleJumpSound));
		map.put("double-jump", tmp12);

		tmp14.put("enabled", hologramEnabled);
		tmp14.put("text", hologramText);
		tmp14.put("position", SFileUtils.serialize(hologramPosition));
		map.put("hologram", tmp14);

		return map;
	}
}
