package com.github.shynixn.blockball.impl.exception

import com.github.shynixn.blockball.entity.SoccerArena

class SoccerGameException(val arena: SoccerArena, message: String) : RuntimeException(message) {

}
