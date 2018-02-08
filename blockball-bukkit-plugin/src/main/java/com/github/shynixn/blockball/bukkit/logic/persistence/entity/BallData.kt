package com.github.shynixn.blockball.bukkit.logic.persistence.entity

import com.github.shynixn.ball.api.bukkit.persistence.controller.BukkitBounceController
import com.github.shynixn.ball.api.bukkit.persistence.entity.BukkitParticleEffectMeta
import com.github.shynixn.ball.api.bukkit.persistence.entity.BukkitSoundEffectMeta
import com.github.shynixn.ball.api.persistence.controller.BounceController
import com.github.shynixn.ball.api.persistence.effect.ParticleEffectMeta
import com.github.shynixn.ball.api.persistence.effect.SoundEffectMeta
import com.github.shynixn.ball.bukkit.core.logic.persistence.entity.BallData
import com.github.shynixn.blockball.api.persistence.entity.BallExtensionMeta
import com.github.shynixn.blockball.api.persistence.entity.basic.StorageLocation

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
class BallData(skin : String) : BallData(skin), BallExtensionMeta {
    /** Spawning delay. */
    override var delayInTicks: Int
        get() = TODO("not implemented") //To change initializer of created properties use File | Settings | File Templates.
        set(value) {}
    /** Spawnpoint of the ball. */
    override var spawnpoint: StorageLocation
        get() = TODO("not implemented") //To change initializer of created properties use File | Settings | File Templates.
        set(value) {}
    /** Returns the id of the object. */
    override val id: Long
        get() = TODO("not implemented") //To change initializer of created properties use File | Settings | File Templates.

    /**
     * Returns a controller for all bounce Objects.
     *
     * @return list
     */
    override fun getBounceObjectController(): BukkitBounceController {
        return super.getBounceObjectController()
    }
}