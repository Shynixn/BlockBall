package com.github.shynixn.blockball.entity

import com.github.shynixn.blockball.api.business.annotation.YamlSerialize
import com.github.shynixn.blockball.api.business.enumeration.GameMode
import com.github.shynixn.blockball.api.persistence.entity.LobbyMeta
import com.github.shynixn.blockball.api.persistence.entity.Position
import com.github.shynixn.mcutils.common.ChatColor

class LobbyMetaEntity : LobbyMeta {
    /** Max score of a team until the match ends and the arena gets reset. */
    @YamlSerialize(value = "max-score", orderNumber = 1)
    override var maxScore: Int = 10

    /** Should players automatically join the other team to even out them?*/
    @YamlSerialize(value = "even-teams", orderNumber = 2)
    override var onlyAllowEventTeams: Boolean = false

    /** List of signs which can be clicked to join the game. */
    override val joinSigns: MutableList<Position>
        get() = sign.joinSigns as MutableList<Position>

    /** Lines displayed on the sign for leaving the match. */
    @YamlSerialize(orderNumber = 3, value = "join-sign-lines", implementation = List::class)
    override var joinSignLines: List<String> = arrayListOf(
        "%blockball_lang_joinSignLine1%",
        "%blockball_lang_joinSignLine2%",
        "%blockball_lang_joinSignLine3%",
        "%blockball_lang_joinSignLine4%"
    )
    /** Lines displayed on the sign for leaving the match. */
    @YamlSerialize(
        orderNumber = 4,
        value = "leave-sign-lines",
        implementation = List::class
    ) // Compatibility implementation. Support will be removed after August 2018.
    override var leaveSignLines: List<String> = arrayListOf(
        "%blockball_lang_leaveSignLine1%",
        "%blockball_lang_leaveSignLine2%",
        "%blockball_lang_leaveSignLine3%",
        "%blockball_lang_leaveSignLine4%"
    )

    /** List of signs which can be clicked to leave the game. */
    override val leaveSigns: MutableList<Position>
        get() = sign.leaveSigns as MutableList<Position>

    /** Spawnpoint when someone leaves the hub game. */
    @YamlSerialize(orderNumber = 5, value = "leave-spawnpoint", implementation = PositionEntity::class)
    override var leaveSpawnpoint: Position? = null

    /** Minecraft gamemode (Survival, Adventure, Creative) the players should be */
    @YamlSerialize(orderNumber = 6, value = "gamemode", implementation = GameMode::class)
    override var gamemode: GameMode = GameMode.ADVENTURE

    @YamlSerialize(orderNumber = 7, value = "signs")
    private val sign: SignCollection = SignCollection()
}
