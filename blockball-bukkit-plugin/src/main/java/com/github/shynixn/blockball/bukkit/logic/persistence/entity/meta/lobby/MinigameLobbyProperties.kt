package com.github.shynixn.blockball.bukkit.logic.persistence.entity.meta.lobby

import com.github.shynixn.blockball.api.persistence.entity.basic.StorageLocation
import com.github.shynixn.blockball.api.persistence.entity.meta.lobby.MinigameLobbyMeta
import com.github.shynixn.blockball.bukkit.logic.business.helper.YamlSerializer
import com.github.shynixn.blockball.bukkit.logic.persistence.entity.PersistenceObject
import com.github.shynixn.blockball.bukkit.logic.persistence.entity.basic.LocationBuilder

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
class MinigameLobbyProperties : PersistenceObject(), MinigameLobbyMeta {
    /** Duration the match will max last. */
    @YamlSerializer.YamlSerialize(orderNumber = 2, value = "lobby-duration")
    override var lobbyDuration: Int = 20
    /** Duration the match will max last. */
    @YamlSerializer.YamlSerialize(orderNumber = 1, value = "match-duration")
    override var matchDuration: Int = 300
    /** Spawnpoint of the player in the lobby. */
    override var lobbySpawnpoint: StorageLocation?
        get() {
            return this.internalLocation
        }
        set(value) {
            this.internalLocation = value as LocationBuilder;
        }

    @YamlSerializer.YamlSerialize(orderNumber = 3, value = "lobby-spawnpoint")
    private var internalLocation: LocationBuilder? = null
}