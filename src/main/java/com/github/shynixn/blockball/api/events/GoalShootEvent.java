package com.github.shynixn.blockball.api.events;

import com.github.shynixn.blockball.api.entities.Game;
import com.github.shynixn.blockball.api.entities.Team;
import com.github.shynixn.blockball.lib.SEvent;
import org.bukkit.entity.Player;

/**
 * Created by Shynixn
 */
public class GoalShootEvent extends SEvent {
    private Game game;
    private Player player;
    private Team team;

    public GoalShootEvent(Game game, Player player, Team team) {
        this.game = game;
        this.player = player;
        this.team = team;
    }

    public Game getGame() {
        return game;
    }

    public Player getPlayer() {
        return player;
    }

    public Team getTeam() {
        return team;
    }
}
