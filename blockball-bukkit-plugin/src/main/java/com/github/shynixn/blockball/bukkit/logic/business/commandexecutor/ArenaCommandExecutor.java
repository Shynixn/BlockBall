package com.github.shynixn.blockball.bukkit.logic.business.commandexecutor;

import com.github.shynixn.blockball.api.persistence.entity.PotionEffectMeta;
import com.github.shynixn.blockball.api.persistence.entity.SoundMeta;
import com.github.shynixn.blockball.bukkit.dependencies.worldedit.WorldEditConnection;
import com.github.shynixn.blockball.bukkit.logic.business.BlockBallManager;
import com.github.shynixn.blockball.bukkit.logic.business.configuration.Config;
import com.github.shynixn.blockball.bukkit.logic.business.configuration.Language;
import com.github.shynixn.blockball.bukkit.logic.business.configuration.SChatMenuManager;
import com.github.shynixn.blockball.bukkit.logic.business.configuration.SChatpage;
import com.github.shynixn.blockball.bukkit.logic.business.entity.ItemSpawner;
import com.github.shynixn.blockball.bukkit.logic.persistence.entity.PotionEffectBuilder;
import com.github.shynixn.blockball.bukkit.logic.persistence.entity.SoundBuilder;
import com.github.shynixn.blockball.bukkit.nms.NMSRegistry;
import com.github.shynixn.blockball.lib.FastBossBar;
import com.github.shynixn.blockball.lib.SFileUtils;
import com.github.shynixn.blockball.lib.SimpleCommandExecutor;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

class ArenaCommandExecutor extends SimpleCommandExecutor.Registered {
    private static final String HEADER_STANDARD = ChatColor.WHITE + "" + ChatColor.BOLD + "" + ChatColor.UNDERLINE + "                         Balls                      ";
    private static final String FOOTER_STANDARD = ChatColor.WHITE + "" + ChatColor.BOLD + "" + ChatColor.UNDERLINE + "                           ┌1/1┐                            ";

    private static final String MENU_BACK = "b - Go back";
    private static final String MENU_EXIT = "e - Close chat menu";

    private final BlockBallManager manager;

    ArenaCommandExecutor(BlockBallManager manager, Plugin plugin) {
        super("blockball", (JavaPlugin) plugin);
        this.manager = manager;
        if (Config.getInstance().getChatNavigateCommand().isEnabled()) {
            final Config.CommandContainer c = Config.getInstance().getChatNavigateCommand();
            new SimpleCommandExecutor.UnRegistered(c.getCommand(), c.getUseage(), c.getDescription(), c.getPermission(), c.getPermissionMessage(), (JavaPlugin) plugin) {
                /**
                 * Can be overwritten to listen to player executed commands
                 *
                 * @param player player
                 * @param args   args
                 */
                @Override
                public void onPlayerExecuteCommand(Player player, String[] args) {
                    SChatMenuManager.getInstance().handleChatMessage(player, ChatColor.stripColor(this.getText(args)));
                }

                /**
                 * Returns the text
                 * @param args args
                 * @return text
                 */
                private String getText(String[] args) {
                    final StringBuilder b = new StringBuilder();
                    for (final String s : args) {
                        b.append(s);
                    }
                    return b.toString();
                }
            };
        }
    }

    private class FirstPage extends SChatpage {
        FirstPage(Player player) {
            super(player);
        }

        @Override
        public void onPlayerSelect(int number) {
            if (number == 1) {
                this.open(this.player, new EditArenaPage(this.player, ArenaCommandExecutor.this.manager.createNewArenaEntity()));
            } else if (number == 2) {
                this.open(this.player, new EditArenaList(this.player, 3));
            } else if (number == 3) {
                this.open(this.player, new EditArenaList(this.player, 1));
            } else if (number == 4) {
                this.open(this.player, new EditArenaList(this.player, 2));
            }
        }

        @Override
        public void show() {
            this.player.sendMessage("");
            this.player.sendMessage(HEADER_STANDARD);
            this.player.sendMessage("");
            this.player.sendMessage(Language.PREFIX + "1 - Create new arena");
            this.player.sendMessage(Language.PREFIX + "2 - Copy and create arena");
            this.player.sendMessage(Language.PREFIX + "3 - Edit arena");
            this.player.sendMessage(Language.PREFIX + "4 - List arenas");
            this.player.sendMessage(Language.PREFIX + ChatColor.RED + MENU_EXIT);
            this.player.sendMessage("");
            this.player.sendMessage(FOOTER_STANDARD);
            this.player.sendMessage("");
        }
    }

    private class EditArenaList extends SChatpage {
        private int type = -1;
        private ArenaEntity arenaEntity;

        EditArenaList(Player player, int type) {
            super(player);
            this.type = type;
        }

        @Override
        public void onPlayerSelect(int number) {
            if (this.type == 1) {
                if (ArenaCommandExecutor.this.manager.contains(number)) {
                    final ArenaEntity arenaEntity = (ArenaEntity) ArenaCommandExecutor.this.manager.getArenaFromName(String.valueOf(number));
                    this.open(this.player, new EditArenaPage(this.player, arenaEntity));
                }
            } else if (this.type == 2) {
                if (ArenaCommandExecutor.this.manager.contains(number)) {
                    this.arenaEntity = (ArenaEntity) ArenaCommandExecutor.this.manager.getArenaFromName(String.valueOf(number));
                    this.show();
                }
            }
            if (this.type == 3) {
                if (ArenaCommandExecutor.this.manager.contains(number)) {
                    final ArenaEntity arenaEntity = (ArenaEntity) ArenaCommandExecutor.this.manager.getArenaFromName(String.valueOf(number));
                    final ArenaEntity newentity = ArenaCommandExecutor.this.manager.createNewArenaEntity();
                    newentity.setBallMeta((BallMetaEntity) arenaEntity.getBallMeta().clone());
                    try {
                        ((TeamMetaEntity) arenaEntity.getTeamMeta()).copy((TeamMetaEntity) newentity.getTeamMeta());
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }

                    this.open(this.player, new EditArenaPage(this.player, newentity));
                }
            }
        }

        @Override
        public void show() {
            this.player.sendMessage("");
            this.player.sendMessage(HEADER_STANDARD);
            this.player.sendMessage("");
            if (this.arenaEntity != null) {
                this.arenaEntity.showPlayer(this.player);
                this.arenaEntity = null;
            }
            if (this.type == 1)
                this.player.sendMessage(Language.PREFIX + "Enter the number of an arena to edit it:");
            else if (this.type == 2)
                this.player.sendMessage(Language.PREFIX + "Enter the number of an arena to view it:");
            else if (this.type == 3)
                this.player.sendMessage(Language.PREFIX + "Enter the number of an arena to copy its properties:");
            for (int i = 0; i < ArenaCommandExecutor.this.manager.getArenas().size(); i++) {
                final ArenaEntity entity = (ArenaEntity) ArenaCommandExecutor.this.manager.getArenas().get(i);
                if (entity.getAlias() == null)
                    this.player.sendMessage(Language.PREFIX + entity.getName() + " - " + ChatColor.GRAY + entity.getDownCornerLocation());
                else
                    this.player.sendMessage(Language.PREFIX + entity.getName() + " - " + ChatColor.GRAY + entity.getAlias() + ChatColor.GRAY + ' ' + entity.getDownCornerLocation());
            }
            this.player.sendMessage(Language.PREFIX + ChatColor.GREEN + MENU_BACK);
            this.player.sendMessage(Language.PREFIX + ChatColor.RED + MENU_EXIT);
            this.player.sendMessage("");
            this.player.sendMessage(FOOTER_STANDARD);
            this.player.sendMessage("");
        }
    }

    private class EditArenaPage extends SChatpage {
        private final ArenaEntity arenaEntity;

        EditArenaPage(Player player, ArenaEntity arenaEntity) {
            super(player);
            this.arenaEntity = arenaEntity;
        }

        @Override
        public void onPlayerSelect(int number) {
            if (number == 1) {
                if (WorldEditConnection.hasSelections(this.player)) {
                    this.arenaEntity.setCornerLocations(WorldEditConnection.getLeftSelection(this.player), WorldEditConnection.getRightSelection(this.player));
                } else {
                    this.player.sendMessage(Language.PREFIX + "Please select an arena via worldedit.");
                }
                this.show();
            } else if (number == 2) {
                if (WorldEditConnection.hasSelections(this.player)) {
                    this.arenaEntity.setGoal(Team.RED, WorldEditConnection.getLeftSelection(this.player), WorldEditConnection.getRightSelection(this.player));
                } else {
                    this.player.sendMessage(Language.PREFIX + "Please select a goal via worldedit.");
                }
                this.show();
            } else if (number == 3) {
                if (WorldEditConnection.hasSelections(this.player)) {
                    this.arenaEntity.setGoal(Team.BLUE, WorldEditConnection.getLeftSelection(this.player), WorldEditConnection.getRightSelection(this.player));
                } else {
                    this.player.sendMessage(Language.PREFIX + "Please select a goal via worldedit.");
                }
                this.show();
            } else if (number == 4) {
                this.arenaEntity.setBallSpawnLocation(this.player.getLocation());
                this.show();
            } else if (number == 5) {
                this.arenaEntity.getBallMeta().setBallSkin("http://textures.minecraft.net/texture/8e4a70b7bbcd7a8c322d522520491a27ea6b83d60ecf961d2b4efbbf9f605d");
                this.player.sendMessage(Language.PREFIX + ChatColor.GREEN + "Reset ball.");
            } else if (number == 6) {
                this.open(this.player, new MainSettingsPage(this.player, this.arenaEntity));
            } else if (number == 7) {
                ArenaCommandExecutor.this.manager.remove(this.arenaEntity);
                this.player.sendMessage(Language.PREFIX + ChatColor.GREEN + "Deleted arena.");
                this.open(this.player, new FirstPage(this.player));
            } else if (number == 8) {
                if (this.arenaEntity.isValid()) {
                    if (this.arenaEntity.getGameType() == GameType.MINIGAME) {
                        if (this.arenaEntity.getLobbyMeta().getLobbyLeave() == null || this.arenaEntity.getLobbyMeta().getLobbySpawn() == null) {
                            this.player.sendMessage(Language.PREFIX + ChatColor.RED + "Cannot save arena. Set the leaving and joining spawnpoint.");
                            return;
                        }
                    } else if (this.arenaEntity.getGameType() == GameType.BUNGEE) {
                        if (this.arenaEntity.getLobbyMeta().getLobbySpawn() == null) {
                            this.player.sendMessage(Language.PREFIX + ChatColor.RED + "Cannot save arena. Set the joining spawnpoint.");
                            return;
                        }
                    }
                    ArenaCommandExecutor.this.manager.store(this.arenaEntity);
                    this.player.sendMessage(Language.PREFIX + ChatColor.GREEN + "Successfully saved arena.");
                    ArenaCommandExecutor.this.manager.manager.reload();
                    if (this.arenaEntity.getGameType() == GameType.BUNGEE) {
                        for (final Player player : SFileUtils.getOnlinePlayers()) {
                            player.kickPlayer(Language.PREFIX + "Server is restarting for BlockBall bungee mode!");
                        }
                        SFileUtils.restartServer();
                    }
                    this.open(this.player, new FirstPage(this.player));
                } else {
                    this.player.sendMessage(Language.PREFIX + ChatColor.RED + "Cannot save arena. More options are required.");
                }
            }
        }

        @Override
        public void show() {
            this.player.sendMessage("");
            this.player.sendMessage(HEADER_STANDARD);
            this.player.sendMessage("");
            this.arenaEntity.showPlayer(this.player);
            this.player.sendMessage(Language.PREFIX + "1 - Set corners (Worldedit selection)");
            this.player.sendMessage(Language.PREFIX + "2 - Set goal blue corners (Worldedit selection)");
            this.player.sendMessage(Language.PREFIX + "3 - Set goal red corners (Worldedit selection)");
            this.player.sendMessage(Language.PREFIX + "4 - Set ballspawn location");
            this.player.sendMessage(Language.PREFIX + "5 - Reset ball");
            this.player.sendMessage(Language.PREFIX + "6 - Settings");
            this.player.sendMessage(Language.PREFIX + "7 - Delete arena");
            this.player.sendMessage(Language.PREFIX + "8 - Save arena");
            this.player.sendMessage(Language.PREFIX + ChatColor.GREEN + MENU_BACK);
            this.player.sendMessage(Language.PREFIX + ChatColor.RED + MENU_EXIT);
            this.player.sendMessage("");
            this.player.sendMessage(FOOTER_STANDARD);
            this.player.sendMessage("");
        }
    }

    //---------------------- SETTING PAGES --------------------------------------

    private class MainSettingsPage extends SChatpage {
        private final ArenaEntity arenaEntity;

        MainSettingsPage(Player player, ArenaEntity arenaEntity) {
            super(player);
            this.arenaEntity = arenaEntity;
        }

        @Override
        public void onPlayerSelect(int number) {
            if (number == 1)
                this.open(this.player, new GeneralSettingsPage(this.player, this.arenaEntity));
            else if (number == 2)
                this.open(this.player, new AllModesSettingsPage(this.player, this.arenaEntity));
            else if (number == 3)
                this.open(this.player, new LobbySettingsPage(this.player, this.arenaEntity));
            else if (number == 4)
                this.open(this.player, new MinigameSettingsPage(this.player, this.arenaEntity));
            else if (number == 5)
                this.open(this.player, new BungeeMinigameSettings(this.player, this.arenaEntity));
            else if (number == 6)
                this.open(this.player, new EventMetaSettingsPage(this.player, this.arenaEntity));
            else if (number == 7)
                this.open(this.player, new AnnouncementSettingsPage(this.player, this.arenaEntity));
            else if (number == 8)
                this.open(this.player, new MessagesSettingsPage(this.player, this.arenaEntity));
            else if (number == 9)
                this.open(this.player, new BallDesignSettingsPage(this.player, this.arenaEntity));
            else if (number == 10)
                this.open(this.player, new BallSoundParticlesSettingPage(this.player, this.arenaEntity));
            else if (number == 11)
                this.open(this.player, new AddtionalSettingPage(this.player, this.arenaEntity));
            else if (number == 12) {
                this.player.sendMessage(Language.PREFIX + "Saved settings.");
                this.open(this.player, new EditArenaPage(this.player, this.arenaEntity));
            }
        }

        @Override
        public void show() {
            this.player.sendMessage("");
            this.player.sendMessage(HEADER_STANDARD);
            this.player.sendMessage("");
            this.player.sendMessage(Language.PREFIX + "1 - General settings");
            this.player.sendMessage(Language.PREFIX + "2 - Allmodes settings");
            this.player.sendMessage(Language.PREFIX + "3 - Lobbygamemode settings");
            this.player.sendMessage(Language.PREFIX + "4 - Minigamemode settings");
            this.player.sendMessage(Language.PREFIX + "5 - BungeecordMinigame settings");
            this.player.sendMessage(Language.PREFIX + "6 - Eventmode settings");
            this.player.sendMessage(Language.PREFIX + "7 - Announcements");
            this.player.sendMessage(Language.PREFIX + "8 - Messages");
            this.player.sendMessage(Language.PREFIX + "9 - Ball design/appearance");
            this.player.sendMessage(Language.PREFIX + "10 - Ball sounds/particles");
            this.player.sendMessage(Language.PREFIX + "11 - Additional settings");
            this.player.sendMessage(Language.PREFIX + "12 - Save settings");
            this.player.sendMessage(Language.PREFIX + ChatColor.GREEN + MENU_BACK);
            this.player.sendMessage(Language.PREFIX + ChatColor.RED + MENU_EXIT);
            this.player.sendMessage("");
            this.player.sendMessage(FOOTER_STANDARD);
            this.player.sendMessage("");
        }
    }

    private class GeneralSettingsPage extends SChatpage {
        private final ArenaEntity arenaEntity;

        GeneralSettingsPage(Player player, ArenaEntity arenaEntity) {
            super(player);
            this.arenaEntity = arenaEntity;
        }

        @Override
        public boolean playerPreChatEnter(String text) {
            if (this.lastNumber == 5)
                this.arenaEntity.setAlias(text);
            else
                return true;
            return false;
        }

        @Override
        public void onPlayerSelect(int number) {
            if (number == 1) {
                this.arenaEntity.setGameType(GameType.MINIGAME);
                this.player.sendMessage(Language.PREFIX + "Enabled minigame mode.");
            } else if (number == 2) {
                this.arenaEntity.setGameType(GameType.LOBBY);
                this.player.sendMessage(Language.PREFIX + "Enabled lobbygame mode.");
            } else if (number == 3) {
                this.arenaEntity.setGameType(GameType.BUNGEE);
                this.player.sendMessage(Language.PREFIX + "Enabled bungeecordminigame mode.");
            } else if (number == 4) {
                this.arenaEntity.setGameType(GameType.EVENT);
                this.player.sendMessage(Language.PREFIX + "Enabled eventgame mode.");
            } else if (number == 5) {
                this.player.sendMessage(Language.PREFIX + "Enter an alias of your arena:");
            } else if (number == 6) {
                this.arenaEntity.setIsEnabled(!this.arenaEntity.isEnabled());
                if (this.arenaEntity.isEnabled()) {
                    this.player.sendMessage(Language.PREFIX + "Enabled arena.");
                } else {
                    this.player.sendMessage(Language.PREFIX + "Disabled arena.");
                }
            } else if (number == 7) {
                this.open(this.player, new WallBouncingPage(this.player, this.arenaEntity));
            } else if (number == 8) {
                this.open(this.player, new SpectatorsModePage(this.player, this.arenaEntity));

            } else if (number == 9) {
                this.open(this.player, new EditCommands(this.player, this.arenaEntity));

            } else if (number == 10) {
                this.player.sendMessage(Language.PREFIX + "Saved settings.");
                this.open(this.player, new MainSettingsPage(this.player, this.arenaEntity));
            }
        }

        @Override
        public void show() {
            this.player.sendMessage("");
            this.player.sendMessage(HEADER_STANDARD);
            this.player.sendMessage("");
            this.player.sendMessage(Language.PREFIX + "1 - Set minigame mode");
            this.player.sendMessage(Language.PREFIX + "2 - Set lobbygame mode");
            this.player.sendMessage(Language.PREFIX + "3 - Set bungeecordminigame mode");
            this.player.sendMessage(Language.PREFIX + "4 - Set eventgame mode");
            this.player.sendMessage(Language.PREFIX + "5 - Set alias");
            this.player.sendMessage(Language.PREFIX + "6 - Enable/Disable arena");
            this.player.sendMessage(Language.PREFIX + "7 - Wall bouncing");
            this.player.sendMessage(Language.PREFIX + "8 - Spectators");
            this.player.sendMessage(Language.PREFIX + "9 - Commands");
            this.player.sendMessage(Language.PREFIX + "10 - Save settings");
            this.player.sendMessage(Language.PREFIX + ChatColor.GREEN + MENU_BACK);
            this.player.sendMessage(Language.PREFIX + ChatColor.RED + MENU_EXIT);
            this.player.sendMessage("");
            this.player.sendMessage(FOOTER_STANDARD);
            this.player.sendMessage("");
        }
    }

    private class AllModesSettingsPage extends SChatpage {
        private final ArenaEntity arenaEntity;

        AllModesSettingsPage(Player player, ArenaEntity arenaEntity) {
            super(player);
            this.arenaEntity = arenaEntity;
        }

        @Override
        public boolean playerPreChatEnter(String text) {
            if (this.lastNumber == 1 && tryPInt(text) && Integer.parseInt(text) > 0)
                this.arenaEntity.getTeamMeta().setMaxScore(Integer.parseInt(text));
            else if (this.lastNumber == 2 && tryPInt(text) && Integer.parseInt(text) > 0)
                this.arenaEntity.getTeamMeta().setTeamMaxSize(Integer.parseInt(text));
            else if (this.lastNumber == 3 && tryPInt(text) && Integer.parseInt(text) >= 0)
                this.arenaEntity.getTeamMeta().setTeamMinSize(Integer.parseInt(text));
            else
                return true;
            return false;
        }

        @Override
        public void onPlayerSelect(int number) {
            if (number == 1)
                this.player.sendMessage(Language.PREFIX + "Enter the max score of a game:");
            else if (number == 2)
                this.player.sendMessage(Language.PREFIX + "Enter the max amount of players per team:");
            else if (number == 3)
                this.player.sendMessage(Language.PREFIX + "Enter the min amount of players:");
            else if (number == 4) {
                this.arenaEntity.getTeamMeta().setRedSpawnPoint(this.player.getLocation());
                this.player.sendMessage(Language.PREFIX + "Set the red spawn at your location.");
            } else if (number == 5) {
                this.arenaEntity.getTeamMeta().setBlueSpawnPoint(this.player.getLocation());
                this.player.sendMessage(Language.PREFIX + "Set the blue spawn at your location.");
            }
            if (number == 6) {
                this.arenaEntity.getTeamMeta().setRedItems(this.player.getInventory().getArmorContents().clone());
                this.player.sendMessage(Language.PREFIX + "Copied your current armor to the red team.");
            } else if (number == 7) {
                this.arenaEntity.getTeamMeta().setBlueItems(this.player.getInventory().getArmorContents().clone());
                this.player.sendMessage(Language.PREFIX + "Copied your current armor to the blue team.");
            } else if (number == 8) {
                this.arenaEntity.getTeamMeta().setTeamMaxSize(5);
                this.arenaEntity.getTeamMeta().setTeamMinSize(0);
                this.arenaEntity.getTeamMeta().setRedSpawnPoint(null);
                this.arenaEntity.getTeamMeta().setBlueSpawnPoint(null);
                this.arenaEntity.getTeamMeta().resetArmor();
                this.player.sendMessage(Language.PREFIX + "Reset this page.");
            } else if (number == 9) {
                this.player.sendMessage(Language.PREFIX + "Saved team settings.");
                this.open(this.player, new MainSettingsPage(this.player, this.arenaEntity));
            }
        }

        @Override
        public void show() {
            this.player.sendMessage("");
            this.player.sendMessage(HEADER_STANDARD);
            this.player.sendMessage("");
            this.player.sendMessage(Language.PREFIX + "1 - Set max score");
            this.player.sendMessage(Language.PREFIX + "2 - Set max players per team");
            this.player.sendMessage(Language.PREFIX + "3 - Set min players for match");
            this.player.sendMessage(Language.PREFIX + "4 - Set red spawnpoint");
            this.player.sendMessage(Language.PREFIX + "5 - Set blue spawnpoint");
            this.player.sendMessage(Language.PREFIX + "6 - Set red equipment");
            this.player.sendMessage(Language.PREFIX + "7 - Set blue equipment");
            this.player.sendMessage(Language.PREFIX + "8 - Reset this page");
            this.player.sendMessage(Language.PREFIX + "9 - Save settings");
            this.player.sendMessage(Language.PREFIX + ChatColor.GREEN + MENU_BACK);
            this.player.sendMessage(Language.PREFIX + ChatColor.RED + MENU_EXIT);
            this.player.sendMessage("");
            this.player.sendMessage(FOOTER_STANDARD);
            this.player.sendMessage("");
        }
    }

    private class LobbySettingsPage extends SChatpage {
        private final ArenaEntity arenaEntity;

        LobbySettingsPage(Player player, ArenaEntity arenaEntity) {
            super(player);
            this.arenaEntity = arenaEntity;
        }

        @Override
        public void hitBlockEvent(Block block) {
            if (this.lastNumber == 5 && (block.getType() == Material.SIGN_POST || block.getType() == Material.WALL_SIGN)) {
                final Sign sign = (Sign) block.getState();
                sign.setLine(0, Config.getInstance().getTeamSign().getLine1(this.arenaEntity, Team.RED));
                sign.setLine(1, Config.getInstance().getTeamSign().getLine2(this.arenaEntity, Team.RED));
                sign.setLine(2, Config.getInstance().getTeamSign().getLine3(this.arenaEntity, Team.RED));
                sign.setLine(3, Config.getInstance().getTeamSign().getLine4(this.arenaEntity, Team.RED));
                sign.update();
                this.lastNumber = -1;
                this.arenaEntity.getLobbyMeta().addRedTeamSignLocation(block.getLocation());
                this.player.sendMessage(Language.PREFIX + "Set sign.");
            }
            if (this.lastNumber == 6 && (block.getType() == Material.SIGN_POST || block.getType() == Material.WALL_SIGN)) {
                final Sign sign = (Sign) block.getState();
                sign.setLine(0, Config.getInstance().getTeamSign().getLine1(this.arenaEntity, Team.BLUE));
                sign.setLine(1, Config.getInstance().getTeamSign().getLine2(this.arenaEntity, Team.BLUE));
                sign.setLine(2, Config.getInstance().getTeamSign().getLine3(this.arenaEntity, Team.BLUE));
                sign.setLine(3, Config.getInstance().getTeamSign().getLine4(this.arenaEntity, Team.BLUE));
                sign.update();
                this.lastNumber = -1;
                this.arenaEntity.getLobbyMeta().addBlueTeamSignLocation(block.getLocation());
                this.player.sendMessage(Language.PREFIX + "Set sign.");
            }
        }

        @Override
        public void onPlayerSelect(int number) {
            if (number == 1) {
                this.arenaEntity.getTeamMeta().setGameEndSpawnpoint(this.player.getLocation());
                this.player.sendMessage(Language.PREFIX + "Set the win spawnpoint at your location.");
            } else if (number == 2) {
                this.arenaEntity.getTeamMeta().setTeamAutoJoin(!this.arenaEntity.getTeamMeta().isTeamAutoJoin());
                if (this.arenaEntity.getTeamMeta().isTeamAutoJoin())
                    this.player.sendMessage(Language.PREFIX + "Enabled automatically choosing team.");
                else
                    this.player.sendMessage(Language.PREFIX + "Disabled automatically choosing team.");
            } else if (number == 3) {
                this.arenaEntity.getTeamMeta().setFastJoin(!this.arenaEntity.getTeamMeta().isFastJoin());
                if (this.arenaEntity.getTeamMeta().isFastJoin())
                    this.player.sendMessage(Language.PREFIX + "Enabled fast joining.");
                else
                    this.player.sendMessage(Language.PREFIX + "Disabled fast joining.");
            } else if (number == 4) {
                this.arenaEntity.getTeamMeta().setEmptyReset(!this.arenaEntity.getTeamMeta().isEmtptyReset());
                if (this.arenaEntity.getTeamMeta().isEmtptyReset())
                    this.player.sendMessage(Language.PREFIX + "Enabled game-empty-reset.");
                else
                    this.player.sendMessage(Language.PREFIX + "Disabled game-empty-reset.");
            } else if (number == 5) {
                this.player.sendMessage(Language.PREFIX + "Rightclick a sign to convert it:");
            } else if (number == 6) {
                this.player.sendMessage(Language.PREFIX + "Rightclick a sign to convert it:");
            } else if (number == 7) {
                this.arenaEntity.getTeamMeta().setMaxScore(100);
                this.arenaEntity.getTeamMeta().setGameEndSpawnpoint(null);
                this.arenaEntity.getTeamMeta().setTeamAutoJoin(false);
                this.arenaEntity.getTeamMeta().setFastJoin(false);
                this.arenaEntity.getTeamMeta().setEmptyReset(false);
                this.player.sendMessage(Language.PREFIX + "Reset this page.");
            } else if (number == 8) {
                this.player.sendMessage(Language.PREFIX + "Saved settings.");
                this.open(this.player, new MainSettingsPage(this.player, this.arenaEntity));
            }
        }

        @Override
        public void show() {
            this.player.sendMessage("");
            this.player.sendMessage(HEADER_STANDARD);
            this.player.sendMessage("");
            this.player.sendMessage(Language.PREFIX + "1 - Set spawnpoint after win");
            this.player.sendMessage(Language.PREFIX + "2 - Enable/Disable auto team joining");
            this.player.sendMessage(Language.PREFIX + "3 - Enable/Disable fast joining");
            this.player.sendMessage(Language.PREFIX + "4 - Enable/Disable auto empty reset");
            this.player.sendMessage(Language.PREFIX + "5 - Set redteam sign");
            this.player.sendMessage(Language.PREFIX + "6 - Set blueteam sign");
            this.player.sendMessage(Language.PREFIX + "7 - Reset this page");
            this.player.sendMessage(Language.PREFIX + "8 - Save settings");
            this.player.sendMessage(Language.PREFIX + ChatColor.GREEN + MENU_BACK);
            this.player.sendMessage(Language.PREFIX + ChatColor.RED + MENU_EXIT);
            this.player.sendMessage("");
            this.player.sendMessage(FOOTER_STANDARD);
            this.player.sendMessage("");
        }
    }

    private class MinigameSettingsPage extends SChatpage {
        private final ArenaEntity arenaEntity;

        MinigameSettingsPage(Player player, ArenaEntity arenaEntity) {
            super(player);
            this.arenaEntity = arenaEntity;
        }

        @Override
        public boolean playerPreChatEnter(String text) {
            if (this.lastNumber == 1 && tryPInt(text) && Integer.parseInt(text) > 0)
                this.arenaEntity.getLobbyMeta().setGameTime(Integer.parseInt(text));
            else if (this.lastNumber == 2 && tryPInt(text) && Integer.parseInt(text) >= 0)
                this.arenaEntity.getLobbyMeta().setCountDown(Integer.parseInt(text));
            else if (this.lastNumber == 8)
                this.arenaEntity.getLobbyMeta().setGameTitleMessage(text);
            else if (this.lastNumber == 9)
                this.arenaEntity.getLobbyMeta().setGameSubTitleMessage(text);
            else
                return true;
            return false;
        }

        @Override
        public void hitBlockEvent(Block block) {
            if (this.lastNumber == 5 && (block.getType() == Material.SIGN_POST || block.getType() == Material.WALL_SIGN)) {
                final Sign sign = (Sign) block.getState();
                sign.setLine(0, Config.getInstance().getMinigameSign().getLine1(this.arenaEntity, null));
                sign.setLine(1, Config.getInstance().getMinigameSign().getLine2(this.arenaEntity, null));
                sign.setLine(2, Config.getInstance().getMinigameSign().getLine3(this.arenaEntity, null));
                sign.setLine(3, Config.getInstance().getMinigameSign().getLine4(this.arenaEntity, null));
                sign.update();
                this.arenaEntity.getLobbyMeta().addSignLocation(block.getLocation());
                this.lastNumber = -1;
                this.player.sendMessage(Language.PREFIX + "Set sign.");
            }
            if (this.lastNumber == 6 && (block.getType() == Material.SIGN_POST || block.getType() == Material.WALL_SIGN)) {
                final Sign sign = (Sign) block.getState();
                sign.setLine(0, Config.getInstance().getTeamSign().getLine1(this.arenaEntity, Team.RED));
                sign.setLine(1, Config.getInstance().getTeamSign().getLine2(this.arenaEntity, Team.RED));
                sign.setLine(2, Config.getInstance().getTeamSign().getLine3(this.arenaEntity, Team.RED));
                sign.setLine(3, Config.getInstance().getTeamSign().getLine4(this.arenaEntity, Team.RED));
                sign.update();
                this.lastNumber = -1;
                this.arenaEntity.getLobbyMeta().addRedTeamSignLocation(block.getLocation());
                this.player.sendMessage(Language.PREFIX + "Set sign.");
            }
            if (this.lastNumber == 7 && (block.getType() == Material.SIGN_POST || block.getType() == Material.WALL_SIGN)) {
                final Sign sign = (Sign) block.getState();
                sign.setLine(0, Config.getInstance().getTeamSign().getLine1(this.arenaEntity, Team.BLUE));
                sign.setLine(1, Config.getInstance().getTeamSign().getLine2(this.arenaEntity, Team.BLUE));
                sign.setLine(2, Config.getInstance().getTeamSign().getLine3(this.arenaEntity, Team.BLUE));
                sign.setLine(3, Config.getInstance().getTeamSign().getLine4(this.arenaEntity, Team.BLUE));
                sign.update();
                this.lastNumber = -1;
                this.arenaEntity.getLobbyMeta().addBlueTeamSignLocation(block.getLocation());
                this.player.sendMessage(Language.PREFIX + "Set sign.");
            }
            if (this.lastNumber == 8 && (block.getType() == Material.SIGN_POST || block.getType() == Material.WALL_SIGN)) {
                final Sign sign = (Sign) block.getState();
                sign.setLine(0, Config.getInstance().getLeaveSign().getLine1(this.arenaEntity, null));
                sign.setLine(1, Config.getInstance().getLeaveSign().getLine2(this.arenaEntity, null));
                sign.setLine(2, Config.getInstance().getLeaveSign().getLine3(this.arenaEntity, null));
                sign.setLine(3, Config.getInstance().getLeaveSign().getLine4(this.arenaEntity, null));
                sign.update();
                this.lastNumber = -1;
                this.arenaEntity.getLobbyMeta().addLeaveignLocation(block.getLocation());
                this.player.sendMessage(Language.PREFIX + "Set sign.");
            }
        }

        @Override
        public void onPlayerSelect(int number) {
            if (number == 1)
                this.player.sendMessage(Language.PREFIX + "Enter the duration of the match:");
            else if (number == 2)
                this.player.sendMessage(Language.PREFIX + "Enter the duration of the lobby:");
            else if (number == 3) {
                this.arenaEntity.getLobbyMeta().setLobbySpawnpoint(this.player.getLocation());
                this.player.sendMessage(Language.PREFIX + "Set the lobby spawnpoint at your location.");
            } else if (number == 4) {
                this.arenaEntity.getLobbyMeta().setLobbyLeave(this.player.getLocation());
                this.player.sendMessage(Language.PREFIX + "Set the leave spawnpoint at your location.");
            } else if (number == 5) {
                this.player.sendMessage(Language.PREFIX + "Rightclick a sign to convert it:");
            } else if (number == 6) {
                this.player.sendMessage(Language.PREFIX + "Rightclick a sign to convert it:");
            } else if (number == 7) {
                this.player.sendMessage(Language.PREFIX + "Rightclick a sign to convert it:");
            } else if (number == 8) {
                this.player.sendMessage(Language.PREFIX + "Rightclick a sign to convert it:");
            } else if (number == 9) {
                this.player.sendMessage(Language.PREFIX + "Enter the title when the lobby countdown changes:");
                this.player.sendMessage(Language.PREFIX + "Example: 'Game'");
            } else if (number == 10) {
                this.player.sendMessage(Language.PREFIX + "Enter the subtitle when the lobby countdown changes:");
                this.player.sendMessage(Language.PREFIX + "Example: 'Starting in :countdown seconds.'");
            } else if (number == 11) {
                this.arenaEntity.getTeamMeta().setMaxScore(100);
                this.arenaEntity.getTeamMeta().setGameEndSpawnpoint(null);
                this.arenaEntity.getTeamMeta().setTeamAutoJoin(false);
                this.arenaEntity.getTeamMeta().setFastJoin(false);
                this.arenaEntity.getTeamMeta().setEmptyReset(false);
                this.arenaEntity.getLobbyMeta().setGameSubTitleMessage(ChatColor.YELLOW + "Starting in :countdown seconds.");
                this.arenaEntity.getLobbyMeta().setGameTitleMessage(ChatColor.GOLD + "Game");
                this.player.sendMessage(Language.PREFIX + "Reset this page.");
            } else if (number == 12) {
                this.player.sendMessage(Language.PREFIX + "Saved settings.");
                this.open(this.player, new MainSettingsPage(this.player, this.arenaEntity));
            }
        }

        @Override
        public void show() {
            this.player.sendMessage("");
            this.player.sendMessage(HEADER_STANDARD);
            this.player.sendMessage("");
            this.player.sendMessage(Language.PREFIX + "1 - Set match time");
            this.player.sendMessage(Language.PREFIX + "2 - Set lobby time");
            this.player.sendMessage(Language.PREFIX + "3 - Set lobby spawnpoint");
            this.player.sendMessage(Language.PREFIX + "4 - Set leave spawnpoint");
            this.player.sendMessage(Language.PREFIX + "5 - Add join sign");
            this.player.sendMessage(Language.PREFIX + "6 - Set redteam sign");
            this.player.sendMessage(Language.PREFIX + "7 - Set blueteam sign");
            this.player.sendMessage(Language.PREFIX + "8 - Set leaving sign");
            this.player.sendMessage(Language.PREFIX + "9 - Set game countdowntitle");
            this.player.sendMessage(Language.PREFIX + "10 - Set game countdownsubtitle");
            this.player.sendMessage(Language.PREFIX + "11 - Reset this page");
            this.player.sendMessage(Language.PREFIX + "12 - Save settings");
            this.player.sendMessage(Language.PREFIX + ChatColor.GREEN + MENU_BACK);
            this.player.sendMessage(Language.PREFIX + ChatColor.RED + MENU_EXIT);
            this.player.sendMessage("");
            this.player.sendMessage(FOOTER_STANDARD);
            this.player.sendMessage("");
        }
    }

    private class AnnouncementSettingsPage extends SChatpage {
        private final ArenaEntity arenaEntity;

        AnnouncementSettingsPage(Player player, ArenaEntity arenaEntity) {
            super(player);
            this.arenaEntity = arenaEntity;
        }

        @Override
        public boolean playerPreChatEnter(String text) {
            if (this.lastNumber == 1)
                this.arenaEntity.getTeamMeta().setRedtitleScoreMessage(text);
            else if (this.lastNumber == 2)
                this.arenaEntity.getTeamMeta().setRedsubtitleMessage(text);
            else if (this.lastNumber == 3)
                this.arenaEntity.getTeamMeta().setRedwinnerTitleMessage(text);
            else if (this.lastNumber == 4)
                this.arenaEntity.getTeamMeta().setRedwinnerSubtitleMessage(text);
            else if (this.lastNumber == 5)
                this.arenaEntity.getTeamMeta().setBluetitleScoreMessage(text);
            else if (this.lastNumber == 6)
                this.arenaEntity.getTeamMeta().setBluesubtitleMessage(text);
            else if (this.lastNumber == 7)
                this.arenaEntity.getTeamMeta().setBluewinnerTitleMessage(text);
            else if (this.lastNumber == 8)
                this.arenaEntity.getTeamMeta().setBluewinnerSubtitleMessage(text);
            else
                return true;
            return false;
        }

        @Override
        public void onPlayerSelect(int number) {
            if (number == 1) {
                this.player.sendMessage(Language.PREFIX + "Enter the title when the redteam scores:");
                this.player.sendMessage(Language.PREFIX + "Example: ':redcolor:redscore : :bluecolor:bluescore'");
            } else if (number == 2) {
                this.player.sendMessage(Language.PREFIX + "Enter the subtitle when the redteam scores:");
                this.player.sendMessage(Language.PREFIX + "Example: ':redcolor:player scored for :red'");
            } else if (number == 3) {
                this.player.sendMessage(Language.PREFIX + "Enter the title when the redteam wins:");
                this.player.sendMessage(Language.PREFIX + "Example: ':redcolor:red'");
            } else if (number == 4) {
                this.player.sendMessage(Language.PREFIX + "Enter the subtitle when the redteam wins:");
                this.player.sendMessage(Language.PREFIX + "Example: '&a&lWinner'");
            } else if (number == 5) {
                this.player.sendMessage(Language.PREFIX + "Enter the title when the blueteam scores:");
                this.player.sendMessage(Language.PREFIX + "Example: ':bluecolor:bluescore : :recolor:redscore'");
            } else if (number == 6) {
                this.player.sendMessage(Language.PREFIX + "Enter the subtitle when the blueteam scores:");
                this.player.sendMessage(Language.PREFIX + "Example: ':bluecolor:player scored for :blue'");
            } else if (number == 7) {
                this.player.sendMessage(Language.PREFIX + "Enter the title when the blueteam wins:");
                this.player.sendMessage(Language.PREFIX + "Example: ':bluecolor:blue'");
            } else if (number == 8) {
                this.player.sendMessage(Language.PREFIX + "Enter the subtitle when the blueteam wins:");
                this.player.sendMessage(Language.PREFIX + "Example: '&a&lWinner'");
            } else if (number == 9) {
                this.open(this.player, new BossBarMiddlePage(this.player, this.arenaEntity));
            } else if (number == 10) {
                this.open(this.player, new ScoreboardPage(this.player, this.arenaEntity));

            } else if (number == 11) {
                this.arenaEntity.getTeamMeta().setRedtitleScoreMessage(":redcolor:redscore : :bluecolor:bluescore");
                this.arenaEntity.getTeamMeta().setRedsubtitleMessage(":redcolor:player scored for :red");
                this.arenaEntity.getTeamMeta().setRedwinnerTitleMessage(":redcolor:red");
                this.arenaEntity.getTeamMeta().setRedwinnerSubtitleMessage("&a&lWinner");
                this.arenaEntity.getTeamMeta().setBluetitleScoreMessage(":bluecolor:bluescore : :redcolor:redscore");
                this.arenaEntity.getTeamMeta().setBluesubtitleMessage(":bluecolor:player scored for :blue");
                this.arenaEntity.getTeamMeta().setBluewinnerTitleMessage(":bluecolor:blue");
                this.arenaEntity.getTeamMeta().setBluewinnerSubtitleMessage("&a&lWinner");
                this.player.sendMessage(Language.PREFIX + "Reset this page.");
            } else if (number == 12) {
                this.player.sendMessage(Language.PREFIX + "Saved team settings.");
                this.open(this.player, new MainSettingsPage(this.player, this.arenaEntity));
            }
        }

        @Override
        public void show() {
            this.player.sendMessage("");
            this.player.sendMessage(HEADER_STANDARD);
            this.player.sendMessage("");
            this.player.sendMessage(Language.PREFIX + "1 - Set redscore title");
            this.player.sendMessage(Language.PREFIX + "2 - Set redscore subtitle");
            this.player.sendMessage(Language.PREFIX + "3 - Set redwin title");
            this.player.sendMessage(Language.PREFIX + "4 - Set redwin subtitle");
            this.player.sendMessage(Language.PREFIX + "5 - Set bluescore title");
            this.player.sendMessage(Language.PREFIX + "6 - Set bluescore subtitle");
            this.player.sendMessage(Language.PREFIX + "7 - Set bluewin title");
            this.player.sendMessage(Language.PREFIX + "8 - Set bluewin subtitle");
            this.player.sendMessage(Language.PREFIX + "9 - Set bossbar");
            this.player.sendMessage(Language.PREFIX + "10 - Set scoreboard");
            this.player.sendMessage(Language.PREFIX + "11 - Reset this page");
            this.player.sendMessage(Language.PREFIX + "12 - Save team settings");
            this.player.sendMessage(Language.PREFIX + ChatColor.GREEN + MENU_BACK);
            this.player.sendMessage(Language.PREFIX + ChatColor.RED + MENU_EXIT);
            this.player.sendMessage("");
            this.player.sendMessage(FOOTER_STANDARD);
            this.player.sendMessage("");
        }
    }

    private class MessagesSettingsPage extends SChatpage {
        private final ArenaEntity arenaEntity;

        MessagesSettingsPage(Player player, ArenaEntity arenaEntity) {
            super(player);
            this.arenaEntity = arenaEntity;
        }

        @Override
        public boolean playerPreChatEnter(String text) {
            if (this.lastNumber == 1)
                this.arenaEntity.getTeamMeta().setRedTeamName(text);
            else if (this.lastNumber == 2)
                this.arenaEntity.getTeamMeta().setBlueTeamName(text);
            else if (this.lastNumber == 3)
                this.arenaEntity.getTeamMeta().setRedColor(text);
            else if (this.lastNumber == 4)
                this.arenaEntity.getTeamMeta().setBlueColor(text);
            else if (this.lastNumber == 5)
                this.arenaEntity.getTeamMeta().setJoinMessage(text);
            else if (this.lastNumber == 6)
                this.arenaEntity.getTeamMeta().setLeaveMessage(text);
            else if (this.lastNumber == 7)
                this.arenaEntity.getTeamMeta().setHowToJoinMessage(text);
            else if (this.lastNumber == 8)
                this.arenaEntity.getTeamMeta().setTeamFullMessage(text);
            else
                return true;
            return false;
        }

        @Override
        public void onPlayerSelect(int number) {
            if (number == 1)
                this.player.sendMessage(Language.PREFIX + "Enter the name of the red team:");
            else if (number == 2)
                this.player.sendMessage(Language.PREFIX + "Enter the name of the blue team:");
            else if (number == 3)
                this.player.sendMessage(Language.PREFIX + "Enter the prefix (color) of the red team:");
            else if (number == 4)
                this.player.sendMessage(Language.PREFIX + "Enter the prefix (color) of the blue team:");
            else if (number == 5)
                this.player.sendMessage(Language.PREFIX + "Enter the join message:");
            else if (number == 6)
                this.player.sendMessage(Language.PREFIX + "Enter the leave message:");
            else if (number == 7) {
                this.player.sendMessage(Language.PREFIX + "Enter the 'how to join' message:");
                this.player.sendMessage(Language.PREFIX + "Example: 'Type ':red' to join the :red team or type ':blue' to join the :blue team. 'Cancel' to exit.'");
                this.player.sendMessage(Language.PREFIX + ":red and :blue gets automatically replaced by the team name!");
            } else if (number == 8)
                this.player.sendMessage(Language.PREFIX + "Enter the teamfull message:");
            else if (number == 9) {
                this.arenaEntity.getTeamMeta().setRedTeamName(ChatColor.RED + "Team Red");
                this.arenaEntity.getTeamMeta().setBlueTeamName(ChatColor.BLUE + "Team Blue");
                this.arenaEntity.getTeamMeta().setRedColor("" + ChatColor.RED);
                this.arenaEntity.getTeamMeta().setBlueColor("" + ChatColor.BLUE);
                this.arenaEntity.getTeamMeta().setJoinMessage("You joined the game.");
                this.arenaEntity.getTeamMeta().setLeaveMessage("You left the game.");
                this.arenaEntity.getTeamMeta().setHowToJoinMessage("Type ':red' to join the :red team or type ':blue' to join the :blue team. 'Cancel' to exit.");
                this.player.sendMessage(Language.PREFIX + "Reset this page.");
            } else if (number == 10) {
                this.player.sendMessage(Language.PREFIX + "Saved team settings.");
                this.open(this.player, new MainSettingsPage(this.player, this.arenaEntity));
            }
        }

        @Override
        public void show() {
            this.player.sendMessage("");
            this.player.sendMessage(HEADER_STANDARD);
            this.player.sendMessage("");
            this.player.sendMessage(Language.PREFIX + "1 - Set red name");
            this.player.sendMessage(Language.PREFIX + "2 - Set blue name");
            this.player.sendMessage(Language.PREFIX + "3 - Set red prefix");
            this.player.sendMessage(Language.PREFIX + "4 - Set blue prefix");
            this.player.sendMessage(Language.PREFIX + "5 - Set join message");
            this.player.sendMessage(Language.PREFIX + "6 - Set leave message");
            this.player.sendMessage(Language.PREFIX + "7 - Set howtojoin message");
            this.player.sendMessage(Language.PREFIX + "8 - Set teamfull message");
            this.player.sendMessage(Language.PREFIX + "9 - Reset this page");
            this.player.sendMessage(Language.PREFIX + "10 - Save team settings");
            this.player.sendMessage(Language.PREFIX + ChatColor.GREEN + MENU_BACK);
            this.player.sendMessage(Language.PREFIX + ChatColor.RED + MENU_EXIT);
            this.player.sendMessage("");
            this.player.sendMessage(FOOTER_STANDARD);
            this.player.sendMessage("");
        }
    }

    //-------------------------------------------------------------------------------------------

    private class BallDesignSettingsPage extends SChatpage {
        private final ArenaEntity arenaEntity;

        BallDesignSettingsPage(Player player, ArenaEntity arenaEntity) {
            super(player);
            this.arenaEntity = arenaEntity;
        }

        @Override
        public boolean playerPreChatEnter(String text) {
            if (this.lastNumber == 1)
                this.arenaEntity.getBallMeta().setBallSkin(text);
            else if (this.lastNumber == 2 && tryPDouble(text) && Double.parseDouble(text) >= 0 && Double.parseDouble(text) < 10)
                this.arenaEntity.getBallMeta().setHorizontalStrength(Double.parseDouble(text));
            else if (this.lastNumber == 3 && tryPDouble(text) && Double.parseDouble(text) >= 0 && Double.parseDouble(text) < 10)
                this.arenaEntity.getBallMeta().setVerticalStrength(Double.parseDouble(text));
            else if (this.lastNumber == 4 && tryPInt(text) && Integer.parseInt(text) >= 0)
                this.arenaEntity.getBallMeta().setBallSpawnTime(Integer.parseInt(text));
            else
                return true;

            return false;
        }

        @Override
        public void onPlayerSelect(int number) {
            if (number == 1)
                this.player.sendMessage(Language.PREFIX + "Enter the url or owner of the skin:");
            else if (number == 2)
                this.player.sendMessage(Language.PREFIX + "Enter the horizontal strength of the ball (0.0-10.0):");
            else if (number == 3)
                this.player.sendMessage(Language.PREFIX + "Enter the vertical strength of the ball (0.0-10.0):");
            else if (number == 4)
                this.player.sendMessage(Language.PREFIX + "Enter the delay until a ball spawns:");
            else if (number == 5) {
                this.arenaEntity.getBallMeta().setRotating(!this.arenaEntity.getBallMeta().isRotating());
                if (this.arenaEntity.getBallMeta().isRotating())
                    this.player.sendMessage(Language.PREFIX + "Enabled ball rotation.");
                else
                    this.player.sendMessage(Language.PREFIX + "Disabled ball rotation.");
            } else if (number == 6) {
                this.arenaEntity.getBallMeta().setBallSkin("http://textures.minecraft.net/texture/8e4a70b7bbcd7a8c322d522520491a27ea6b83d60ecf961d2b4efbbf9f605d");
                this.player.sendMessage(Language.PREFIX + "Reset ball skin.");
            } else if (number == 7) {
                this.arenaEntity.getBallMeta().setBallSkin("http://textures.minecraft.net/texture/8e4a70b7bbcd7a8c322d522520491a27ea6b83d60ecf961d2b4efbbf9f605d");
                this.arenaEntity.getBallMeta().setHorizontalStrength(1.8);
                this.arenaEntity.getBallMeta().setVerticalStrength(0.8);
                this.arenaEntity.getBallMeta().setBallSpawnTime(3);
                this.arenaEntity.getBallMeta().setRotating(true);
                this.player.sendMessage(Language.PREFIX + "Reset this page.");
            } else if (number == 8) {
                this.player.sendMessage(Language.PREFIX + "Saved settings.");
                this.open(this.player, new MainSettingsPage(this.player, this.arenaEntity));
            }
        }

        @Override
        public void show() {
            this.player.sendMessage("");
            this.player.sendMessage(HEADER_STANDARD);
            this.player.sendMessage("");
            this.player.sendMessage(Language.PREFIX + "1 - Set ball skin");
            this.player.sendMessage(Language.PREFIX + "2 - Set ball horizontal strength");
            this.player.sendMessage(Language.PREFIX + "3 - Set ball vertical strength");
            this.player.sendMessage(Language.PREFIX + "4 - Set ball-spawn delay");
            this.player.sendMessage(Language.PREFIX + "5 - Toggle ball-rotation");
            this.player.sendMessage(Language.PREFIX + "6 - Reset ball skin");
            this.player.sendMessage(Language.PREFIX + "7 - Reset this page");
            this.player.sendMessage(Language.PREFIX + "8 - Save settings");
            this.player.sendMessage(Language.PREFIX + ChatColor.GREEN + MENU_BACK);
            this.player.sendMessage(Language.PREFIX + ChatColor.RED + MENU_EXIT);
            this.player.sendMessage("");
            this.player.sendMessage(FOOTER_STANDARD);
            this.player.sendMessage("");
        }
    }

    private class BallSoundParticlesSettingPage extends SChatpage {
        private final ArenaEntity arenaEntity;

        BallSoundParticlesSettingPage(Player player, ArenaEntity arenaEntity) {
            super(player);
            this.arenaEntity = arenaEntity;
        }

        @Override
        public void onPlayerSelect(int number) {
            if (number == 1)
                this.open(this.player, new EditSoundPage(this.player, this.arenaEntity.getBallMeta().getGenericHitSound(), this.arenaEntity));
            else if (number == 2)
                this.open(this.player, new EditParticlePage(this.player, this.arenaEntity.getBallMeta().getGenericHitParticle(), this.arenaEntity));
            else if (number == 3)
                this.open(this.player, new EditParticlePage(this.player, this.arenaEntity.getBallMeta().getPlayerTeamRedHitParticle(), this.arenaEntity));
            else if (number == 4)
                this.open(this.player, new EditParticlePage(this.player, this.arenaEntity.getBallMeta().getPlayerTeamBlueHitParticle(), this.arenaEntity));
            else if (number == 5)
                this.open(this.player, new EditParticlePage(this.player, this.arenaEntity.getBallMeta().getBallSpawnParticle(), this.arenaEntity));
            else if (number == 6)
                this.open(this.player, new EditSoundPage(this.player, this.arenaEntity.getBallMeta().getBallSpawnSound(), this.arenaEntity));
            else if (number == 7)
                this.open(this.player, new EditParticlePage(this.player, this.arenaEntity.getBallMeta().getBallGoalParticle(), this.arenaEntity));
            else if (number == 8)
                this.open(this.player, new EditSoundPage(this.player, this.arenaEntity.getBallMeta().getBallGoalSound(), this.arenaEntity));
            else if (number == 9) {
                this.arenaEntity.getBallMeta().setGenericHitSound(new SoundBuilder("ZOMBIE_WOOD", 1.0, 1.0));
                this.arenaEntity.getBallMeta().setGenericHitParticle(new SParticle(ParticleEffect.EXPLOSION_HUGE, 1, 0.0002, 0.01, 0.01, 0.01));
                this.arenaEntity.getBallMeta().setPlayerTeamRedHitParticle(new SParticle(ParticleEffect.REDSTONE, 1, 1, 0, 0, 0).setColors(255, 0, 0));
                this.arenaEntity.getBallMeta().setPlayerTeamBlueHitParticle(new SParticle(ParticleEffect.REDSTONE, 1, 1, -1, -1, 1).setColors(0, 0, 255));
                this.arenaEntity.getBallMeta().setBallSpawnParticle(new SParticle(ParticleEffect.SMOKE_LARGE, 4, 0.0002, 2, 2, 2));
                this.arenaEntity.getBallMeta().setBallSpawnSound(new SoundBuilder("NOTE_BASS", 1.0, 1.0));
                this.arenaEntity.getBallMeta().setBallGoalParticle(new SParticle(ParticleEffect.NOTE, 4, 0.0002, 2, 2, 2).setNoteColor(2));
                this.arenaEntity.getBallMeta().setBallGoalSound(new SoundBuilder("NOTE_PLING", 1.0, 2.0));
                this.player.sendMessage(Language.PREFIX + "Reset this page.");
            } else if (number == 10) {
                this.player.sendMessage(Language.PREFIX + "Saved settings.");
                this.open(this.player, new MainSettingsPage(this.player, this.arenaEntity));
            }
        }

        @Override
        public void show() {
            this.player.sendMessage("");
            this.player.sendMessage(HEADER_STANDARD);
            this.player.sendMessage("");
            this.player.sendMessage(Language.PREFIX + "1 - Set hit sound");
            this.player.sendMessage(Language.PREFIX + "2 - Set hit particle");
            this.player.sendMessage(Language.PREFIX + "3 - Set hit-team-red particle");
            this.player.sendMessage(Language.PREFIX + "4 - Set hit-team-blue particle");
            this.player.sendMessage(Language.PREFIX + "5 - Set ball-spawn particle");
            this.player.sendMessage(Language.PREFIX + "6 - Set ball-spawn sound");
            this.player.sendMessage(Language.PREFIX + "7 - Set ball-goal particle");
            this.player.sendMessage(Language.PREFIX + "8 - Set ball-goal sound");
            this.player.sendMessage(Language.PREFIX + "9 - Reset this page");
            this.player.sendMessage(Language.PREFIX + "10 - Save ball settings");
            this.player.sendMessage(Language.PREFIX + ChatColor.GREEN + MENU_BACK);
            this.player.sendMessage(Language.PREFIX + ChatColor.RED + MENU_EXIT);
            this.player.sendMessage("");
            this.player.sendMessage(FOOTER_STANDARD);
            this.player.sendMessage("");
        }
    }

    private class AddtionalSettingPage extends SChatpage {
        private final ArenaEntity arenaEntity;

        AddtionalSettingPage(Player player, ArenaEntity arenaEntity) {
            super(player);
            this.arenaEntity = arenaEntity;
        }

        @Override
        public boolean playerPreChatEnter(String text) {
            if (this.lastNumber == 2 && tryPDouble(text))
                this.arenaEntity.getTeamMeta().setWalkingSpeed(Float.parseFloat(text));
            else
                return true;
            return false;
        }

        @Override
        public void onPlayerSelect(int number) {
            if (number == 1) {
                this.arenaEntity.getTeamMeta().setDamage(!this.arenaEntity.getTeamMeta().isDamageEnabled());
                if (this.arenaEntity.getTeamMeta().isDamageEnabled())
                    this.player.sendMessage(Language.PREFIX + "Enabled damage.");
                else
                    this.player.sendMessage(Language.PREFIX + "Disabled damage.");
            } else if (number == 2) {
                this.player.sendMessage(Language.PREFIX + "Enter the walking-speed (default 0.2): ");
            } else if (number == 3) {
                this.arenaEntity.getTeamMeta().setForceEvenTeams(!this.arenaEntity.getTeamMeta().isForceEvenTeamsEnabled());
                if (this.arenaEntity.getTeamMeta().isForceEvenTeamsEnabled())
                    this.player.sendMessage(Language.PREFIX + "Enabled force-even-teams.");
                else
                    this.player.sendMessage(Language.PREFIX + "Disabled force-even-teams.");
            } else if (number == 4) {
                this.open(this.player, new EditDoubleJumpPage(this.player, this.arenaEntity));
            } else if (number == 5) {
                this.open(this.player, new HologramSettingsPage(this.player, this.arenaEntity));
            } else if (number == 6) {
                if (NMSRegistry.getCurrencyName() == null)
                    this.player.sendMessage(Language.PREFIX + "Cannot find valid currency name. Did you install/setup Vault and an economy plugin?");
                else
                    this.open(this.player, new VaultSettings(this.player, this.arenaEntity));
            } else if (number == 7) {
                this.open(this.player, new SpawnerItems(this.player, this.arenaEntity));
            } else if (number == 8) {
                this.open(this.player, new GlowingSettingsPage(this.player, this.arenaEntity));
            } else if (number == 9) {
                this.arenaEntity.getTeamMeta().setForceEvenTeams(false);
                this.arenaEntity.getTeamMeta().setDamage(true);
                this.arenaEntity.getTeamMeta().setWalkingSpeed(0.2F);
                this.player.sendMessage(Language.PREFIX + "Reset this page.");
            } else if (number == 10) {
                this.player.sendMessage(Language.PREFIX + "Saved settings.");
                this.open(this.player, new MainSettingsPage(this.player, this.arenaEntity));
            }
        }

        @Override
        public void show() {
            this.player.sendMessage("");
            this.player.sendMessage(HEADER_STANDARD);
            this.player.sendMessage("");
            this.player.sendMessage(Language.PREFIX + "1 - Enable/Disable damage");
            this.player.sendMessage(Language.PREFIX + "2 - Enable/Disable force-even-teams");
            this.player.sendMessage(Language.PREFIX + "3 - Set walking-speed");
            this.player.sendMessage(Language.PREFIX + "4 - Set DoubleJump");
            this.player.sendMessage(Language.PREFIX + "5 - Set Hologram");
            this.player.sendMessage(Language.PREFIX + "6 - [Vault] Set rewards");
            this.player.sendMessage(Language.PREFIX + "7 - Item Spawner");
            this.player.sendMessage(Language.PREFIX + "8 - ScoreGlowing 1.9+");
            this.player.sendMessage(Language.PREFIX + "9 - Reset this page");
            this.player.sendMessage(Language.PREFIX + "10 - Save settings");
            this.player.sendMessage(Language.PREFIX + ChatColor.GREEN + MENU_BACK);
            this.player.sendMessage(Language.PREFIX + ChatColor.RED + MENU_EXIT);
            this.player.sendMessage("");
            this.player.sendMessage(FOOTER_STANDARD);
            this.player.sendMessage("");
        }
    }

    private class EditDoubleJumpPage extends SChatpage {
        private final ArenaEntity arenaEntity;

        private EditDoubleJumpPage(Player player, ArenaEntity entity) {
            super(player);
            this.arenaEntity = entity;
        }

        @Override
        public boolean playerPreChatEnter(String text) {
            if (this.lastNumber == 2 && tryPDouble(text))
                this.arenaEntity.getTeamMeta().getDoubleJumpMeta().setHorizontalStrength(Double.parseDouble(text));
            else if (this.lastNumber == 3 && tryPDouble(text))
                this.arenaEntity.getTeamMeta().getDoubleJumpMeta().setVerticalStrength(Double.parseDouble(text));
            else
                return true;
            return false;
        }

        @Override
        public void onPlayerSelect(int number) {
            if (number == 1) {
                this.arenaEntity.getTeamMeta().setAllowDoubleJump(!this.arenaEntity.getTeamMeta().isAllowDoubleJump());
                if (this.arenaEntity.getTeamMeta().isAllowDoubleJump())
                    this.player.sendMessage(Language.PREFIX + "Enabled double jump.");
                else
                    this.player.sendMessage(Language.PREFIX + "Disabled double jump.");
            } else if (number == 2) {
                this.player.sendMessage(Language.PREFIX + "Enter the amount of horizontal-strength (Default: 2.6):");
            } else if (number == 3) {
                this.player.sendMessage(Language.PREFIX + "Enter the amount of vertical-strength (Default: 1.0):");
            } else if (number == 4) {
                this.open(this.player, new EditParticlePage(this.player, this.arenaEntity.getTeamMeta().getDoubleJumpParticle(), this.arenaEntity));
            } else if (number == 5) {
                this.open(this.player, new EditSoundPage(this.player, this.arenaEntity.getTeamMeta().getDoubleJumpMeta().getSoundEffect(), this.arenaEntity));
            } else if (number == 6) {
                this.arenaEntity.getTeamMeta().setAllowDoubleJump(true);
                this.arenaEntity.getTeamMeta().setDoubleJumpParticle(new SParticle(ParticleEffect.EXPLOSION_NORMAL, 4, 0.0002, 2, 2, 2));
                this.arenaEntity.getTeamMeta()
                        .getDoubleJumpMeta()
                        .getSoundEffect()
                        .setName("GHAST_FIREBALL")
                        .setVolume(100)
                        .setPitch(1.0);
                this.arenaEntity.getTeamMeta().getDoubleJumpMeta().setHorizontalStrength(2.6);
                this.arenaEntity.getTeamMeta().getDoubleJumpMeta().setVerticalStrength(1.0);
                this.player.sendMessage(Language.PREFIX + "Reset this page.");
            } else if (number == 7) {
                this.player.sendMessage(Language.PREFIX + "Saved settings.");
                this.open(this.player, new AddtionalSettingPage(this.player, this.arenaEntity));
            }
        }

        @Override
        public void show() {
            this.player.sendMessage("");
            this.player.sendMessage(HEADER_STANDARD);
            this.player.sendMessage("");
            this.player.sendMessage(Language.PREFIX + "1 - Enable/Disable DoubleJump");
            this.player.sendMessage(Language.PREFIX + "2 - Set horizontal strength");
            this.player.sendMessage(Language.PREFIX + "3 - Set vertical strength");
            this.player.sendMessage(Language.PREFIX + "4 - Set particle");
            this.player.sendMessage(Language.PREFIX + "5 - Set sound");
            this.player.sendMessage(Language.PREFIX + "6 - Reset this page");
            this.player.sendMessage(Language.PREFIX + "7 - Save settings");
            this.player.sendMessage(Language.PREFIX + ChatColor.GREEN + MENU_BACK);
            this.player.sendMessage(Language.PREFIX + ChatColor.RED + MENU_EXIT);
            this.player.sendMessage("");
            this.player.sendMessage(FOOTER_STANDARD);
            this.player.sendMessage("");
        }
    }

    //-----------------------------------------------------

    private class EditParticlePage extends SChatpage {
        private final LightParticle particle;
        private final ArenaEntity entity;

        EditParticlePage(Player player, LightParticle particle, ArenaEntity entity) {
            super(player);
            this.particle = particle;
            this.entity = entity;
        }

        @Override
        public boolean playerPreChatEnter(String text) {
            if (this.lastNumber == 1 && SParticle.getParticleEffectFromName(text) != null)
                this.particle.setEffect(SParticle.getParticleEffectFromName(text));
            else if (this.lastNumber == 2 && tryPInt(text) && Integer.parseInt(text) > 0)
                this.particle.setAmount(Integer.parseInt(text));
            else if (this.lastNumber == 3 && tryPDouble(text) && Double.parseDouble(text) > 0)
                this.particle.setSpeed(Double.parseDouble(text));

            else if (this.lastNumber == 4 && tryPDouble(text) && Double.parseDouble(text) > 0)
                this.particle.setX(Double.parseDouble(text));
            else if (this.lastNumber == 5 && tryPDouble(text) && Double.parseDouble(text) > 0)
                this.particle.setY(Double.parseDouble(text));
            else if (this.lastNumber == 6 && tryPDouble(text) && Double.parseDouble(text) > 0)
                this.particle.setZ(Double.parseDouble(text));
            else
                return true;
            return false;
        }

        @Override
        public void onPlayerSelect(int number) {
            if (number == 1) {
                this.player.sendMessage(Language.PREFIX + "Enter the name of the effect:");
                this.player.sendMessage(Language.PREFIX + "Names: " + SParticle.getParticlesText());
            } else if (number == 2)
                this.player.sendMessage(Language.PREFIX + "Enter the amount of the effect:");
            else if (number == 3)
                this.player.sendMessage(Language.PREFIX + "Enter the speed of the effect:");
            else if (number == 4)
                this.player.sendMessage(Language.PREFIX + "Enter the offset X of the effect:");
            else if (number == 5)
                this.player.sendMessage(Language.PREFIX + "Enter the offset Y of the effect:");
            else if (number == 6)
                this.player.sendMessage(Language.PREFIX + "Enter the offset Z of the effect:");
            else if (number == 7) {
                if (this.particle.isNoteParticleEffect())
                    this.open(this.player, new EditColorParticlePage(this.player, this.particle, 2));
                else if (this.particle.isColorParticleEffect())
                    this.open(this.player, new EditColorParticlePage(this.player, this.particle, 1));
                else if (this.particle.isMaterialParticleEffect())
                    this.open(this.player, new EditMaterialParticlePage(this.player, this.particle));
                else
                    this.player.sendMessage(Language.PREFIX + ChatColor.RED + "This effect does not have anymore settings.");
            } else if (number == 8) {
                this.player.sendMessage(SParticle.getParticlesText());
            } else if (number == 9) {
                this.particle.setEnabled(!this.particle.isEnabled());
                if (this.particle.isEnabled()) {
                    this.player.sendMessage(Language.PREFIX + "Enabled particle effect.");
                } else {
                    this.player.sendMessage(Language.PREFIX + "Disabled particle effect.");
                }
            } else if (number == 10) {
                this.player.sendMessage(Language.PREFIX + "Saved particle effect.");
                this.open(this.player, new BallSoundParticlesSettingPage(this.player, this.entity));
            }
        }

        @Override
        public void show() {
            this.player.sendMessage("");
            this.player.sendMessage(HEADER_STANDARD);
            this.player.sendMessage("");
            this.player.sendMessage(this.particle.toString());
            this.player.sendMessage(Language.PREFIX + "1 - Set name");
            this.player.sendMessage(Language.PREFIX + "2 - Set amount");
            this.player.sendMessage(Language.PREFIX + "3 - Set speed");
            this.player.sendMessage(Language.PREFIX + "4 - Set offset x");
            this.player.sendMessage(Language.PREFIX + "5 - Set offset y");
            this.player.sendMessage(Language.PREFIX + "6 - Set offset z");
            this.player.sendMessage(Language.PREFIX + "7 - More settings");
            this.player.sendMessage(Language.PREFIX + "8 - Show effect names");
            this.player.sendMessage(Language.PREFIX + "9 - Toggle particle effect");
            this.player.sendMessage(Language.PREFIX + "10 - Save particle effect");
            this.player.sendMessage(Language.PREFIX + ChatColor.GREEN + MENU_BACK);
            this.player.sendMessage(Language.PREFIX + ChatColor.RED + MENU_EXIT);
            this.player.sendMessage("");
            this.player.sendMessage(FOOTER_STANDARD);
            this.player.sendMessage("");
        }
    }

    private static class EditColorParticlePage extends SChatpage {
        private final LightParticle particle;
        private final int type;

        EditColorParticlePage(Player player, LightParticle particle, int type) {
            super(player);
            this.particle = particle;
            this.type = type;
        }

        @Override
        public boolean playerPreChatEnter(String text) {
            if (this.type == 1) {
                if (this.lastNumber == 1 && tryPInt(text))
                    this.particle.setRed(Integer.parseInt(text));
                else if (this.lastNumber == 2 && tryPInt(text))
                    this.particle.setGreen(Integer.parseInt(text));
                else if (this.lastNumber == 3 && tryPInt(text))
                    this.particle.setBlue(Integer.parseInt(text));
                else
                    return true;
                return false;
            } else {
                if (this.lastNumber == 1 && tryPInt(text) && Integer.parseInt(text) >= 0 && Integer.parseInt(text) <= 24) {
                    this.particle.setRed(Integer.parseInt(text));
                    this.particle.setBlue(0);
                    this.particle.setGreen(0);
                } else
                    return true;
                return false;
            }
        }

        @Override
        public void onPlayerSelect(int number) {
            if (this.type == 1) {
                if (number == 1)
                    this.player.sendMessage(Language.PREFIX + "Enter the value of red:");
                else if (number == 2)
                    this.player.sendMessage(Language.PREFIX + "Enter the value of green:");
                else if (number == 3)
                    this.player.sendMessage(Language.PREFIX + "Enter the value of blue:");
                else if (number == 4) {
                    this.player.sendMessage(Language.PREFIX + "Saved color settings.");
                    this.open(this.player, this.getLastInstance());
                }
            } else {
                if (number == 1)
                    this.player.sendMessage(Language.PREFIX + "Enter the value of the note:");
                else if (number == 2) {
                    this.player.sendMessage(Language.PREFIX + "Saved color settings.");
                    this.open(this.player, this.getLastInstance());
                }
            }
        }

        @Override
        public void show() {
            this.player.sendMessage("");
            this.player.sendMessage(HEADER_STANDARD);
            this.player.sendMessage("");
            if (this.type == 1) {
                this.player.sendMessage(Language.PREFIX + "1 - Set color red (special)");
                this.player.sendMessage(Language.PREFIX + "2 - Set color green (special)");
                this.player.sendMessage(Language.PREFIX + "3 - Set color blue (special)");
                this.player.sendMessage(Language.PREFIX + "4 - Save color settings");
            } else {
                this.player.sendMessage(Language.PREFIX + "1 - Set note color (0-24)");
                this.player.sendMessage(Language.PREFIX + "2 - Save color settings");
            }
            this.player.sendMessage(Language.PREFIX + ChatColor.GREEN + MENU_BACK);
            this.player.sendMessage(Language.PREFIX + ChatColor.RED + MENU_EXIT);
            this.player.sendMessage("");
            this.player.sendMessage(FOOTER_STANDARD);
            this.player.sendMessage("");
        }
    }

    private static class EditMaterialParticlePage extends SChatpage {
        private final LightParticle particle;

        EditMaterialParticlePage(Player player, LightParticle particle) {
            super(player);
            this.particle = particle;
        }

        @Override
        public boolean playerPreChatEnter(String text) {
            if (this.lastNumber == 1 && tryPInt(text) && Material.getMaterial(Integer.parseInt(text)) != null)
                this.particle.setMaterial(Material.getMaterial(Integer.parseInt(text)));
            else if (this.lastNumber == 2 && tryPInt(text))
                this.particle.setData((byte) Integer.parseInt(text));
            else
                return true;
            return false;
        }

        @Override
        public void onPlayerSelect(int number) {
            if (number == 1)
                this.player.sendMessage(Language.PREFIX + "Enter the id of the material:");
            else if (number == 2)
                this.player.sendMessage(Language.PREFIX + "Enter the data of the material:");
            else if (number == 3) {
                this.player.sendMessage(Language.PREFIX + "Saved material settings.");
                this.open(this.player, this.getLastInstance());
            }
        }

        @Override
        public void show() {
            this.player.sendMessage("");
            this.player.sendMessage(HEADER_STANDARD);
            this.player.sendMessage("");
            this.player.sendMessage(Language.PREFIX + "1 - Set id of the material (special)");
            this.player.sendMessage(Language.PREFIX + "2 - Set data of the material (special)");
            this.player.sendMessage(Language.PREFIX + "3 - Save material settings");
            this.player.sendMessage(Language.PREFIX + ChatColor.GREEN + MENU_BACK);
            this.player.sendMessage(Language.PREFIX + ChatColor.RED + MENU_EXIT);
            this.player.sendMessage("");
            this.player.sendMessage(FOOTER_STANDARD);
            this.player.sendMessage("");
        }
    }

    private class EditSoundPage extends SChatpage {
        private final SoundMeta sound;
        private final ArenaEntity entity;

        EditSoundPage(Player player, SoundMeta sound, ArenaEntity entity) {
            super(player);
            this.sound = sound;
            this.entity = entity;
        }

        @Override
        public boolean playerPreChatEnter(String text) {
            if (this.lastNumber == 1 && SoundBuilder.getSoundFromName(text) != null)
                this.sound.setName(text);
            else if (this.lastNumber == 2 && tryPDouble(text) && Double.parseDouble(text) > 0)
                this.sound.setVolume(Double.parseDouble(text));
            else if (this.lastNumber == 3 && tryPDouble(text) && Double.parseDouble(text) > 0)
                this.sound.setPitch(Double.parseDouble(text));
            else
                return true;
            return false;
        }

        @Override
        public void onPlayerSelect(int number) {
            if (number == 1) {
                this.player.sendMessage(Language.PREFIX + "Enter the name of the sound:");
                this.player.sendMessage(Language.PREFIX + "Names: " + SoundBuilder.getAvailableSounds());
            } else if (number == 2)
                this.player.sendMessage(Language.PREFIX + "Enter the volume of the sound:");
            else if (number == 3)
                this.player.sendMessage(Language.PREFIX + "Enter the pitch of the sound:");
            else if (number == 4) {
                this.player.sendMessage("Saved sound.");
                this.open(this.player, new BallSoundParticlesSettingPage(this.player, this.entity));
            }
        }

        @Override
        public void show() {
            this.player.sendMessage("");
            this.player.sendMessage(HEADER_STANDARD);
            this.player.sendMessage("");
            this.player.sendMessage(this.sound.toString());
            this.player.sendMessage(Language.PREFIX + "1 - Set name");
            this.player.sendMessage(Language.PREFIX + "2 - Set volume");
            this.player.sendMessage(Language.PREFIX + "3 - Set pitch");
            this.player.sendMessage(Language.PREFIX + "4 - Save sound");
            this.player.sendMessage(Language.PREFIX + ChatColor.GREEN + MENU_BACK);
            this.player.sendMessage(Language.PREFIX + ChatColor.RED + MENU_EXIT);
            this.player.sendMessage("");
            this.player.sendMessage(FOOTER_STANDARD);
            this.player.sendMessage("");
        }
    }

    private class EditBossBarSettings extends SChatpage {
        private final ArenaEntity arenaEntity;
        private final int mode;

        EditBossBarSettings(Player player, ArenaEntity entity, int mode) {
            super(player);
            this.arenaEntity = entity;
            this.mode = mode;
        }

        @Override
        public boolean playerPreChatEnter(String text) {
            if (this.mode == 0 && this.lastNumber == 1) {
                this.arenaEntity.getTeamMeta().setBossBarPluginMessage(text);
            } else if (this.mode == 1 && this.lastNumber == 1) {
                this.arenaEntity.getTeamMeta().getBossBar().setMessage(text);
            } else if (this.mode == 1 && this.lastNumber == 3 && FastBossBar.getColorFromName(text) != -1) {
                this.arenaEntity.getTeamMeta().getBossBar().setColor(FastBossBar.getColorFromName(text));
            } else if (this.mode == 1 && this.lastNumber == 4 && FastBossBar.getFlagFromName(text) != -1) {
                this.arenaEntity.getTeamMeta().getBossBar().setFlag(FastBossBar.getFlagFromName(text));
            } else if (this.mode == 1 && this.lastNumber == 5 && FastBossBar.getStyleFromName(text) != -1) {
                this.arenaEntity.getTeamMeta().getBossBar().setStyle(FastBossBar.getStyleFromName(text));
            } else
                return true;
            return false;
        }

        @Override
        public void onPlayerSelect(int number) {
            if (this.mode == 0) {
                if (number == 1) {
                    this.player.sendMessage(Language.PREFIX + "Enter the title for the bossbar:");
                    this.player.sendMessage(Language.PREFIX + "Example: ':red :redcolor:redscore : :bluecolor:bluescore :blue'");
                } else if (number == 2) {
                    this.arenaEntity.getTeamMeta().setBossBarPluginEnabled(!this.arenaEntity.getTeamMeta().isBossBarPluginEnabled());
                    if (this.arenaEntity.getTeamMeta().isBossBarPluginEnabled())
                        this.player.sendMessage(Language.PREFIX + "Enabled bossbar.");
                    else
                        this.player.sendMessage(Language.PREFIX + "Disabled bossbar.");
                } else if (number == 3) {
                    this.player.sendMessage(Language.PREFIX + "Saved bossbar settings.");
                    this.open(this.player, new AnnouncementSettingsPage(this.player, this.arenaEntity));
                }
            } else if (this.mode == 1) {
                if (number == 1) {
                    this.player.sendMessage(Language.PREFIX + "Enter the title for the bossbar:");
                    this.player.sendMessage(Language.PREFIX + "Example: ':red :redcolor:redscore : :bluecolor:bluescore :blue'");
                } else if (number == 2) {
                    this.arenaEntity.getTeamMeta().getBossBar().setEnabled(!this.arenaEntity.getTeamMeta().getBossBar().isEnabled());
                    if (this.arenaEntity.getTeamMeta().getBossBar().isEnabled())
                        this.player.sendMessage(Language.PREFIX + "Enabled bossbar.");
                    else
                        this.player.sendMessage(Language.PREFIX + "Disabled bossbar.");
                } else if (number == 3) {
                    this.player.sendMessage(Language.PREFIX + "Enter the name of the color:");
                    this.player.sendMessage(Language.PREFIX + "Colors: " + FastBossBar.getColorsText());
                } else if (number == 4) {
                    this.player.sendMessage(Language.PREFIX + "Enter the name of the flag:");
                    this.player.sendMessage(Language.PREFIX + "Flags: " + FastBossBar.getFlagsText());
                } else if (number == 5) {
                    this.player.sendMessage(Language.PREFIX + "Enter the name of the style:");
                    this.player.sendMessage(Language.PREFIX + "Styles: " + FastBossBar.getStylesText());
                } else if (number == 6) {
                    ((TeamMetaEntity) this.arenaEntity.getTeamMeta()).setBossBarLight(new FastBossBar(":red :redcolor:redscore : :bluecolor:bluescore :blue"));
                    this.player.sendMessage(Language.PREFIX + "Reset this page.");
                } else if (number == 7) {
                    this.player.sendMessage(Language.PREFIX + "Saved bossbar settings.");
                    this.open(this.player, new AnnouncementSettingsPage(this.player, this.arenaEntity));
                }
            }
        }

        @Override
        public void show() {
            this.player.sendMessage("");
            this.player.sendMessage(HEADER_STANDARD);
            this.player.sendMessage("");
            if (this.mode == 0) {
                this.player.sendMessage(Language.PREFIX + "1 - Set message");
                this.player.sendMessage(Language.PREFIX + "2 - Toggle bossbar");
                this.player.sendMessage(Language.PREFIX + "3 - Save bossbar settings");
            } else if (this.mode == 1) {
                this.player.sendMessage(Language.PREFIX + "1 - Set message");
                this.player.sendMessage(Language.PREFIX + "2 - Toggle bossbar");
                this.player.sendMessage(Language.PREFIX + "3 - Set color");
                this.player.sendMessage(Language.PREFIX + "4 - Set flag");
                this.player.sendMessage(Language.PREFIX + "5 - Set style");
                this.player.sendMessage(Language.PREFIX + "6 - Reset this page");
                this.player.sendMessage(Language.PREFIX + "7 - Save bossbar settings");
            }
            this.player.sendMessage(Language.PREFIX + ChatColor.GREEN + MENU_BACK);
            this.player.sendMessage(Language.PREFIX + ChatColor.RED + MENU_EXIT);
            this.player.sendMessage("");
            this.player.sendMessage(FOOTER_STANDARD);
            this.player.sendMessage("");
        }
    }

    private class VaultSettings extends SChatpage {
        private final ArenaEntity arenaEntity;

        VaultSettings(Player player, ArenaEntity entity) {
            super(player);
            this.arenaEntity = entity;
        }

        @Override
        public boolean playerPreChatEnter(String text) {
            if (this.lastNumber == 1 && tryPInt(text)) {
                this.arenaEntity.getTeamMeta().setRewardGoals(Integer.parseInt(text));
            } else if (this.lastNumber == 2 && tryPInt(text)) {
                this.arenaEntity.getTeamMeta().setRewardGames(Integer.parseInt(text));
            } else if (this.lastNumber == 3 && tryPInt(text)) {
                this.arenaEntity.getTeamMeta().setRewardWinning(Integer.parseInt(text));
            } else
                return true;
            return false;
        }

        @Override
        public void onPlayerSelect(int number) {
            if (number == 1) {
                this.player.sendMessage(Language.PREFIX + "Enter the amount of " + NMSRegistry.getCurrencyName() + " a player gets for shooting goals:");
            } else if (number == 2) {
                this.player.sendMessage(Language.PREFIX + "Enter the amount of " + NMSRegistry.getCurrencyName() + " a player gets for playing one whole game:");
            } else if (number == 3) {
                this.player.sendMessage(Language.PREFIX + "Enter the amount of " + NMSRegistry.getCurrencyName() + " a player gets for winning a game:");
            } else if (number == 4) {
                this.player.sendMessage(Language.PREFIX + "Saved vault settings.");
                this.open(this.player, new AllModesSettingsPage(this.player, this.arenaEntity));
            }
        }

        @Override
        public void show() {
            this.player.sendMessage("");
            this.player.sendMessage(HEADER_STANDARD);
            this.player.sendMessage("");
            this.player.sendMessage(Language.PREFIX + "1 - Set " + NMSRegistry.getCurrencyName() + " for goals shot");
            this.player.sendMessage(Language.PREFIX + "2 - Set " + NMSRegistry.getCurrencyName() + " for attended games");
            this.player.sendMessage(Language.PREFIX + "3 - Set " + NMSRegistry.getCurrencyName() + " for won games");
            this.player.sendMessage(Language.PREFIX + "4 - Save vault settings");
            this.player.sendMessage(Language.PREFIX + ChatColor.GREEN + MENU_BACK);
            this.player.sendMessage(Language.PREFIX + ChatColor.RED + MENU_EXIT);
            this.player.sendMessage("");
            this.player.sendMessage(FOOTER_STANDARD);
            this.player.sendMessage("");
        }
    }

    private class BossBarMiddlePage extends SChatpage {
        private final ArenaEntity arenaEntity;

        BossBarMiddlePage(Player player, ArenaEntity entity) {
            super(player);
            this.arenaEntity = entity;
        }

        @Override
        public void onPlayerSelect(int number) {
            if (number == 1) {
                this.open(this.player, new EditBossBarSettings(this.player, this.arenaEntity, 0));
            } else if (number == 2) {
                this.open(this.player, new EditBossBarSettings(this.player, this.arenaEntity, 1));
            } else if (number == 3) {
                this.open(this.player, new AnnouncementSettingsPage(this.player, this.arenaEntity));
            }
        }

        @Override
        public void show() {
            this.player.sendMessage("");
            this.player.sendMessage(HEADER_STANDARD);
            this.player.sendMessage("");
            this.player.sendMessage(Language.PREFIX + "1 - [Requires BossBarPlugin] BossBar");
            this.player.sendMessage(Language.PREFIX + "2 - [Requires +1.9.x] BossBar");
            this.player.sendMessage(Language.PREFIX + "3 - Save bossbar settings");
            this.player.sendMessage(Language.PREFIX + ChatColor.GREEN + MENU_BACK);
            this.player.sendMessage(Language.PREFIX + ChatColor.RED + MENU_EXIT);
            this.player.sendMessage("");
            this.player.sendMessage(FOOTER_STANDARD);
            this.player.sendMessage("");
        }
    }

    private class SpectatorsModePage extends SChatpage {
        private final ArenaEntity arenaEntity;

        SpectatorsModePage(Player player, ArenaEntity entity) {
            super(player);
            this.arenaEntity = entity;
        }

        @Override
        public boolean playerPreChatEnter(String text) {
            if (this.lastNumber == 2 && tryPInt(text)) {
                this.arenaEntity.getTeamMeta().setSpecatorradius(Integer.parseInt(text));
            } else
                return true;
            return false;
        }

        @Override
        public void onPlayerSelect(int number) {
            if (number == 1) {
                this.arenaEntity.getTeamMeta().setSpecatorMessages(!this.arenaEntity.getTeamMeta().isSpectatorMessagesEnabled());
                if (this.arenaEntity.getTeamMeta().isSpectatorMessagesEnabled())
                    this.player.sendMessage(Language.PREFIX + "Enabled spectator annoucements.");
                else
                    this.player.sendMessage(Language.PREFIX + "Disabled sspectatorannoucements.");
            } else if (number == 2) {
                this.player.sendMessage(Language.PREFIX + "Enter the radius where players receive the latest announcements:");
            } else if (number == 3) {
                this.open(this.player, new GeneralSettingsPage(this.player, this.arenaEntity));
            }
        }

        @Override
        public void show() {
            this.player.sendMessage("");
            this.player.sendMessage(HEADER_STANDARD);
            this.player.sendMessage("");
            this.player.sendMessage(Language.PREFIX + "1 - Toggle nearby spectator announcements");
            this.player.sendMessage(Language.PREFIX + "2 - Set spectator radius");
            this.player.sendMessage(Language.PREFIX + "3 - Save spectator settings");
            this.player.sendMessage(Language.PREFIX + ChatColor.GREEN + MENU_BACK);
            this.player.sendMessage(Language.PREFIX + ChatColor.RED + MENU_EXIT);
            this.player.sendMessage("");
            this.player.sendMessage(FOOTER_STANDARD);
            this.player.sendMessage("");
        }
    }

    private class WallBouncingPage extends SChatpage {
        private final ArenaEntity arenaEntity;

        WallBouncingPage(Player player, ArenaEntity entity) {
            super(player);
            this.arenaEntity = entity;
        }

        @Override
        public boolean playerPreChatEnter(String text) {
            if (this.lastNumber == 1) {
                this.arenaEntity.addBounceType(text);
                this.player.sendMessage(Language.PREFIX + "Added id.");
            } else if (this.lastNumber == 2) {
                if (this.arenaEntity.getBounceTypes().contains(text)) {
                    this.arenaEntity.removeBounceType(text);
                    this.player.sendMessage(Language.PREFIX + "Removed id.");
                } else {
                    this.player.sendMessage(Language.PREFIX + "Cannot find '" + text + "'.");
                }
            } else
                return true;
            return false;
        }

        @Override
        public void onPlayerSelect(int number) {
            if (number == 1) {
                this.player.sendMessage(Language.PREFIX + "Enter the id of the block to add wallbouncing: (example: 15:3 or 5)");
            } else if (number == 2) {
                this.player.sendMessage(Language.PREFIX + "Enter the id of the block to remove wallbouncing:");
            } else if (number == 3) {
                this.player.sendMessage(Language.PREFIX + "Bouncing ids:");
                for (final String s : this.arenaEntity.getBounceTypes()) {
                    this.player.sendMessage(Language.PREFIX + s);
                }
            } else if (number == 4) {
                this.open(this.player, new GeneralSettingsPage(this.player, this.arenaEntity));
            }
        }

        @Override
        public void show() {
            this.player.sendMessage("");
            this.player.sendMessage(HEADER_STANDARD);
            this.player.sendMessage("");
            this.player.sendMessage(Language.PREFIX + "1 - Add block-type to bouncing");
            this.player.sendMessage(Language.PREFIX + "2 - Remove blocktype from bouncing");
            this.player.sendMessage(Language.PREFIX + "3 - Show bouncing blocks");
            this.player.sendMessage(Language.PREFIX + "4 - Save boucing blocks");
            this.player.sendMessage(Language.PREFIX + ChatColor.GREEN + MENU_BACK);
            this.player.sendMessage(Language.PREFIX + ChatColor.RED + MENU_EXIT);
            this.player.sendMessage("");
            this.player.sendMessage(FOOTER_STANDARD);
            this.player.sendMessage("");
        }
    }

    private class ScoreboardPage extends SChatpage {
        private final ArenaEntity arenaEntity;

        ScoreboardPage(Player player, ArenaEntity entity) {
            super(player);
            this.arenaEntity = entity;
        }

        @Override
        public boolean playerPreChatEnter(String text) {
            if (this.lastNumber == 2) {
                this.arenaEntity.getTeamMeta().setScoreboardTitle(text);
            } else
                return true;
            return false;
        }

        @Override
        public void onPlayerSelect(int number) {
            if (number == 1) {
                this.arenaEntity.getTeamMeta().setScoreboardEnabled(!this.arenaEntity.getTeamMeta().isScoreboardEnabled());
                if (this.arenaEntity.getTeamMeta().isScoreboardEnabled())
                    this.player.sendMessage(Language.PREFIX + "Enabled the scoreboard.");
                else
                    this.player.sendMessage(Language.PREFIX + "Disabled the scoreboard.");
            } else if (number == 2) {
                this.player.sendMessage(Language.PREFIX + "Enter the title of the scoreboard:");
                this.player.sendMessage(Language.PREFIX + "Example: '&a&lBlockBall'");
            } else if (number == 3) {
                this.open(this.player, new ScoreboardLinesPage(this.player, this.arenaEntity));
            } else if (number == 4) {
                this.player.sendMessage(Language.PREFIX + "Scoreboard lines: ");
                final String[] lines = this.arenaEntity.getTeamMeta().getScoreboardLines();
                for (int i = 0; i < lines.length; i++) {
                    this.player.sendMessage((i + 1) + " - " + ChatColor.translateAlternateColorCodes('&', lines[i]));
                }
            } else if (number == 5) {
                this.arenaEntity.getTeamMeta().setScoreboardEnabled(true);
                this.arenaEntity.getTeamMeta().setScoreboardTitle("&a&lBlockBall");
                this.arenaEntity.getTeamMeta().setScoreboardLines(new String[]{"", "&eTime:", ":countdown", "&m           ", ":red", "&6vs", ":blue", "&m           ", "&aScore:", ":redcolor:redscore &r: :bluecolor:bluescore", "&m           "});
                this.player.sendMessage(Language.PREFIX + "Reset this page.");
            } else if (number == 6) {
                this.open(this.player, new AnnouncementSettingsPage(this.player, this.arenaEntity));
            }
        }

        @Override
        public void show() {
            this.player.sendMessage("");
            this.player.sendMessage(HEADER_STANDARD);
            this.player.sendMessage("");
            this.player.sendMessage(Language.PREFIX + "1 - Toggle scoreboard");
            this.player.sendMessage(Language.PREFIX + "2 - Set title");
            this.player.sendMessage(Language.PREFIX + "3 - Edit lines");
            this.player.sendMessage(Language.PREFIX + "4 - Show lines");
            this.player.sendMessage(Language.PREFIX + "5 - Reset this page");
            this.player.sendMessage(Language.PREFIX + "6 - Save scoreboard");
            this.player.sendMessage(Language.PREFIX + ChatColor.GREEN + MENU_BACK);
            this.player.sendMessage(Language.PREFIX + ChatColor.RED + MENU_EXIT);
            this.player.sendMessage("");
            this.player.sendMessage(FOOTER_STANDARD);
            this.player.sendMessage("");
        }
    }

    private class ScoreboardLinesPage extends SChatpage {
        private final ArenaEntity arenaEntity;
        private int selectedLineNumber = 1;

        ScoreboardLinesPage(Player player, ArenaEntity entity) {
            super(player);
            this.arenaEntity = entity;
        }

        @Override
        public boolean playerPreChatEnter(String text) {
            if (this.lastNumber == 1 && tryPInt(text) && Integer.parseInt(text) > 0) {
                this.selectedLineNumber = Integer.parseInt(text);
            } else if (this.lastNumber == 2) {
                final int lineNumber = this.selectedLineNumber - 1;
                final List<String> lines = new ArrayList<>(Arrays.asList(this.arenaEntity.getTeamMeta().getScoreboardLines()));
                while (lineNumber >= lines.size()) {
                    lines.add("");
                }
                final String[] finalLines = lines.toArray(new String[lines.size()]);
                finalLines[lineNumber] = text;
                this.arenaEntity.getTeamMeta().setScoreboardLines(finalLines);
            } else
                return true;
            return false;
        }

        @Override
        public void onPlayerSelect(int number) {
            if (number == 1) {
                this.player.sendMessage(Language.PREFIX + "Enter the number of the line you want to edit:");
            } else if (number == 2) {
                this.player.sendMessage(Language.PREFIX + "Enter the text of this line:");
                this.player.sendMessage(Language.PREFIX + "Example :red/:blue or :redscore/:bluescore");
            } else if (number == 3) {
                final int lineNumber = this.selectedLineNumber - 1;
                final List<String> lines = new ArrayList<>(Arrays.asList(this.arenaEntity.getTeamMeta().getScoreboardLines()));
                if (lineNumber < lines.size()) {
                    lines.remove(lineNumber);
                    this.arenaEntity.getTeamMeta().setScoreboardLines(lines.toArray(new String[lines.size()]));
                    this.player.sendMessage(Language.PREFIX + "Deleted line.");
                } else {
                    this.player.sendMessage(Language.PREFIX + "This line is already deleted.");
                }
            } else if (number == 4) {
                this.open(this.player, new ScoreboardPage(this.player, this.arenaEntity));
            }
        }

        @Override
        public void show() {
            this.player.sendMessage("");
            this.player.sendMessage(HEADER_STANDARD);
            this.player.sendMessage("");
            this.player.sendMessage(Language.PREFIX + "Selected line: " + this.selectedLineNumber);
            this.player.sendMessage(Language.PREFIX + "1 - Select line");
            this.player.sendMessage(Language.PREFIX + "2 - Set text");
            this.player.sendMessage(Language.PREFIX + "3 - Delete");
            this.player.sendMessage(Language.PREFIX + "4 - Save lines");
            this.player.sendMessage(Language.PREFIX + ChatColor.GREEN + MENU_BACK);
            this.player.sendMessage(Language.PREFIX + ChatColor.RED + MENU_EXIT);
            this.player.sendMessage("");
            this.player.sendMessage(FOOTER_STANDARD);
            this.player.sendMessage("");
        }
    }

    ///-------------------------------------

    private class SpawnerItems extends SChatpage {
        private final ArenaEntity arenaEntity;
        private int cache;

        SpawnerItems(Player player, ArenaEntity entity) {
            super(player);
            this.arenaEntity = entity;
        }

        @Override
        public boolean playerPreChatEnter(String text) {
            if (this.cache == 0 && this.lastNumber == 1) {
                if (Spawnrate.getSpawnrateFromName(text) == null)
                    this.player.sendMessage(Language.PREFIX + "This type does not exist.");
                else
                    this.arenaEntity.getBoostItemHandler().setSpawnRate(Spawnrate.getSpawnrateFromName(text));
            } else
                return true;
            return false;
        }

        @Override
        public void onPlayerSelect(int number) {
            if (this.cache == 0) {
                if (number == 1) {
                    this.player.sendMessage(Language.PREFIX + "Enter the spawn rate of the booster items:");
                    this.player.sendMessage(Language.PREFIX + "[None, Little, Medium, High, Highest]");
                } else if (number == 2) {
                    final BoostItem item = ItemSpawner.createBoostItem();
                    this.arenaEntity.getBoostItemHandler().setBoostItem(item);
                    this.open(this.player, new EditSpawnerItems(this.player, this.arenaEntity, item));
                } else if (number == 3) {
                    this.player.sendMessage(Language.PREFIX + "Enter number of the item to edit it:");
                    this.listItems();
                    this.cache = 1;
                } else if (number == 4) {
                    this.player.sendMessage(Language.PREFIX + "Enter number of the item to remove it:");
                    this.listItems();
                    this.cache = 2;
                } else if (number == 5) {
                    this.listItems();
                } else if (number == 6) {
                    this.arenaEntity.getBoostItemHandler().clear();
                    this.player.sendMessage(Language.PREFIX + "Reset this page.");
                } else if (number == 7) {
                    this.open(this.player, new GeneralSettingsPage(this.player, this.arenaEntity));
                }
            }
            if (this.cache == 1) {
                if (number >= 0 && number < this.arenaEntity.getBoostItemHandler().getBoostItems().size()) {
                    this.open(this.player, new EditSpawnerItems(this.player, this.arenaEntity, this.arenaEntity.getBoostItemHandler().getBoostItems().get(number)));
                }
            }
            if (this.cache == 2) {
                if (number >= 0 && number < this.arenaEntity.getBoostItemHandler().getBoostItems().size()) {
                    this.arenaEntity.getBoostItemHandler().removeBoostItem(this.arenaEntity.getBoostItemHandler().getBoostItems().get(number));
                }
            }
        }

        @Override
        public void show() {
            this.player.sendMessage("");
            this.player.sendMessage(HEADER_STANDARD);
            this.player.sendMessage("");
            this.player.sendMessage(Language.PREFIX + "1 - Set spawnrate");
            this.player.sendMessage(Language.PREFIX + "2 - Add item");
            this.player.sendMessage(Language.PREFIX + "3 - Edit item");
            this.player.sendMessage(Language.PREFIX + "4 - Remove item");
            this.player.sendMessage(Language.PREFIX + "5 - List items");
            this.player.sendMessage(Language.PREFIX + "6 - Reset this page");
            this.player.sendMessage(Language.PREFIX + "7 - Save boostitems");
            this.player.sendMessage(Language.PREFIX + ChatColor.GREEN + MENU_BACK);
            this.player.sendMessage(Language.PREFIX + ChatColor.RED + MENU_EXIT);
            this.player.sendMessage("");
            this.player.sendMessage(FOOTER_STANDARD);
            this.player.sendMessage("");
        }

        private void listItems() {
            int i = 0;
            for (final BoostItem boostItem : this.arenaEntity.getBoostItemHandler().getBoostItems()) {
                if (Material.getMaterial(boostItem.getId()) != null)
                    this.player.sendMessage(Language.PREFIX + i + " - " + Material.getMaterial(boostItem.getId()).name().toUpperCase() + '-' + boostItem.getDamage());
                else
                    this.player.sendMessage(Language.PREFIX + i + " - " + boostItem.getId() + '-' + boostItem.getDamage());
                i++;
            }
        }
    }

    private class EditSpawnerItems extends SChatpage {
        private final ArenaEntity arenaEntity;
        private final BoostItem boostItem;
        private int type;

        EditSpawnerItems(Player player, ArenaEntity entity, BoostItem boostItem) {
            super(player);
            this.arenaEntity = entity;
            this.boostItem = boostItem;
        }

        @Override
        public boolean playerPreChatEnter(String text) {
            if (this.lastNumber == 1 && tryPInt(text)) {
                this.boostItem.setId(Integer.parseInt(text));
            } else if (this.lastNumber == 2 && tryPInt(text)) {
                this.boostItem.setDamage(Integer.parseInt(text));
            } else if (this.lastNumber == 3) {
                this.boostItem.setOwner(text);
            } else if (this.lastNumber == 4) {
                if (Spawnrate.getSpawnrateFromName(text) == null)
                    this.player.sendMessage(Language.PREFIX + "This type does not exist.");
                else
                    this.boostItem.setSpawnrate(Spawnrate.getSpawnrateFromName(text));
            } else if (this.lastNumber == 5) {
                this.boostItem.setDisplayName(text);
            } else if (this.lastNumber == 7) {
                if (PotionEffectBuilder.getPotionEffectFromName(text) == null || this.boostItem.getPotionEffect(PotionEffectBuilder.getPotionEffectFromName(text)) == null)
                    this.player.sendMessage(Language.PREFIX + "This type does not exist.");
                else
                    this.open(this.player, new EditPotionEffect(this.player, this.arenaEntity, this.boostItem.getPotionEffect(PotionEffectBuilder.getPotionEffectFromName(text))));
            } else if (this.lastNumber == 8) {
                if (PotionEffectBuilder.getPotionEffectFromName(text) == null || this.boostItem.getPotionEffect(PotionEffectBuilder.getPotionEffectFromName(text)) == null)
                    this.player.sendMessage(Language.PREFIX + "This type does not exist.");
                else
                    this.boostItem.removePotionEffect(PotionEffectBuilder.getPotionEffectFromName(text));
            } else
                return true;
            return false;
        }

        @Override
        public void onPlayerSelect(int number) {
            if (number == 1) {
                this.player.sendMessage(Language.PREFIX + "Enter the id of the item:");
            } else if (number == 2) {
                this.player.sendMessage(Language.PREFIX + "Enter the damage of the item:");
            } else if (number == 3) {
                this.player.sendMessage(Language.PREFIX + "Enter the owner(skull) of the item:");
            } else if (number == 4) {
                this.player.sendMessage(Language.PREFIX + "Enter the individual spawnrate of the item:");
                this.player.sendMessage(Language.PREFIX + "[None, Little, Medium, High, Highest]");
            } else if (number == 5) {
                this.player.sendMessage(Language.PREFIX + "Enter the displayname of the item:");
            } else if (number == 6) {
                final PotionEffectMeta potioneffect = new PotionEffectBuilder();
                this.boostItem.setPotionEffect(potioneffect);
                this.open(this.player, new EditPotionEffect(this.player, this.arenaEntity, potioneffect));
            } else if (number == 7) {
                this.player.sendMessage(Language.PREFIX + "Type the name to edit the potioneffect:");
                for (final PotionEffectMeta lightPotioneffect : this.boostItem.getPotionEffects()) {
                    this.player.sendMessage(Language.PREFIX + lightPotioneffect.getType());
                }
            } else if (number == 8) {
                this.player.sendMessage(Language.PREFIX + "Type the name to remove the potioneffect:");
                for (final PotionEffectMeta lightPotioneffect : this.boostItem.getPotionEffects()) {
                    this.player.sendMessage(Language.PREFIX + lightPotioneffect.getType());
                }
            } else if (number == 9) {
                for (final PotionEffectMeta lightPotioneffect : this.boostItem.getPotionEffects()) {
                    this.player.sendMessage(Language.PREFIX + lightPotioneffect.getType());
                }
            } else if (number == 10) {
                this.open(this.player, new SpawnerItems(this.player, this.arenaEntity));
            }
        }

        @Override
        public void show() {
            this.player.sendMessage("");
            this.player.sendMessage(HEADER_STANDARD);
            this.player.sendMessage("");
            this.player.sendMessage(Language.PREFIX + "1 - Set item id");
            this.player.sendMessage(Language.PREFIX + "2 - Set item damage");
            this.player.sendMessage(Language.PREFIX + "3 - Set item owner");
            this.player.sendMessage(Language.PREFIX + "4 - Set spawnrate");
            this.player.sendMessage(Language.PREFIX + "5 - Set displayname");
            this.player.sendMessage(Language.PREFIX + "6 - Add potioneffect");
            this.player.sendMessage(Language.PREFIX + "7 - Edit potioneffect");
            this.player.sendMessage(Language.PREFIX + "8 - Remove potioneffect");
            this.player.sendMessage(Language.PREFIX + "9 - List potioneffect");
            this.player.sendMessage(Language.PREFIX + "10 - Save boostitem");
            this.player.sendMessage(Language.PREFIX + ChatColor.GREEN + MENU_BACK);
            this.player.sendMessage(Language.PREFIX + ChatColor.RED + MENU_EXIT);
            this.player.sendMessage("");
            this.player.sendMessage(FOOTER_STANDARD);
            this.player.sendMessage("");
        }
    }

    private static class EditPotionEffect extends SChatpage {
        private final ArenaEntity arenaEntity;
        private final PotionEffectMeta lightPotioneffect;

        EditPotionEffect(Player player, ArenaEntity entity, PotionEffectMeta lightPotioneffect) {
            super(player);
            this.arenaEntity = entity;
            this.lightPotioneffect = lightPotioneffect;
        }

        @Override
        public boolean playerPreChatEnter(String text) {
            if (this.lastNumber == 1) {
                if (PotionEffectBuilder.getPotionEffectFromName(text) == null)
                    this.player.sendMessage(Language.PREFIX + "This type does not exist.");
                else
                    this.lightPotioneffect.setTypeId(PotionEffectBuilder.getPotionEffectFromName(text).getId());
            } else if (this.lastNumber == 2 && tryPInt(text)) {
                this.lightPotioneffect.setSeconds(Integer.parseInt(text));
            } else if (this.lastNumber == 3 && tryPInt(text)) {
                this.lightPotioneffect.setStrength(Integer.parseInt(text));
            } else
                return true;
            return false;
        }

        @Override
        public void onPlayerSelect(int number) {
            if (number == 1) {
                this.player.sendMessage(Language.PREFIX + "Enter the type of the potioneffect:");
                this.player.sendMessage(Language.PREFIX + PotionEffectBuilder.getPotionEffectsText());
            } else if (number == 2) {
                this.player.sendMessage(Language.PREFIX + "Enter the duration (seconds) of the potioneffect:");
            } else if (number == 3) {
                this.player.sendMessage(Language.PREFIX + "Enter the strength of the potioneffect:");
            } else if (number == 4) {
                this.lightPotioneffect.setAmbientVisible(!this.lightPotioneffect.isAmbientVisible());
                if (this.lightPotioneffect.isAmbientVisible()) {
                    this.player.sendMessage(Language.PREFIX + "Enabled ambient visibility.");
                } else {
                    this.player.sendMessage(Language.PREFIX + "Disabled ambient visibility.");
                }
            } else if (number == 5) {
                this.lightPotioneffect.setParticleVisible(!this.lightPotioneffect.isParticleVisible());
                if (this.lightPotioneffect.isParticleVisible()) {
                    this.player.sendMessage(Language.PREFIX + "Enabled particle visibility.");
                } else {
                    this.player.sendMessage(Language.PREFIX + "Disabled particle visibility.");
                }
            } else if (number == 6) {
                this.open(this.player, this.getLastInstance());
            }
        }

        @Override
        public void show() {
            this.player.sendMessage("");
            this.player.sendMessage(HEADER_STANDARD);
            this.player.sendMessage("");
            this.player.sendMessage(Language.PREFIX + "1 - Set type");
            this.player.sendMessage(Language.PREFIX + "2 - Set duration");
            this.player.sendMessage(Language.PREFIX + "3 - Set strength");
            this.player.sendMessage(Language.PREFIX + "4 - Toggle ambient");
            this.player.sendMessage(Language.PREFIX + "5 - Toggle particles");
            this.player.sendMessage(Language.PREFIX + "6 - Save potioneffect");
            this.player.sendMessage(Language.PREFIX + ChatColor.GREEN + MENU_BACK);
            this.player.sendMessage(Language.PREFIX + ChatColor.RED + MENU_EXIT);
            this.player.sendMessage("");
            this.player.sendMessage(FOOTER_STANDARD);
            this.player.sendMessage("");
        }
    }

    private static class EditCommands extends SChatpage {
        private final ArenaEntity arenaEntity;

        EditCommands(Player player, ArenaEntity entity) {
            super(player);
            this.arenaEntity = entity;
        }

        @Override
        public boolean playerPreChatEnter(String text) {
            if (this.lastNumber == 1) {
                this.arenaEntity.getTeamMeta().setWinCommand(text);
            } else if (this.lastNumber == 2) {
                this.arenaEntity.getTeamMeta().setGamendCommand(text);
            } else
                return true;
            return false;
        }

        @Override
        public void onPlayerSelect(int number) {
            if (number == 1) {
                this.player.sendMessage(Language.PREFIX + "Enter the command execute for team who wins a game:");
                this.player.sendMessage(Language.PREFIX + "(If you want that the command get executed per player write add a placeholder ':player')");
            } else if (number == 2) {
                this.player.sendMessage(Language.PREFIX + "Enter the command executed when a game ends:");
                this.player.sendMessage(Language.PREFIX + "(If you want that the command get executed per player write add a placeholder ':player')");
            } else if (number == 3) {
                this.open(this.player, this.getLastInstance());
            }
        }

        @Override
        public void show() {
            this.player.sendMessage("");
            this.player.sendMessage(HEADER_STANDARD);
            this.player.sendMessage("");
            this.player.sendMessage(Language.PREFIX + "1 - Set game-win");
            this.player.sendMessage(Language.PREFIX + "2 - Set game-end");
            this.player.sendMessage(Language.PREFIX + "3 - Save commands");
            this.player.sendMessage(Language.PREFIX + ChatColor.GREEN + MENU_BACK);
            this.player.sendMessage(Language.PREFIX + ChatColor.RED + MENU_EXIT);
            this.player.sendMessage("");
            this.player.sendMessage(FOOTER_STANDARD);
            this.player.sendMessage("");
        }
    }

    private static class EventMetaSettingsPage extends SChatpage {
        private final ArenaEntity arena;

        EventMetaSettingsPage(Player player, ArenaEntity arena) {
            super(player);
            this.arena = arena;
        }

        @Override
        public boolean playerPreChatEnter(String text) {
            if (this.lastNumber == 1) {
                this.arena.getEventMeta().setReferee(text);
                this.player.sendMessage(Language.PREFIX + "Referee: " + this.arena.getEventMeta().getReferee());
            } else if (this.lastNumber == 2 && tryPInt(text)) {
                this.arena.getLobbyMeta().setGameTime(Integer.parseInt(text));
            } else if (this.lastNumber == 3) {
                this.arena.getEventMeta().addRegisteredRedPlayer(text);
                this.printPlayers(this.arena.getEventMeta().getRegisteredRedPlayers());
            } else if (this.lastNumber == 4) {
                this.arena.getEventMeta().removeRegisteredRedPlayer(text);
                this.printPlayers(this.arena.getEventMeta().getRegisteredRedPlayers());
            } else if (this.lastNumber == 6) {
                this.arena.getEventMeta().addRegisteredBluePlayer(text);
                this.printPlayers(this.arena.getEventMeta().getRegisteredBluePlayers());
            } else if (this.lastNumber == 7) {
                this.arena.getEventMeta().removeRegisteredBluePlayer(text);
                this.printPlayers(this.arena.getEventMeta().getRegisteredBluePlayers());
            } else
                return true;
            return false;
        }

        private void printPlayers(String[] data) {
            this.player.sendMessage(Language.PREFIX + "Players:");
            for (final String s : data) {
                this.player.sendMessage(s);
            }
        }

        @Override
        public void onPlayerSelect(int number) {
            if (number == 1) {
                this.player.sendMessage(Language.PREFIX + "Enter the name of the player:");
            } else if (number == 2) {
                this.player.sendMessage(Language.PREFIX + "Enter the matchtime (seconds):");
            } else if (number == 3) {
                this.player.sendMessage(Language.PREFIX + "Enter the name of the red player:");
            } else if (number == 4) {
                this.player.sendMessage(Language.PREFIX + "Enter the name of the red player:");
            } else if (number == 5) {
                this.printPlayers(this.arena.getEventMeta().getRegisteredRedPlayers());
            } else if (number == 6) {
                this.player.sendMessage(Language.PREFIX + "Enter the name of the blue player:");
            } else if (number == 7) {
                this.player.sendMessage(Language.PREFIX + "Enter the name of the blue player:");
            } else if (number == 8) {
                this.printPlayers(this.arena.getEventMeta().getRegisteredBluePlayers());
            } else if (number == 9) {
                this.open(this.player, this.getLastInstance());
            }
        }

        @Override
        public void show() {
            this.player.sendMessage("");
            this.player.sendMessage(HEADER_STANDARD);
            this.player.sendMessage("");
            this.player.sendMessage(Language.PREFIX + "1 - Set referee");
            this.player.sendMessage(Language.PREFIX + "2 - Set matchtime");
            this.player.sendMessage(Language.PREFIX + "3 - Add red player");
            this.player.sendMessage(Language.PREFIX + "4 - Remove red player");
            this.player.sendMessage(Language.PREFIX + "5 - Show red players");
            this.player.sendMessage(Language.PREFIX + "6 - Add blue player");
            this.player.sendMessage(Language.PREFIX + "7 - Remove blue player");
            this.player.sendMessage(Language.PREFIX + "8 - Show blue players");
            this.player.sendMessage(Language.PREFIX + "9 - Save settings");
            this.player.sendMessage(Language.PREFIX + ChatColor.GREEN + MENU_BACK);
            this.player.sendMessage(Language.PREFIX + ChatColor.RED + MENU_EXIT);
            this.player.sendMessage("");
            this.player.sendMessage(FOOTER_STANDARD);
            this.player.sendMessage("");
        }
    }

    private static class HologramSettingsPage extends SChatpage {
        private final ArenaEntity arena;

        HologramSettingsPage(Player player, ArenaEntity arena) {
            super(player);
            this.arena = arena;
        }

        @Override
        public boolean playerPreChatEnter(String text) {
            if (this.lastNumber == 2) {
                this.arena.getTeamMeta().setHologramText(text);
                this.player.sendMessage(Language.PREFIX + "Changed hologramtext.");
            } else
                return true;
            return false;
        }

        @Override
        public void onPlayerSelect(int number) {
            if (number == 1) {
                this.arena.getTeamMeta().setHologramEnabled(!this.arena.getTeamMeta().isHologramEnabled());
                if (this.arena.getTeamMeta().isHologramEnabled())
                    this.player.sendMessage(Language.PREFIX + "Enabled the hologram.");
                else
                    this.player.sendMessage(Language.PREFIX + "Disabled the hologram.");
            } else if (number == 2) {
                this.player.sendMessage(Language.PREFIX + "Enter the displaying text of the hologram: (Sample: '[ :redcolor:redscore : :bluecolor:bluescore ]')");
            } else if (number == 3) {
                this.arena.getTeamMeta().setHologramLocation(this.player.getLocation());
                this.player.sendMessage(Language.PREFIX + "Set the location of the hologram.");
            } else if (number == 4) {
                this.open(this.player, this.getLastInstance());
            }
        }

        @Override
        public void show() {
            this.player.sendMessage("");
            this.player.sendMessage(HEADER_STANDARD);
            this.player.sendMessage("");
            this.player.sendMessage(Language.PREFIX + "1 - Toggle enabled");
            this.player.sendMessage(Language.PREFIX + "2 - Set text");
            this.player.sendMessage(Language.PREFIX + "3 - Set position");
            this.player.sendMessage(Language.PREFIX + "4 - Save settings");
            this.player.sendMessage(Language.PREFIX + ChatColor.GREEN + MENU_BACK);
            this.player.sendMessage(Language.PREFIX + ChatColor.RED + MENU_EXIT);
            this.player.sendMessage("");
            this.player.sendMessage(FOOTER_STANDARD);
            this.player.sendMessage("");
        }
    }

    private static class GlowingSettingsPage extends SChatpage {
        private final ArenaEntity arena;

        GlowingSettingsPage(Player player, ArenaEntity arena) {
            super(player);
            this.arena = arena;
        }

        @Override
        public boolean playerPreChatEnter(String text) {
            if (this.lastNumber == 2 && tryPInt(text)) {
                this.arena.getTeamMeta().setGoalShooterGlowingSeconds(Integer.parseInt(text));
                this.player.sendMessage(Language.PREFIX + "Changed amount of glowing seconds.");
            } else
                return true;
            return false;
        }

        @Override
        public void onPlayerSelect(int number) {
            if (number == 1) {
                this.arena.getTeamMeta().setGoalShooterGlowing(!this.arena.getTeamMeta().isGoalShooterGlowing());
                if (this.arena.getTeamMeta().isGoalShooterGlowing())
                    this.player.sendMessage(Language.PREFIX + "Enabled glowing.");
                else
                    this.player.sendMessage(Language.PREFIX + "Disabled glowing.");
            } else if (number == 2) {
                this.player.sendMessage(Language.PREFIX + "Enter the amount of seconds a player should be glowing:");
            } else if (number == 3) {
                this.open(this.player, this.getLastInstance());
            }
        }

        @Override
        public void show() {
            this.player.sendMessage("");
            this.player.sendMessage(HEADER_STANDARD);
            this.player.sendMessage("");
            this.player.sendMessage(Language.PREFIX + "1 - Toggle glowing");
            this.player.sendMessage(Language.PREFIX + "2 - Set glowing seconds");
            this.player.sendMessage(Language.PREFIX + "3 - Save settings");
            this.player.sendMessage(Language.PREFIX + ChatColor.GREEN + MENU_BACK);
            this.player.sendMessage(Language.PREFIX + ChatColor.RED + MENU_EXIT);
            this.player.sendMessage("");
            this.player.sendMessage(FOOTER_STANDARD);
            this.player.sendMessage("");
        }
    }

    public class BungeeMinigameSettings extends SChatpage {
        private final ArenaEntity arenaEntity;

        BungeeMinigameSettings(Player player, ArenaEntity arenaEntity) {
            super(player);
            this.arenaEntity = arenaEntity;
        }

        @Override
        public boolean playerPreChatEnter(String text) {
            if (this.lastNumber == 1 && tryPInt(text) && Integer.parseInt(text) > 0)
                this.arenaEntity.getLobbyMeta().setGameTime(Integer.parseInt(text));
            else if (this.lastNumber == 2 && tryPInt(text) && Integer.parseInt(text) >= 0)
                this.arenaEntity.getLobbyMeta().setCountDown(Integer.parseInt(text));
            else if (this.lastNumber == 7)
                this.arenaEntity.getLobbyMeta().setGameTitleMessage(text);
            else if (this.lastNumber == 8)
                this.arenaEntity.getLobbyMeta().setGameSubTitleMessage(text);
            else
                return true;
            return false;
        }

        @Override
        public void hitBlockEvent(Block block) {
            if (this.lastNumber == 4 && (block.getType() == Material.SIGN_POST || block.getType() == Material.WALL_SIGN)) {
                final Sign sign = (Sign) block.getState();
                sign.setLine(0, Config.getInstance().getTeamSign().getLine1(this.arenaEntity, Team.RED));
                sign.setLine(1, Config.getInstance().getTeamSign().getLine2(this.arenaEntity, Team.RED));
                sign.setLine(2, Config.getInstance().getTeamSign().getLine3(this.arenaEntity, Team.RED));
                sign.setLine(3, Config.getInstance().getTeamSign().getLine4(this.arenaEntity, Team.RED));
                sign.update();
                this.lastNumber = -1;
                this.arenaEntity.getLobbyMeta().addRedTeamSignLocation(block.getLocation());
                this.player.sendMessage(Language.PREFIX + "Set sign.");
            }
            if (this.lastNumber == 5 && (block.getType() == Material.SIGN_POST || block.getType() == Material.WALL_SIGN)) {
                final Sign sign = (Sign) block.getState();
                sign.setLine(0, Config.getInstance().getTeamSign().getLine1(this.arenaEntity, Team.BLUE));
                sign.setLine(1, Config.getInstance().getTeamSign().getLine2(this.arenaEntity, Team.BLUE));
                sign.setLine(2, Config.getInstance().getTeamSign().getLine3(this.arenaEntity, Team.BLUE));
                sign.setLine(3, Config.getInstance().getTeamSign().getLine4(this.arenaEntity, Team.BLUE));
                sign.update();
                this.lastNumber = -1;
                this.arenaEntity.getLobbyMeta().addBlueTeamSignLocation(block.getLocation());
                this.player.sendMessage(Language.PREFIX + "Set sign.");
            }
            if (this.lastNumber == 6 && (block.getType() == Material.SIGN_POST || block.getType() == Material.WALL_SIGN)) {
                final Sign sign = (Sign) block.getState();
                sign.setLine(0, Config.getInstance().getLeaveSign().getLine1(this.arenaEntity, null));
                sign.setLine(1, Config.getInstance().getLeaveSign().getLine2(this.arenaEntity, null));
                sign.setLine(2, Config.getInstance().getLeaveSign().getLine3(this.arenaEntity, null));
                sign.setLine(3, Config.getInstance().getLeaveSign().getLine4(this.arenaEntity, null));
                sign.update();
                this.lastNumber = -1;
                this.arenaEntity.getLobbyMeta().addLeaveignLocation(block.getLocation());
                this.player.sendMessage(Language.PREFIX + "Set sign.");
            }
        }

        @Override
        public void onPlayerSelect(int number) {
            if (number == 1)
                this.player.sendMessage(Language.PREFIX + "Enter the duration of the match:");
            else if (number == 2)
                this.player.sendMessage(Language.PREFIX + "Enter the duration of the lobby:");
            else if (number == 3) {
                this.arenaEntity.getLobbyMeta().setLobbySpawnpoint(this.player.getLocation());
                this.player.sendMessage(Language.PREFIX + "Set the lobby spawnpoint at your location.");
            } else if (number == 4) {
                this.player.sendMessage(Language.PREFIX + "Rightclick a sign to convert it:");
            } else if (number == 5) {
                this.player.sendMessage(Language.PREFIX + "Rightclick a sign to convert it:");
            } else if (number == 6) {
                this.player.sendMessage(Language.PREFIX + "Rightclick a sign to convert it:");
            } else if (number == 7) {
                this.player.sendMessage(Language.PREFIX + "Enter the title when the lobby countdown changes:");
                this.player.sendMessage(Language.PREFIX + "Example: 'Game'");
            } else if (number == 8) {
                this.player.sendMessage(Language.PREFIX + "Enter the subtitle when the lobby countdown changes:");
                this.player.sendMessage(Language.PREFIX + "Example: 'Starting in :countdown seconds.'");
            } else if (number == 9) {
                this.arenaEntity.getTeamMeta().setMaxScore(100);
                this.arenaEntity.getTeamMeta().setTeamAutoJoin(false);
                this.arenaEntity.getTeamMeta().setFastJoin(false);
                this.arenaEntity.getTeamMeta().setEmptyReset(false);
                this.arenaEntity.getLobbyMeta().setGameSubTitleMessage(ChatColor.YELLOW + "Starting in :countdown seconds.");
                this.arenaEntity.getLobbyMeta().setGameTitleMessage(ChatColor.GOLD + "Game");
                this.player.sendMessage(Language.PREFIX + "Reset this page.");
            } else if (number == 10) {
                this.player.sendMessage(Language.PREFIX + "Saved settings.");
                this.open(this.player, new ArenaCommandExecutor.MainSettingsPage(this.player, this.arenaEntity));
            }
        }

        @Override
        public void show() {
            this.player.sendMessage("");
            this.player.sendMessage(HEADER_STANDARD);
            this.player.sendMessage("");
            this.player.sendMessage(Language.PREFIX + "1 - Set match time");
            this.player.sendMessage(Language.PREFIX + "2 - Set lobby time");
            this.player.sendMessage(Language.PREFIX + "3 - Set lobby spawnpoint");
            this.player.sendMessage(Language.PREFIX + "4 - Set redteam sign");
            this.player.sendMessage(Language.PREFIX + "5 - Set blueteam sign");
            this.player.sendMessage(Language.PREFIX + "6 - Set leaving sign");
            this.player.sendMessage(Language.PREFIX + "7 - Set game countdowntitle");
            this.player.sendMessage(Language.PREFIX + "8 - Set game countdownsubtitle");
            this.player.sendMessage(Language.PREFIX + "9 - Reset this page");
            this.player.sendMessage(Language.PREFIX + "10 - Save settings");
            this.player.sendMessage(Language.PREFIX + ChatColor.GREEN + MENU_BACK);
            this.player.sendMessage(Language.PREFIX + ChatColor.RED + MENU_EXIT);
            this.player.sendMessage("");
            this.player.sendMessage(FOOTER_STANDARD);
            this.player.sendMessage("");
        }
    }
}