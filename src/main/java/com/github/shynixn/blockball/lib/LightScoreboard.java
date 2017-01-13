package com.github.shynixn.blockball.lib;

import org.bukkit.entity.Player;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Shynixn
 */
public interface LightScoreboard extends Serializable {
    void remove(Player player);

    void play(Player... players);

    void play(Integer timeleft, Integer redgoals, Integer bluegoals, Player... players);

    void play(Integer timeleft, Integer redgoals, Integer bluegoals, List<Player> players);

    boolean isEnabled();

    void setEnabled(boolean enabled);

    String getTime();

    void setTime(String time);

    String getTeamRed();

    void setTeamRed(String red);

    String getTeamBlue();

    void setTeamBlue(String blue);

    String getTitle();

    void setTitle(String title);

    void remove();
}
