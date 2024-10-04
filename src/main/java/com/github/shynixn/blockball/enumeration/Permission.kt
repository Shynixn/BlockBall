package com.github.shynixn.blockball.enumeration

enum class Permission(val permission: String) {
    COMMAND("blockball.command"),
    EDIT_GAME("blockball.edit"),

    JOIN("blockball.join.[name]"),

    /**
     * Permission for staff to allow executing commands while ingame.
     */
    OBSOLETE_STAFF("blockball.command.staff"),

    /**
     * Permission for users to allow opening and clicking their inventory while ingame.
     */
    OBSOLETE_INVENTORY("blockball.game.inventory"),

    /**
     * Allows to join the referee team.
     */
    REFEREE_JOIN("blockball.referee.join"),
}
