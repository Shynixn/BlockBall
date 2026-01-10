package com.github.shynixn.blockball.entity

import com.github.shynixn.blockball.entity.cloud.CloudMeta

class ArenaMeta {
    /** Metadata of the customizing properties. */
    val customizingMeta: CustomizationMeta = CustomizationMeta()

    /** Meta data of a generic lobby. */
    val lobbyMeta: LobbyMeta = LobbyMeta()

    /** Metadata of the hub lobby. */
    var hubLobbyMeta: HubLobbyMeta = HubLobbyMeta()

    /** Meta data of the minigame lobby. */
    val minigameMeta: MinigameLobbyMeta = MinigameLobbyMeta()

    /** Meta data of the doubleJump. */
    val doubleJumpMeta: DoubleJumpMeta = DoubleJumpMeta()

    /** Meta data of proection. */
    val protectionMeta: ArenaProtectionMeta = ArenaProtectionMeta()

    /** Metadata of the blueTeam. */
    val blueTeamMeta: TeamMeta = TeamMeta()

    /** Meta data of the redTeam. */
    val redTeamMeta: TeamMeta = TeamMeta()

    /** Metadata of the refereeTeam. */
    val refereeTeamMeta: TeamMeta = TeamMeta()

    /** Metadata of the publishing properties. */
    val cloudMeta: CloudMeta = CloudMeta()

    init {
        blueTeamMeta.displayName = "&9Team Blue"
        redTeamMeta.displayName = "&cTeam Red"
        refereeTeamMeta.displayName = "&fTeam Referee"
    }
}
