package com.github.shynixn.blockball.api.events;

import com.github.shynixn.blockball.api.entities.Ball;
import com.github.shynixn.blockball.lib.SEvent;

public final class BallMoveEvent extends SEvent {
	private final Ball ball;

	public BallMoveEvent(Ball ball) {
		super();
		this.ball = ball;
	}

	public Ball getBall() {
		return this.ball;
	}
}
