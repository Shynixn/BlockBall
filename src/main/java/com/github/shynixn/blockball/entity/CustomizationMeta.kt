package com.github.shynixn.blockball.entity

import com.github.shynixn.mcutils.common.repository.Comment


class CustomizationMeta {
    @Comment("Should player take damage during a game?")
    var damageEnabled: Boolean = false

    @Comment("Should players be teleported back to their spawnpoint in the game when someone scores a goal?")
    var backTeleport: Boolean = false

    @Comment("The amount of seconds until the players are teleported back to their spawnpoint when someone scores a goal.")
    var backTeleportDelay: Int = 2

    @Comment("Should players keep their inventory when they join a game?")
    var keepInventoryEnabled: Boolean = false

    @Comment("Should players keep their current health when they join a game?")
    var keepHealthEnabled: Boolean = true

    @Comment("If set to true, the goal size is allowed to by very tiny. If set to false, the goal size has to be a minimum size.")
    var ignoreGoalSize: Boolean = false

    @Comment("When players queue for a game in gameType=MINIGAME, this is the timeout in seconds how long they can wait.")
    var queueTimeOutSec: Int = 30

    @Comment("The ball spawn delay in ticks when the game initially starts or when a new period is entered.")
    var gameStartBallSpawnDelayTicks: Int = 2

    @Comment("The ball spawn delay in ticks when spawning after a goal has been scored.")
    var goalScoredBallSpawnDelayTicks: Int = 1
}
