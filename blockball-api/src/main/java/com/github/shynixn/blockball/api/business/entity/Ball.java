package com.github.shynixn.blockball.api.business.entity;

import com.github.shynixn.blockball.api.persistence.entity.BallMeta;

public interface Ball {

    /**
     * Kicks the ball with the given strength parameters.
     *
     * @param entity             entity
     * @param horizontalStrength horizontalStrength
     * @param verticalStrength   verticalStrength
     */
    void kick(Object entity, double horizontalStrength, double verticalStrength);

    /**
     * Kicks the ball with the default strength values.
     *
     * @param entity entity
     */
    void kick(Object entity);

    /**
     * Passes the ball with the given strength parameters.
     *
     * @param entity             entity
     * @param horizontalStrength horizontalStrength
     * @param verticalStrength   verticalStrength
     */
    void pass(Object entity, double horizontalStrength, double verticalStrength);

    /**
     * Passes the ball with the default strength values.
     *
     * @param entity entity
     */
    void pass(Object entity);

    /**
     * Shoots the ball in the during vector direction.
     *
     * @param vector vector
     */
    void setVelocity(Object vector);

    /**
     * Returns the velocity the ball is flying after a kick/pass or velocity manipulation.
     * @return velocity
     */
    Object getVelocity();

    /**
     * Respawns the entity at the given location.
     *
     * @param location location
     */
    void spawn(Object location);

    /**
     * Damages the ball entity with the given amount.
     *
     * @param amount amount
     */
    void damage(double amount);

    /**
     * Removes the entity.
     */
    void remove();

    /**
     * Teleports the ball to the given location.
     *
     * @param location location
     */
    void teleport(Object location);

    /**
     * Returns the location of the ball.
     *
     * @return location
     */
    Object getLocation();

    /**
     * Returns if the ball entity is Dead.
     *
     * @return isDead
     */
    boolean isDead();

    /**
     * Returns the entity who is responsible for the ball design and skin.
     *
     * @return entity
     */
    Object getDesignEntity();

    /**
     * Returns the entity who is responsible for the hitbox.
     *
     * @return entity
     */
    Object getHitboxEntity();

    /**
     * Returns the metaData of the entity.
     *
     * @return meta
     */
    BallMeta getMeta();
}
