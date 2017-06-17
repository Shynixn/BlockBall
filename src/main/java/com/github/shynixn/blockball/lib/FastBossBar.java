package com.github.shynixn.blockball.lib;

import org.bukkit.entity.Player;

import java.lang.reflect.Array;
import java.util.List;

@Deprecated
public class FastBossBar implements LightBossBar {
    private int style;
    private int color;
    private int flag = -1;
    private String message;
    private boolean enabled;

    public FastBossBar(String message) {
        super();
        this.message = message;
    }

    public FastBossBar() {
        super();
    }

    public FastBossBar(int style, int color, int flag, String message) {
        super();
        this.style = style;
        this.color = color;
        this.flag = flag;
        this.message = message;
    }

    @Override
    public void stopPlay(Object bossBar, Player player) {
        if (bossBar != null) {
            List<Player> players = (List<Player>) ReflectionLib.invokeMethodByObject(bossBar, "getPlayers");
            if (players.contains(player)) {
                ReflectionLib.invokeMethodByObject(bossBar, "removePlayer", player);
            }
            players = (List<Player>) ReflectionLib.invokeMethodByObject(bossBar, "getPlayers");
            if (players.isEmpty()) {
                dispose(bossBar);
            }
        }
    }

    @Override
    public int getStyle() {
        return this.style;
    }

    @Override
    public void setStyle(int style) {
        this.style = style;
    }

    @Override
    public int getColor() {
        return this.color;
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
    public void setColor(int color) {
        this.color = color;
    }

    @Override
    public int getFlag() {
        return this.flag;
    }

    @Override
    public void setFlag(int flag) {
        this.flag = flag;
    }

    @Override
    public String getMessage() {
        return this.message;
    }

    @Override
    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public Object play(Player... players) {
        return this.play(null, players);
    }

    @Override
    public Object play(Object bossBar, Player... players) {
        return this.play(bossBar, this.message, players);
    }

    @Override
    public Object play(Object bossBar, String message, Player... players) {
        if (canBeUsed() && this.isEnabled()) {
            for (final Player player : players) {
                if (bossBar == null) {
                    final Object colorb = ReflectionLib.getClassFromName("org.bukkit.boss.BarColor").getEnumConstants()[this.color];
                    final Object styleb = ReflectionLib.getClassFromName("org.bukkit.boss.BarStyle").getEnumConstants()[this.style];
                    if (this.flag >= 0) {
                        final Object[] objects = (Object[]) Array.newInstance(ReflectionLib.getClassFromName("org.bukkit.boss.BarFlag"), 1);
                        objects[0] = ReflectionLib.getClassFromName("org.bukkit.boss.BarFlag").getEnumConstants()[this.flag];
                        bossBar = ReflectionLib.invokeMethodByClazz(ReflectionLib.getClassFromName("org.bukkit.Bukkit"), "createBossBar", message, colorb, styleb, objects);
                    } else {
                        bossBar = ReflectionLib.invokeMethodByClazz(ReflectionLib.getClassFromName("org.bukkit.Bukkit"), "createBossBar", message, colorb, styleb, Array.newInstance(ReflectionLib.getClassFromName("org.bukkit.boss.BarFlag"), 0));
                    }
                }
                if (!((List<Player>) ReflectionLib.invokeMethodByObject(bossBar, "getPlayers")).contains(player)) {
                    ReflectionLib.invokeMethodByObject(bossBar, "addPlayer", player);
                }
                if (message != null) {
                    ReflectionLib.invokeMethodByObject(bossBar, "setTitle", message);
                }
            }
            return bossBar;
        }
        return null;
    }

    @Override
    public Object play(Object bossBar, String message, List<Player> players) {
        return this.play(bossBar, message, players.toArray(new Player[players.size()]));
    }

    public static String getColorsText() {
        return getText("org.bukkit.boss.BarColor");
    }

    public static String getStylesText() {
        return getText("org.bukkit.boss.BarStyle");
    }

    public static String getFlagsText() {
        return getText("org.bukkit.boss.BarFlag");
    }


    public static int getColorFromName(String name) {
        return getFromName("org.bukkit.boss.BarColor", name);
    }

    public static int getFlagFromName(String name) {
        return getFromName("org.bukkit.boss.BarFlag", name);
    }

    public static int getStyleFromName(String name) {
        return getFromName("org.bukkit.boss.BarStyle", name);
    }

    private static String getText(String classPath) {
        String s = "";
        for (final Object object : ReflectionLib.getClassFromName(classPath).getEnumConstants()) {
            if (s.isEmpty())
                s += ReflectionLib.invokeMethodByObject(object, "name").toString().toLowerCase();
            else
                s += ", " + ReflectionLib.invokeMethodByObject(object, "name").toString().toLowerCase();
        }
        return s;
    }

    private static int getFromName(String classPath, String name) {
        for (int i = 0; i < ReflectionLib.getClassFromName(classPath).getEnumConstants().length; i++) {
            if (ReflectionLib.invokeMethodByObject(ReflectionLib.getClassFromName(classPath).getEnumConstants()[i], "name").toString().equalsIgnoreCase(name))
                return i;
        }
        return -1;
    }

    public static boolean canBeUsed() {
        return ReflectionLib.getServerVersion().equals("v1_9_R1")
                || ReflectionLib.getServerVersion().equals("v1_9_R2")
                || ReflectionLib.getServerVersion().equals("v1_10_R1")
                || ReflectionLib.getServerVersion().equals("v1_11_R1")
                || ReflectionLib.getServerVersion().equals("v1_12_R1");
    }

    @Override
    public void remove(Object bossBar) {
        FastBossBar.dispose(bossBar);
    }

    public static void dispose(Object bossBar) {
        if (bossBar != null) {
            ReflectionLib.invokeMethodByObject(bossBar, "removeAll");
        }
    }
}
