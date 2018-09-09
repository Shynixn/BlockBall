package com.github.shynixn.blockball.api.compatibility;

/**
 * Created by Shynixn 2017.
 * <p>
 * Version 1.1
 * <p>
 * MIT License
 * <p>
 * Copyright (c) 2017 by Shynixn
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
public interface BounceObject {

    /**
     * Returns the material id of the block bouncing off.
     *
     * @return id
     */
    int getMaterialId();

    /**
     * Sets the material id of the block bouncing off.
     *
     * @param id id
     */
    void setMaterialId(int id);

    /**
     * Returns the damage Value of the block bouncing off.
     *
     * @return damageValue
     */
    int getMaterialDamageValue();

    /**
     * Sets the damage Value of the block bouncing off.
     *
     * @param damageValue damageValue
     */
    void setMaterialDamageValue(int damageValue);

    /**
     * Returns how much the ball velocity should be multiplied when hitting this block.
     *
     * @return strength
     */
    double getBounceModifier();

    /**
     * Sets how much the ball velocity should be multiplied when hitting this block.
     *
     * @param strength strength
     */
    void setBounceModifier(double strength);

    /**
     * Returns if the given block is of this type.
     *
     * @param block block
     * @return isType
     */
    boolean isBlock(Object block);
}
