package com.github.shynixn.blockball.api.persistence.entity

import java.io.DataOutput

/**
 * Created by Shynixn 2018.
 * <p>
 * Version 1.2
 * <p>
 * MIT License
 * <p>
 * Copyright (c) 2018 by Shynixn
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
interface Position {
    /** [worldName] which world the location is. */
    var worldName: String?

    /** [x] coordinate. */
    var x: Double

    /** [y] coordinate. */
    var y: Double

    /** [z] coordinate. */
    var z: Double

    /** [yaw] rotation yaw. */
    var yaw: Double

    /** [pitch] rotation pitch. */
    var pitch: Double

    /** [blockX] coordinate as Int. */
    val blockX: Int

    /** [blockY] coordinate as Int. */
    val blockY: Int

    /** [blockZ] coordinate as Int. */
    val blockZ: Int

    /**
     * Adds to this position. Returns this position.
     */
    fun add(x : Double, y : Double, z : Double) : Position

    /**
     * Subtracts the given [position] from this position
     * and returns this position.
     */
    fun subtract(position: Position): Position

    /**
     * Calculates the distance to the other location.
     */
    fun distance(o: Position): Double

    /**
     * Calculates the square distance to the other location.
     */
    fun distanceSquared(o: Position): Double

    /**
     * Normalizes the position and returns the same position.
     */
    fun normalize(): Position

    /**
     * Calculates the dot product and returns.
     */
    fun dot(other: Position) : Double

    /**
     * Returns the vector length.
     */
    fun length(): Double

    /**
     * Multiplies the position and returns the same position.
     */
    fun multiply(multiplier: Double): Position

    /**
     * Clones the position.
     */
    fun clone(): Position
}
