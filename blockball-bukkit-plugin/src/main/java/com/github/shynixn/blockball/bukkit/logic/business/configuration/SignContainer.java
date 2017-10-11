package com.github.shynixn.blockball.bukkit.logic.business.configuration;

public class SignContainer {
        private final String line1;
        private final String line2;
        private final String line3;
        private final String line4;

        public SignContainer(String upper) {
            super();
            this.line1 = ConfigOld.this.c.getString(upper + ".line-1");
            this.line2 = ConfigOld.this.c.getString(upper + ".line-2");
            this.line3 = ConfigOld.this.c.getString(upper + ".line-3");
            this.line4 = ConfigOld.this.c.getString(upper + ".line-4");
        }

        public void updateTeamSignConsideringMaxPlayers(Sign sign, Game game, Team team, List<Player> players) {
            int i = 0;
            for (final String s : new String[]{this.line1, this.line2, this.line3, this.line4}) {
                if (team == Team.BLUE)
                    sign.setLine(i, this.replace(s, game.getArena(), team, true).replace("<players>", String.valueOf(players.size()))
                            .replace("<maxplayers>", String.valueOf(game.getArena().getTeamMeta().getTeamMaxSize())));
                if (team == Team.RED)
                    sign.setLine(i, this.replace(s, game.getArena(), team, true).replace("<players>", String.valueOf(players.size()))
                            .replace("<maxplayers>", String.valueOf(game.getArena().getTeamMeta().getTeamMaxSize())));
                i++;
            }
            sign.update();
        }

        public List<Player> getAmountFromTeam(Team team, Map<Player, Team> preselection) {
            final List<Player> players = new ArrayList<>();
            for (final Player player : preselection.keySet()) {
                if (preselection.get(player) == team)
                    players.add(player);
            }
            return players;
        }

        public void updateTeamSignConsideringMinigame(Sign sign, Game game, Team team, Map<Player, Team> preselection) {
            int i = 0;
            List<Player> players = null;
            for (final String s : new String[]{this.line1, this.line2, this.line3, this.line4}) {
                if (team == Team.RED) {
                    if (game.getRedTeamPlayers().length > this.getAmountFromTeam(Team.RED, preselection).size()) {
                        players = Arrays.asList(game.getRedTeamPlayers());
                    } else {
                        players = this.getAmountFromTeam(Team.RED, preselection);
                    }
                }
                if (team == Team.BLUE) {
                    if (game.getBlueTeamPlayers().length > this.getAmountFromTeam(Team.BLUE, preselection).size()) {
                        players = Arrays.asList(game.getBlueTeamPlayers());
                    } else {
                        players = this.getAmountFromTeam(Team.BLUE, preselection);
                    }
                }
                sign.setLine(i, this.replace(s, game.getArena(), team, true).replace("<players>", String.valueOf(players.size()))
                        .replace("<maxplayers>", String.valueOf(game.getArena().getTeamMeta().getTeamMaxSize())));
                i++;
            }
            sign.update();
        }

        public void updateJoinSignConsideringMaxPlayers(Sign sign, Game game, List<Player> players) {
            int i = 0;
            final MiniGame g = (MiniGame) game;
            String stage = "unknown";
            if (g.getStage() == GameStage.DISABLED)
                stage = Language.SIGN_DISABLED;
            else if (g.getStage() == GameStage.ENABLED)
                stage = Language.SIGN_ENABLED;
            else if (g.getStage() == GameStage.RUNNING)
                stage = Language.SIGN_RUNNING;
            for (final String s : new String[]{this.line1, this.line2, this.line3, this.line4}) {
                if (game.getPlayers().size() < players.size()) {
                    sign.setLine(i, this.replace(s, game.getArena(), null, true).replace("<players>", String.valueOf(players.size()))
                            .replace("<maxplayers>", String.valueOf(game.getArena().getLobbyMeta().getMaxPlayers() * 2))
                            .replace("<state>", stage));
                } else {
                    sign.setLine(i, this.replace(s, game.getArena(), null, true).replace("<players>", String.valueOf(game.getPlayers().size()))
                            .replace("<maxplayers>", String.valueOf(game.getArena().getLobbyMeta().getMaxPlayers() * 2))
                            .replace("<state>", stage));
                }

                i++;
            }
            sign.update();
        }

        public int getGameLine() {
            int i = 0;
            for (final String s : new String[]{this.line1, this.line2, this.line3, this.line4}) {
                if (s.equals("<game>")) {
                    return i;
                }
                i++;
            }
            return -1;
        }

        private String replace(String data, Arena arena, Team team, boolean ignoreDynmic) {
            if (arena.getAlias() == null)
                data = data.replace("<game>", String.valueOf(arena.getId()));
            else
                data = data.replace("<game>", arena.getAlias());
            if (team != null) {
                if (team == Team.RED)
                    data = data.replace("<team>", arena.getTeamMeta().getRedTeamName());
                else
                    data = data.replace("<team>", arena.getTeamMeta().getBlueTeamName());
            }
            if (!ignoreDynmic) {
                data = data.replace("<players>", "0");
                data = data.replace("<maxplayers>", "0");
                data = data.replace("<state>", Language.SIGN_DISABLED);
            }
            return ChatColor.translateAlternateColorCodes('&', data);
        }

        public String getLine1(Arena arena, Team team) {
            return this.replace(this.line1, arena, team, false);
        }

        public String getLine2(Arena arena, Team team) {
            return this.replace(this.line2, arena, team, false);
        }

        public String getLine3(Arena arena, Team team) {
            return this.replace(this.line3, arena, team, false);
        }

        public String getLine4(Arena arena, Team team) {
            return this.replace(this.line4, arena, team, false);
        }
    }