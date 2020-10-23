package com.github.shynixn.blockball.core.logic.persistence.entity

import com.github.shynixn.blockball.api.persistence.entity.Position
import com.github.shynixn.blockball.api.persistence.entity.RaytraceResult

class RayTraceResultEntity(override val hitBlock: Boolean, override val targetPosition: Position) : RaytraceResult {
}
