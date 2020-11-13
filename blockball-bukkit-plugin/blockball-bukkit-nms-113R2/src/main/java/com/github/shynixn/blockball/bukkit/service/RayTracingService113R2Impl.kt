package com.github.shynixn.blockball.bukkit.service

import com.github.shynixn.blockball.api.business.enumeration.BlockDirection
import com.github.shynixn.blockball.api.business.service.RayTracingService
import com.github.shynixn.blockball.api.persistence.entity.Position
import com.github.shynixn.blockball.api.persistence.entity.RaytraceResult
import com.github.shynixn.blockball.core.logic.persistence.entity.PositionEntity
import com.github.shynixn.blockball.core.logic.persistence.entity.RayTraceResultEntity
import net.minecraft.server.v1_13_R2.EntityLiving

class RayTracingService113R2Impl : RayTracingService {
    /**
     * Ray traces in the world for the given motion.
     */
    override fun rayTraceMotion(position: Position, motion: Position): RaytraceResult {
        return RayTraceResultEntity(true, PositionEntity(), BlockDirection.SOUTH)
    }
}
