package com.github.shynixn.blockball.api.events;

import com.github.shynixn.blockball.api.entities.Ball;
import org.bukkit.entity.Player;

@SuppressWarnings("unused")
public final class BallKickEvent extends BallInteractEvent {
    public BallKickEvent(Player player, Ball ball) {
        super(player, ball);
    }
}
