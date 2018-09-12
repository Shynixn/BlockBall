package com.github.shynixn.blockball.bukkit.logic.compatibility;

import com.github.shynixn.blockball.api.compatibility.BounceObject;
import com.github.shynixn.blockball.bukkit.logic.business.nms.MaterialCompatibility13;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.serialization.ConfigurationSerializable;

import java.util.LinkedHashMap;
import java.util.Map;

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
public class BounceInfo implements BounceObject, ConfigurationSerializable {

    private int id;
    private int damage;
    private double modifier = 1.0;

    /**
     * Initializes a new bounce info.
     *
     * @param id id
     */
    public BounceInfo(int id) {
        this.id = id;
    }

    /**
     * Initializes a new bounce info.
     *
     * @param data data
     */
    public BounceInfo(Map<String, Object> data) {
        if (data == null)
            throw new IllegalArgumentException("Data cannot be null!");
        this.id = (int) data.get("id");
        this.damage = (int) data.get("damage");
        this.modifier = (double) data.get("modifier");
    }

    /**
     * Returns the material id of the block bouncing off.
     *
     * @return id
     */
    @Override
    public int getMaterialId() {
        return this.id;
    }

    /**
     * Sets the material id of the block bouncing off.
     *
     * @param id id
     */
    @Override
    public void setMaterialId(int id) {
        this.id = id;
    }

    /**
     * Returns the damage Value of the block bouncing off.
     *
     * @return damageValue
     */
    @Override
    public int getMaterialDamageValue() {
        return this.damage;
    }

    /**
     * Sets the damage Value of the block bouncing off.
     *
     * @param damageValue damageValue
     */
    @Override
    public void setMaterialDamageValue(int damageValue) {
        this.damage = damageValue;
    }

    /**
     * Returns how much the ball velocity should be multiplied when hitting this block.
     *
     * @return strength
     */
    @Override
    public double getBounceModifier() {
        return this.modifier;
    }

    /**
     * Sets how much the ball velocity should be multiplied when hitting this block.
     *
     * @param strength strength
     */
    @Override
    public void setBounceModifier(double strength) {
        this.modifier = strength;
    }

    /**
     * Returns if the given block is of this type.
     *
     * @param block block
     * @return isType
     */
    @Override
    public boolean isBlock(Object block) {
        final Block bukkitBlock = (Block) block;
        if (bukkitBlock.getType() == this.getMaterial()) {
            if (bukkitBlock.getData() == this.getMaterialDamageValue()) {
                return true;
            }
        }
        return false;
    }

    /**
     * Serializes the bounceInfo
     *
     * @return serializedContent.
     */
    @Override
    public Map<String, Object> serialize() {
        final Map<String, Object> data = new LinkedHashMap<>();
        data.put("id", this.id);
        data.put("damage", this.damage);
        data.put("modifier", this.modifier);
        return data;
    }

    private Material getMaterial() {
        return MaterialCompatibility13.getMaterialFromId(this.getMaterialId());
    }
}
