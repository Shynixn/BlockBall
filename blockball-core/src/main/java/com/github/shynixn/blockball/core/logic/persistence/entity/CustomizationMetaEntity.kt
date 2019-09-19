package com.github.shynixn.blockball.core.logic.persistence.entity

import com.github.shynixn.blockball.api.business.annotation.YamlSerialize
import com.github.shynixn.blockball.api.persistence.entity.CustomizationMeta

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
class CustomizationMetaEntity : CustomizationMeta {
    /** Can players damage other players during a game?*/
    @YamlSerialize(value = "damage-enabled", orderNumber = 1)
    override var damageEnabled: Boolean = false
    /** Should players be teleported back to their spawnpoint if someone scores?*/
    @YamlSerialize(value = "score-back-teleport", orderNumber = 2)
    override var backTeleport: Boolean = false
    /** After how many seconds should players be teleported back to their spawnpoint if [backTeleport] is enabled?*/
    @YamlSerialize(value = "score-back-teleport-delay", orderNumber = 3)
    override var backTeleportDelay: Int = 2
    /**
     * Should the ball be restricted by a forcefield to stay in the arena?
     */
    @YamlSerialize(value = "ball-forcefield", orderNumber = 4)
    override var ballForceField: Boolean = true

    /**
     * Should the players keep their inventory when they join a game?
     */
    @YamlSerialize(value = "keep-inventory", orderNumber = 4)
    override var keepInventoryEnabled: Boolean = false
}