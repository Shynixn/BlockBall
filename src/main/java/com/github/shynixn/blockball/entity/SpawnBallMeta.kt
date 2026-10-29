package com.github.shynixn.blockball.entity

import com.github.shynixn.mcutils.common.Vector3d
import com.github.shynixn.mcutils.common.repository.Element

class SpawnBallMeta : Element {
    override var name: String = ""

    var ballName: String = ""

    var removeAfterSeconds: Int = 60

    var spawnRadius: Int = 50

    var enabled : Boolean = false

    var spawnpoint: Vector3d = Vector3d(0.0, 0.0, 0.0)
}