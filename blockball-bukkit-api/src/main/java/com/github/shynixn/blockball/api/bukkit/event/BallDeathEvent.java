package com.github.shynixn.blockball.api.bukkit.event;

import com.github.shynixn.blockball.api.entities.Ball;
import com.github.shynixn.blockball.lib.SEvent;

public final class BallDeathEvent extends SEvent {
	private final Ball ball;

	public BallDeathEvent(Ball ball) {
		super();
		this.ball = ball;
	}

	public Ball getBall() {
		return this.ball;
	}
}
