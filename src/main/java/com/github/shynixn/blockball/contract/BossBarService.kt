package com.github.shynixn.blockball.contract

import com.github.shynixn.blockball.entity.BossBarMeta

interface BossBarService {

    /**
     * Adds the given [player] to this bossbar.
     * Does nothing if the player is already added.
     */
    fun <B, P> addPlayer(bossBar: B, player: P)

    /**
     * Removes the given [player] from this bossbar.
     * Does nothing if the player is already removed.
     */
    fun <B, P> removePlayer(bossBar: B, player: P)

    /**
     * Returns a list of all players watching thie bossbar.
     */
    fun <B, P> getPlayers(bossBar: B): List<P>

    /**
     * Changes the style of the bossbar with given [bossBarMeta].
     */
    fun <B, P> changeConfiguration(bossBar: B, title: String, bossBarMeta: BossBarMeta, player: P)

    /**
     * Generates a new bossbar from the given bossBar meta values.
     */
    fun <B> createNewBossBar(bossBarMeta: BossBarMeta): B

    /**
     * Clears all resources this [bossBar] has allocated from this service.
     */
    fun <B> cleanResources(bossBar: B)
}
