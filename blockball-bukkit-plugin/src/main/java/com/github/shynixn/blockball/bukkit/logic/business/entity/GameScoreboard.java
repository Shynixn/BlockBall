package com.github.shynixn.blockball.bukkit.logic.business.entity;

/*
class GameScoreboard extends SimpleScoreboard {


     * Initializes a fresh new Scoreboard

    GameScoreboard(Arena arena) {
        super();
        this.setDefaultObjective(SimpleScoreboard.DUMMY_TYPE);
        this.setDefaultTitle(arena.getTeamMeta().getScoreboardTitle());
        this.setDefaultDisplaySlot(DisplaySlot.SIDEBAR);
    }


     * Updates the scoreboard for all added players
     *
     * @param gameEntity gameEntity

    void update(GameEntity gameEntity) {
        final String[] lines = gameEntity.getArena().getTeamMeta().getScoreboardLines();
        for (int i = 0, j = lines.length; i < lines.length; i++, j--) {
            final String line = lines[i];
            this.setDefaultLine(j, gameEntity.decryptText(line));
        }
    }
}

*/