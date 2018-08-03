package com.github.shynixn.blockball.bukkit.logic.persistence.entity

import com.github.shynixn.blockball.api.persistence.entity.StorageLocation
import com.github.shynixn.blockball.bukkit.logic.persistence.entity.PersistenceObject
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.configuration.serialization.ConfigurationSerializable
import org.bukkit.util.Vector

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
class LocationBuilder() : PersistenceObject(), StorageLocation, ConfigurationSerializable {

    /** [worldName] which world the location is. */
    override var worldName: String? = null
    /** [x] coordinate. */
    override var x: Double = 0.0
    /** [y] coordinate. */
    override var y: Double = 0.0
    /** [z] coordinate. */
    override var z: Double = 0.0
    /** [yaw] rotation yaw. */
    override var yaw: Double = 0.0
    /** [pitch] rotation pitch. */
    override var pitch: Double = 0.0
    /** [blockX] coordinate as Int. */
    override val blockX: Int
        get() = x.toInt()
    /** [blockY] coordinate as Int. */
    override val blockY: Int
        get() = y.toInt()
    /** [blockZ] coordinate as Int. */
    override val blockZ: Int
        get() = z.toInt()

    constructor(location: Location) : this() {
        this.x = location.x
        this.y = location.y
        this.z = location.z
        this.yaw = location.yaw.toDouble()
        this.pitch = location.pitch.toDouble()
        this.worldName = location.world.name
    }

    constructor(data: Map<String, Any>) : this() {
        this.x = data["x"] as Double
        this.y = data["y"] as Double
        this.z = data["z"] as Double
        this.yaw = data["yaw"] as Double
        this.pitch = data["pitch"] as Double
        this.worldName = data["world"] as String
    }

    /**
     * Indicates whether some other object is "equal to" this one. Implementations must fulfil the following
     * requirements:
     *
     * * Reflexive: for any non-null reference value x, x.equals(x) should return true.
     * * Symmetric: for any non-null reference values x and y, x.equals(y) should return true if and only if y.equals(x) returns true.
     * * Transitive:  for any non-null reference values x, y, and z, if x.equals(y) returns true and y.equals(z) returns true, then x.equals(z) should return true
     * * Consistent:  for any non-null reference values x and y, multiple invocations of x.equals(y) consistently return true or consistently return false, provided no information used in equals comparisons on the objects is modified.
     *
     * Note that the `==` operator in Kotlin code is translated into a call to [equals] when objects on both sides of the
     * operator are not null.
     */
    override fun equals(other: Any?): Boolean {
        if (other is StorageLocation) {
            if (other.x == this.x && other.y == this.y && other.z == this.z) {
                return true
            }
            return false
        }
        return super.equals(other)
    }

    /**
     * Sets the coordinates x, y, z.
     *
     * @param x x
     * @param y y
     * @param z z
     * @return builder
     */
    override fun setCoordinates(x: Double, y: Double, z: Double): StorageLocation {
        this.x = x
        this.y = y
        this.z = z
        return this
    }

    /** Returns the location as bukkit Location. */
    fun toLocation(): Location {
        if (Bukkit.getWorld(worldName) == null)
            throw IllegalArgumentException("World $worldName is not loaded correctly. StorageLocationBuilder cannot execute correctly!")
        return Location(Bukkit.getWorld(worldName)!!, x, y, z, this.yaw.toFloat(), pitch.toFloat())
    }

    /** Returns the location as bukkit Vector. */
    fun toVector(): Vector {
        return Vector(x, y, z)
    }

    /** Serializes the given object. */
    override fun serialize(): MutableMap<String, Any> {
        val data = super.serialize()
        data["x"] = x
        data["y"] = y
        data["z"] = z
        data["yaw"] = yaw
        data["pitch"] = pitch
        if (worldName != null) {
            data["world"] = worldName!!
        }
        return data
    }
}