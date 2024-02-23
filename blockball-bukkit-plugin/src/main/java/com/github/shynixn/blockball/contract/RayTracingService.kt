package com.github.shynixn.blockball.contract

import com.github.shynixn.blockball.entity.Position
import com.github.shynixn.blockball.entity.RayTraceResult


interface RayTracingService {
    /**
     * Ray traces in the world for the given motion.
     */
    fun rayTraceMotion(position: Position, motion: Position): RayTraceResult
}
