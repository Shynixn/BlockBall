package com.github.shynixn.blockball.lib;

import org.bukkit.entity.Player;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Shynixn
 */
public interface LightBossBar extends Serializable {
    void stopPlay(Object bossBar, Player player);

    int getStyle();

    void setStyle(int style);

    int getColor();

    void setColor(int color);

    int getFlag();

    void setFlag(int flag);

    String getMessage();

    boolean isEnabled();

    void setEnabled(boolean enabled);

    void setMessage(String message);

    Object play(Player... players);

    Object play(Object bossBar, Player... players);

    Object play(Object bossBar, String message, Player... players);

    Object play(Object bossBar, String message, List<Player> players);

    void remove(Object bossBar);
}
