package com.github.shynixn.blockball.api.entities;

/**
 * Created by Shynixn
 */
public enum GameType {
    LOBBY,
    MINIGAME,
    BUNGEE,
    EVENT;

    public static GameType getGameTypeFromName(String name) {
        for (final GameType gameType : GameType.values()) {
            if (gameType.name().equalsIgnoreCase(name))
                return gameType;
        }
        return null;
    }
}
