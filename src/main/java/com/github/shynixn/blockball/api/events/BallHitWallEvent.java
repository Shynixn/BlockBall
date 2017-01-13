package com.github.shynixn.blockball.api.events;

import com.github.shynixn.blockball.api.entities.Ball;
import com.github.shynixn.blockball.lib.SEvent;
import org.bukkit.block.Block;

/**
 * Created by Shynixn
 */
public class BallHitWallEvent extends SEvent {
    private final Ball ball;
    private final Block block;

    public BallHitWallEvent(Ball ball, Block block) {
        this.ball = ball;
        this.block = block;
    }

    public Block getBlock() {
        return this.block;
    }

    public Ball getBall() {
        return this.ball;
    }
}
