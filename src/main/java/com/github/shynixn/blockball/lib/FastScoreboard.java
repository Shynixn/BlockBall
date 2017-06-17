package com.github.shynixn.blockball.lib;

import com.github.shynixn.blockball.business.bukkit.BlockBallPlugin;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

import java.util.List;
import java.util.logging.Level;

public class FastScoreboard implements LightScoreboard {
    private transient Scoreboard scoreboard;
    private transient Objective objective;

    private String redTeam = ChatColor.RED + "Team Red";
    private String blueTeam = ChatColor.BLUE + "Team Blue";
    private String timeLeft = ChatColor.YELLOW + "Time";
    private String title = ChatColor.GREEN + "" + ChatColor.BOLD + "BlockBall";
    private boolean enabled;

    public FastScoreboard() {
        super();
    }

    public FastScoreboard(String redTeam, String blueTeam, String timeLeft, String title) {
        super();
        this.redTeam = redTeam;
        this.blueTeam = blueTeam;
        this.timeLeft = timeLeft;
        this.title = title;
    }

    @Override
    public void remove(Player player) {
        if (this.scoreboard != null && player.getScoreboard() != null && player.getScoreboard().equals(this.scoreboard)) {
            player.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
        }
    }

    @Override
    public void play(Player... players) {
        this.play(null, null, null, players);
    }

    @Override
    public void play(Integer timeleft, Integer redgoals, Integer bluegoals, Player... players) {
        try {
            if (!this.isEnabled()) {
                this.remove();
                return;
            }
            if (this.scoreboard == null || this.objective == null) {
                this.scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
                this.objective = this.scoreboard.registerNewObjective("bb_scoreboard", "dummy");
                this.objective.setDisplaySlot(DisplaySlot.SIDEBAR);
                this.objective.setDisplayName(this.title);
            }
            for (final Player player : players) {
                if (!player.getScoreboard().equals(this.scoreboard)) {
                    player.setScoreboard(this.scoreboard);
                }
            }
            if (timeleft != null && timeleft >= 0)
                this.objective.getScore(this.timeLeft).setScore(timeleft);
            if (redgoals != null)
                this.objective.getScore(this.redTeam).setScore(redgoals);
            if (bluegoals != null)
                this.objective.getScore(this.blueTeam).setScore(bluegoals);
        } catch (final Exception ex) {
            SConsoleUtils.sendColoredMessage("Scoreboard crashed. Check if the text is short enough for the scoreboard!", ChatColor.RED, BlockBallPlugin.PREFIX_CONSOLE);
            Bukkit.getLogger().log(Level.WARNING, "Cannot set scoreboard.", ex);
            this.scoreboard = null;
            this.objective = null;
        }
    }

    @Override
    public void play(Integer timeleft, Integer redgoals, Integer bluegoals, List<Player> players) {
        this.play(timeleft, redgoals, bluegoals, players.toArray(new Player[players.size()]));
    }

    @Override
    public boolean isEnabled() {
        return this.enabled;
    }

    @Override
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    @Override
    public String getTime() {
        return this.timeLeft;
    }

    @Override
    public void setTime(String time) {
        this.timeLeft = ChatColor.translateAlternateColorCodes('&', time);
    }

    @Override
    public String getTeamRed() {
        return this.redTeam;
    }

    @Override
    public void setTeamRed(String red) {
        this.redTeam = ChatColor.translateAlternateColorCodes('&', red);
    }

    @Override
    public String getTeamBlue() {
        return this.blueTeam;
    }

    @Override
    public void setTeamBlue(String blue) {
        this.blueTeam = ChatColor.translateAlternateColorCodes('&', blue);
    }

    @Override
    public String getTitle() {
        return this.title;
    }

    @Override
    public void setTitle(String title) {
        this.title = ChatColor.translateAlternateColorCodes('&', title);
    }

    @Override
    public void remove() {
        if (this.scoreboard == null) {
            return;
        }
        for (final Player player : SFileUtils.getOnlinePlayers()) {
            this.remove(player);
        }
        this.objective = null;
        this.scoreboard = null;
    }
}
