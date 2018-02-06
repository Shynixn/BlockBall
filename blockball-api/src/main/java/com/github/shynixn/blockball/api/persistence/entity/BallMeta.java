package com.github.shynixn.blockball.api.persistence.entity;

import com.github.shynixn.ball.api.persistence.controller.BounceController;
import com.github.shynixn.ball.api.persistence.effect.ParticleEffectMeta;
import com.github.shynixn.ball.api.persistence.effect.SoundEffectMeta;
import com.github.shynixn.blockball.api.persistence.entity.basic.IPosition;

public interface BallMeta<T extends ParticleEffectMeta, K extends SoundEffectMeta, P extends BounceController> extends Persistenceable, com.github.shynixn.ball.api.persistence.BallMeta<T, K, P> {

    /**
     * Returns the spawndelay ticks.
     * @return ticks
     */
    int getSpawnDelayTicks();

    /**
     * Sets the spawndelay ticks.
     * @param spawnDelayTicks spawnDelay
     */
    void setSpawnDelayTicks(int spawnDelayTicks);

    /**
     * Sets the spawnpoint of the ball.
     *
     * @param position position
     */
    void setSpawnpoint(IPosition position);

    /**
     * Returns the spawnpoint of the ball.
     *
     * @return spawnpoint
     */
    IPosition getSpawnpoint();
}
