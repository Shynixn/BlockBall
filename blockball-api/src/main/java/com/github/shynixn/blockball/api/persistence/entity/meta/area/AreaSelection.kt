package com.github.shynixn.blockball.api.persistence.entity.meta.area

import com.github.shynixn.blockball.api.persistence.entity.Persistenceable
import com.github.shynixn.blockball.api.persistence.entity.basic.IPosition

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
interface AreaSelection<Location> : Persistenceable {

    /** [upperCorner] of the selected square arena. */
    val upperCorner: IPosition?

    /** [lowerCorner] of the selected square arena. */
    val lowerCorner: IPosition?

    /** [center] of the arena */
    val center: Location?

    /** Length of the x axe. */
    val offsetX: Int

    /** Length of the y axe. */
    val offsetY: Int

    /** Length of the z axe. */
    val offsetZ: Int

    /** Sets the corners between [corner1] and [corner2]. Automatically sets lowerCorner and upperCorner. */
    fun setCorners(corner1: Location, corner2: Location)

    /** Returns if the given [location] is inside of this area selection. */
    fun isLocationInSelection(location: Location): Boolean
}