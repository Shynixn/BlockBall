package com.github.shynixn.blockball.business.logic.arena;

import com.github.shynixn.blockball.api.entities.Arena;
import com.github.shynixn.blockball.business.Config;
import com.github.shynixn.blockball.business.logic.game.GameController;
import com.github.shynixn.blockball.lib.*;
import com.github.shynixn.blockball.api.events.GoalShootEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;

import java.util.*;

public final class ArenaController {
    @SPluginLoader.PluginLoader
    private static JavaPlugin plugin;
    private static final String[] A = new String[0];
    private static final int MAX_AMOUNT_ARENA = 10000000;
    private final ArenaFileManager fileManager;
    private final List<ArenaEntity> arenas = new ArrayList<>();

    final GameController manager;

    private ArenaController(GameController manager) {
        super();
        this.fileManager = new ArenaFileManager(plugin);
        this.manager = manager;
        new ArenaCommandExecutor(this);
        new BlockBallCommandExecutor();
        if (Config.getInstance().isEnableGoalsScoreboard())
            new ArenaShortListener();
    }

    public void persist(Arena arena) {
        if (arena != null) {
            final int id = Integer.parseInt(arena.getName());
            if (!this.contains(id)) {
                this.arenas.add((ArenaEntity) arena);
            }
            this.fileManager.save(arena);
        }
    }

    public void remove(Arena arena) {
        this.fileManager.delete(arena);
        this.reload();
    }

    public boolean contains(int id) {
        for (final ArenaEntity entity : this.arenas) {
            if (entity.getId() == id)
                return true;
        }
        return false;
    }

    public void reload() {
        this.arenas.clear();
        for (Arena arena : this.fileManager.load()) {
            this.arenas.add((ArenaEntity) arena);
        }
    }

    ArenaEntity createNewArenaEntity() {
        final ArenaEntity arenaEntity = new ArenaEntity();
        arenaEntity.setName(String.valueOf(this.getNewId()));
        return arenaEntity;
    }

    public List<Arena> getArenas() {
        return Arrays.asList(this.arenas.toArray(new Arena[this.arenas.size()]));
    }

    private ArenaFileManager getFileManager() {
        return this.fileManager;
    }

    private int getNewId() {
        for (int i = 0; i < MAX_AMOUNT_ARENA; i++) {
            final String s = String.valueOf(i);
            if (this.getArenaFromName(s) == null) {
                return i;
            }
        }
        return -1;
    }

    Arena getArenaFromName(String name) {
        for (final Arena arena : this.arenas) {
            if (arena.getName().equalsIgnoreCase(name))
                return arena;
        }
        return null;
    }


    public static ArenaController createArenaController(final GameController manager) {
        return new ArenaController(manager);
    }

    private class ArenaShortListener extends SEvents {
        private final Map<Player, Integer> players = new HashMap<>();
        private Map<String, Integer> topTenNumbers = new HashMap<>();
        private final Scoreboard scoreboard;
        private final Objective objective;

        ArenaShortListener() {
            super();
            this.scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
            this.objective = this.scoreboard.registerNewObjective("blockballstats", "dummy");
            this.objective.setDisplaySlot(DisplaySlot.SIDEBAR);
            this.objective.setDisplayName(Config.getInstance().getScoreboardTitle());
            AsyncRunnable.toAsynchroneThread(new AsyncRunnable() {
                @Override
                public void run() {
                    ArenaShortListener.this.topTenNumbers = ArenaController.this.getFileManager().getTopTenPlayers();
                    AsyncRunnable.toSynchroneThread(new AsyncRunnable() {
                        @Override
                        public void run() {
                            ArenaShortListener.this.refreshScoreboard();
                            for (final Player player1 : SFileUtils.getOnlinePlayers()) {
                                if (!player1.getScoreboard().equals(ArenaShortListener.this.scoreboard))
                                    player1.setScoreboard(ArenaShortListener.this.scoreboard);
                            }
                        }
                    });
                }
            });
        }

        private void refreshScoreboard() {
            final List<Map.Entry<String, Integer>> items = this.getTopTen();
            for (final String s : this.scoreboard.getEntries()) {
                this.scoreboard.resetScores(s);
            }
            for (int i = 0; i < items.size(); i++) {
                final Score score;
                if (i == 0)
                    score = this.objective.getScore(Config.getInstance().getFirstplaceprefix() + items.get(i).getKey());
                else if (i == 1)
                    score = this.objective.getScore(Config.getInstance().getSecondplaceprefix() + items.get(i).getKey());
                else if (i == 2)
                    score = this.objective.getScore(Config.getInstance().getThirdplaceprefix() + items.get(i).getKey());
                else
                    score = this.objective.getScore(Config.getInstance().getOtherprefix() + items.get(i).getKey());
                score.setScore(items.get(i).getValue());
            }
        }

        List<Map.Entry<String, Integer>> getTopTen() {
            return SMathUtils.entriesSortedByValues(this.topTenNumbers);
        }

        @EventHandler
        public void onGoalEvent(GoalShootEvent event) {
            this.addGoal(event.getPlayer());
        }

        @EventHandler
        public void onPlayerJoinEvent(PlayerJoinEvent event) {
            if (this.objective != null) {
                event.getPlayer().setScoreboard(this.scoreboard);
            }
        }

        @EventHandler
        public void onPlayerLeaveEvent(PlayerQuitEvent event) {
            if (this.objective != null) {
                event.getPlayer().setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
            }
        }

        void addGoal(final Player player) {
            if (this.topTenNumbers.containsKey(player.getName()))
                this.topTenNumbers.put(player.getName(), this.topTenNumbers.get(player.getName()) + 1);
            for (final Player player1 : SFileUtils.getOnlinePlayers()) {
                if (!player1.getScoreboard().equals(this.scoreboard))
                    player1.setScoreboard(this.scoreboard);
            }
            AsyncRunnable.toAsynchroneThread(new AsyncRunnable() {
                @Override
                public void run() {
                    synchronized (ArenaShortListener.this.players) {
                        if (!ArenaShortListener.this.players.containsKey(player)) {
                            ArenaShortListener.this.players.put(player, ArenaController.this.getFileManager().getNumber(player));
                        }
                        ArenaShortListener.this.players.put(player, ArenaShortListener.this.players.get(player) + 1);
                        final int number = ArenaShortListener.this.players.get(player);
                        synchronized (ArenaController.this.getFileManager()) {
                            ArenaController.this.getFileManager().save(player, number);
                        }
                        AsyncRunnable.toSynchroneThread(new AsyncRunnable() {
                            @Override
                            public void run() {
                                if (ArenaShortListener.this.topTenNumbers.size() < 10) {
                                    ArenaShortListener.this.topTenNumbers.put(player.getName(), number);
                                } else if (!ArenaShortListener.this.topTenNumbers.containsKey(player.getName())) {
                                    for (final String name : ArenaShortListener.this.topTenNumbers.keySet().toArray(A)) {
                                        if (ArenaShortListener.this.topTenNumbers.get(name) < number) {
                                            ArenaShortListener.this.topTenNumbers.remove(name);
                                            ArenaShortListener.this.topTenNumbers.put(player.getName(), number);
                                            break;
                                        }
                                    }
                                }
                                ArenaShortListener.this.refreshScoreboard();
                            }
                        });
                    }
                }
            });
        }
    }
}
