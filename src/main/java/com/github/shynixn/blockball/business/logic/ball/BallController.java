package com.github.shynixn.blockball.business.logic.ball;

import java.util.ArrayList;

import com.github.shynixn.blockball.api.entities.Ball;

public final class BallController {
    private final ArrayList<Ball> balls = new ArrayList<>();

    public BallController() {
        super();
        new BallListener(this);
    }

    public void addBall(Ball ball) {
        if (!this.balls.contains(ball))
            this.balls.add(ball);
    }

    void removeBall(Ball ball) {
        if (!this.balls.contains(ball))
            this.balls.remove(ball);
    }

    Ball[] getBalls() {
        return this.balls.toArray(new Ball[this.balls.size()]);
    }
}
