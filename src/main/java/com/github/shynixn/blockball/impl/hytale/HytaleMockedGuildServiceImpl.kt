package com.github.shynixn.blockball.impl.hytale

import com.github.shynixn.shyguild.contract.GuildService
import com.github.shynixn.shyguild.entity.Guild
import com.github.shynixn.shyguild.entity.GuildInvite
import org.bukkit.entity.Player
import java.util.UUID

class HytaleMockedGuildServiceImpl : GuildService {
    override suspend fun acceptInvite(player: Player, name: String): Boolean {
        return false
    }

    override suspend fun applyGuildMemberPermissions(
        playerUUID: UUID,
        guild: Guild
    ) {
    }

    override suspend fun cleanCache(player: Player) {
    }

    override suspend fun deleteGuild(guild: Guild) {
    }

    override suspend fun existsGuild(guildName: String): Boolean {
        return false
    }

    override fun getGuildCache(): List<Guild> {
        return emptyList()
    }

    override suspend fun getGuilds(player: Player): List<Guild> {
        return emptyList()
    }

    override suspend fun saveGuild(guild: Guild) {
    }

    override suspend fun sendInvite(invite: GuildInvite): Boolean {
        return false
    }

    override fun close() {
    }
}