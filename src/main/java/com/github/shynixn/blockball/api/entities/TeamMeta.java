package com.github.shynixn.blockball.api.entities;

import com.github.shynixn.blockball.lib.LightBossBar;
import com.github.shynixn.blockball.lib.LightParticle;
import com.github.shynixn.blockball.lib.LightSound;
import com.github.shynixn.blockball.lib.LightScoreboard;
import org.bukkit.Location;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.inventory.ItemStack;

public interface TeamMeta extends ConfigurationSerializable {
    int getMaxScore();

    void setMaxScore(int maxScore);

    Location getBlueSpawnPoint();

    Location getHologramLocation();

    void setHologramLocation(Location location);

    String getHologramText();

    void setHologramText(String text);

    void setHologramEnabled(boolean enabled);

    boolean isHologramEnabled();

    void setBlueSpawnPoint(Location blueSpawnPoint);

    Location getRedSpawnPoint();

    void setRedSpawnPoint(Location redSpawnPoint);

    LightParticle getDoubleJumpParticle();

    void setDoubleJumpParticle(LightParticle doubleJumpParticle);

    LightSound getDoubleJumpSound();

    boolean isFastJoin();

    int getRewardGoals();

    String getWinCommand();

    void setWinCommand(String winCommand);

    String getGamendCommand();

    void setGamendCommand(String gamendCommand);

    boolean isSpectatorMessagesEnabled();

    void setSpecatorMessages(boolean enabled);

    int getSpecatorradius();

    void setSpecatorradius(int specatorradius);

    void setRewardGoals(int rewardGoals);

    int getRewardGames();

    void setRewardGames(int rewardGames);

    int getRewardWinning();

    void setRewardWinning(int rewardWinning);

    void setFastJoin(boolean enable);

    void setDoubleJumpSound(LightSound doubleJumpSound);

    String getRedtitleScoreMessage();

    void setRedtitleScoreMessage(String redtitleScoreMessage);

    String getRedsubtitleMessage();

    void setRedsubtitleMessage(String redsubtitleMessage);

    String getBluetitleScoreMessage();

    void setBluetitleScoreMessage(String bluetitleScoreMessage);

    String getBluesubtitleMessage();

    void setBluesubtitleMessage(String bluesubtitleMessage);

    String getRedwinnerTitleMessage();

    void setRedwinnerTitleMessage(String redwinnerTitleMessage);

    String getBluewinnerTitleMessage();

    void setBluewinnerTitleMessage(String bluewinnerTitleMessage);

    String getRedwinnerSubtitleMessage();

    void setRedwinnerSubtitleMessage(String redwinnerSubtitleMessage);

    String getBluewinnerSubtitleMessage();

    void setBluewinnerSubtitleMessage(String bluewinnerSubtitleMessage);

    void reset();

    boolean isTeamAutoJoin();

    void setTeamAutoJoin(boolean autoJoin);

    String getTeamFullMessage();

    void setTeamFullMessage(String teamFullMessage);

    void resetArmor();

    Location getGameEndSpawnpoint();

    void setGameEndSpawnpoint(Location location);

    String getRedTeamName();

    void setRedTeamName(String redTeamName);

    String getBlueTeamName();

    void setBlueTeamName(String blueTeamName);

    int getTeamMaxSize();

    void setTeamMaxSize(int teamMaxSize);

    int getTeamMinSize();

    void setTeamMinSize(int teamMinSize);

    String getRedColor();

    boolean isEmtptyReset();

    void setEmptyReset(boolean enabled);

    void setRedColor(String redColor);

    String getBlueColor();

    void setBlueColor(String blueColor);

    ItemStack[] getBlueItems();

    boolean isDamageEnabled();

    void setDamage(boolean enabled);

    void setBlueItems(ItemStack[] itemStacks);

    ItemStack[] getRedItems();

    void setRedItems(ItemStack[] redItems);

    String getJoinMessage();

    void setJoinMessage(String joinMessage);

    String getLeaveMessage();

    void setLeaveMessage(String leaveMessage);

    void setHowToJoinMessage(String message);

    String getHowToJoinMessage();

    boolean isAllowDoubleJump();

    void setAllowDoubleJump(boolean allowDoubleJump);

    String getBossBarPluginMessage();

    boolean isBossBarPluginEnabled();

    void setBossBarPluginMessage(String message);

    void setBossBarPluginEnabled(boolean enable);

    LightBossBar getBossBar();

    LightScoreboard getScoreboard();

    void setGoalShooterGlowing(boolean enable);

    boolean isGoalShooterGlowing();

    void setGoalShooterGlowingSeconds(int seconds);

    int getGoalShooterGlowingSeconds();
}