package com.github.shynixn.blockball.api.events;

import com.github.shynixn.blockball.lib.SEvent;
import com.github.shynixn.blockball.api.entities.Ball;

public final class BallDeathEvent extends SEvent {
	private final Ball ball;

	public BallDeathEvent(Ball ball) {
		this.ball = ball;
	}

	public Ball getBall() {
		return this.ball;
	}
}
