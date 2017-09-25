package com.github.shynixn.blockball.bukkit.logic.ball;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.github.shynixn.blockball.api.business.controller.BallController;
import com.github.shynixn.blockball.api.entities.Ball;
import com.github.shynixn.blockball.bukkit.nms.NMSRegistry;
import org.bukkit.World;

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
public final class BallRepository implements BallController {
    private final List<Ball> balls = new ArrayList<>();

    /**
     * Spawns a new ball in the given world
     *
     * @param world world
     * @return ball
     */
    @Override
    public Ball createBall(World world) {
        return NMSRegistry.createBall(world);
    }

    /**
     * Initializes a new ball repository
     */
    public BallRepository() {
        super();
        new BallListener(this);
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
            ball.despawn();
        }
        this.balls.clear();
    }
}
