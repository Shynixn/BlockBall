package com.github.shynixn.blockball.api.entities;

import org.bukkit.entity.Player;

import java.util.List;

public interface Game {
    Arena getArena();

    Ball getBall();

    boolean join(Player player, Team team);

    boolean isInGame(Player player);

    boolean leave(Player player);

    void playBallMoveEffects();

    void playBallKickEffects(Player player);

    Player[] getBlueTeamPlayers();

    Player[] getRedTeamPlayers();

    List<Player> getPlayers();
}
