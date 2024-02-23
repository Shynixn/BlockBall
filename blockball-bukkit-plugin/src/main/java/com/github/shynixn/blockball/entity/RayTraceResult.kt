package com.github.shynixn.blockball.entity

import com.github.shynixn.blockball.enumeration.BlockDirection

class RayTraceResult(val hitBlock: Boolean, val targetPosition: Position,
                     var blockdirection: BlockDirection
)
