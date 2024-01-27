package com.github.shynixn.blockball.entity

import com.github.shynixn.blockball.api.persistence.entity.Arena
import com.github.shynixn.blockball.api.persistence.entity.BungeeCordGame

class BungeeCordGameEntity(
    /**
     *  Arena of the game.
     */
    override val arena: Arena
) : MiniGameEntity(arena), BungeeCordGame {
    override var modt: String = ""
}
