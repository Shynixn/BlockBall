package com.github.shynixn.blockball.bukkit.logic.business.controller;

import com.github.shynixn.blockball.api.business.controller.BallController;
import com.github.shynixn.blockball.api.business.entity.Ball;
import com.github.shynixn.blockball.api.persistence.entity.meta.BallMeta;
import com.github.shynixn.blockball.bukkit.nms.NMSRegistry;
import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Rabbit;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * Copyright 2017 Shynixn
 * <p>
 * Do not remove this header!
 * <p>
 * Version 1.0
 * <p>
 * MIT License
 * <p>
 * Copyright (c) 2017
 * <p>
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * <p>
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * <p>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
public class BallRepository implements BallController {
    private final List<Ball> balls = new ArrayList<>();

    /**
     * Initializes a new ball repository
     */
    public BallRepository() {
        super();
    }

    /**
     * Creates a new ball at the given location and meta
     *
     * @param location location
     * @param ballMeta metaData
     * @return ball
     */
    @Override
    public Ball create(Object location, BallMeta ballMeta) {
        return NMSRegistry.createBall((Location) location, ballMeta);
    }

    /**
     * Returns a ball from the given entity
     *
     * @param entity entity
     * @return ball
     */
    @Override
    public Optional<Ball> findByBallByEntity(Object entity) {
        final Entity bukkitEntity = (Entity) entity;
        for (final Ball ball : this.balls) {
            final ArmorStand stand = (ArmorStand) ball.getDesignEntity();
            final Rabbit rabbit = (Rabbit) ball.getHitboxEntity();
            if (!stand.isDead()) {
                if (bukkitEntity.getEntityId() == stand.getEntityId() || bukkitEntity.getEntityId() == rabbit.getEntityId()) {
                    return Optional.of(ball);
                }
            }
        }
        return Optional.empty();
    }

    /**
     * Stores a new a item in the repository
     *
     * @param item item
     */
    @Override
    public void store(Ball item) {
        if (!this.balls.contains(item)) {
            this.balls.add(item);
        }
    }

    /**
     * Removes an item from the repository
     *
     * @param item item
     */
    @Override
    public void remove(Ball item) {
        if (!this.balls.contains(item)) {
            this.balls.remove(item);
        }
    }

    /**
     * Returns the amount of items in the repository
     *
     * @return size
     */
    @Override
    public int size() {
        return this.balls.size();
    }

    /**
     * Returns all items from the repository as unmodifiableList
     *
     * @return items
     */
    @Override
    public List<Ball> getAll() {
        return Collections.unmodifiableList(this.balls);
    }

    /**
     * Closes this resource, relinquishing any underlying resources.
     * This method is invoked automatically on objects managed by the
     * {@code try}-with-resources statement.
     * However, implementers of this interface are strongly encouraged
     * to make their {@code close} methods idempotent.
     *
     * @throws Exception if this resource cannot be closed
     */
    @Override
    public void close() throws Exception {
        for (final Ball ball : this.balls) {
            ball.remove();
        }
        this.balls.clear();
    }
}
