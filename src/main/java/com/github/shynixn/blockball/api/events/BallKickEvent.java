package com.github.shynixn.blockball.api.events;

import com.github.shynixn.blockball.lib.SEvent;
import org.bukkit.entity.Player;

import com.github.shynixn.blockball.api.entities.Ball;

@SuppressWarnings("unused")
public final class BallKickEvent extends SEvent {
	private final Player player;
	private final Ball ball;
	private boolean isCancelled;
	
	public BallKickEvent(Player player, Ball ball) {
		this.player = player;
		this.ball = ball;
	}

	public Player getPlayer() {
		return this.player;
	}

	public Ball getBall() {
		return this.ball;
	}

	public boolean isCancelled() {
		return this.isCancelled;
	}

	public void setCancelled(boolean isCancelled) {
		this.isCancelled = isCancelled;
	}		
}
