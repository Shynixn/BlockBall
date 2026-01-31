package com.github.shynixn.blockball.entity

import com.github.shynixn.blockball.enumeration.GameType
import com.github.shynixn.mcutils.common.Vector3d
import com.github.shynixn.mcutils.common.repository.Comment
import com.github.shynixn.mcutils.common.repository.Element

@Comment(
    "###############",
    "",
    "This is the configuration for one arena in the Minecraft plugin BlockBall.",
    "",
    "###############"
)
class SoccerArena : Selection(), Element {
    @Comment("The unique identifier of a soccer arena. Should be identical to the name of this file.")
    override var name: String = ""

    @Comment("A version number for this arena configuration. Do not change this unless you know what you are doing.")
    var version: Int = 1

    @Comment("A display name for this arena, which can contain chat colors and more. This name can be used in messages using the placeholder %blockball_game_displayName%.")
    var displayName: String = ""

    @Comment("Allows to enable to disable the arena.")
    var enabled: Boolean = false

    @Comment("The type of BlockBall game. The supported types are HUBGAME, MINIGAME and REFEREEGAME. REFEREEGAME is PatreonOnly.")
    var gameType: GameType = GameType.HUBGAME

    @Comment("Corner 1 of the playable field.")
    override var corner1: Vector3d? = null

    @Comment("Corner 2 of the playable field.")
    override var corner2: Vector3d? = null

    @Comment("Spawn location of the ball.")
    var ballSpawnPoint: Vector3d? = null

    @Comment("All settings related to the ball.")
    var ball: SoccerBallMeta = SoccerBallMeta()

    @Comment("Outer field location properties.")
    var outerField: Selection = Selection()

    @Comment("All configurable meta data of this arena.")
    var meta: ArenaMeta = ArenaMeta()
}
