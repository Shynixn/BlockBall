package com.github.shynixn.blockball.api.business.service

import com.github.shynixn.blockball.api.persistence.entity.Position
import com.github.shynixn.blockball.api.persistence.entity.RaytraceResult

interface RayTracingService {
    /**
     * Ray traces in the world for the given motion.
     */
    fun rayTraceMotion(position: Position, motion: Position): RaytraceResult
}
