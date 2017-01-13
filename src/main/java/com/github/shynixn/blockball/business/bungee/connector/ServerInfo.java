package com.github.shynixn.blockball.business.bungee.connector;

import com.github.shynixn.blockball.business.bungee.game.BungeeCord;
import org.bukkit.ChatColor;

/**
 * Created by Shynixn
 */
interface ServerInfo {
    int getPlayerAmount();

    int getMaxPlayerAmount();

    State getState();

    String getServerName();

    enum State {
        RESTARTING,
        WAITING_FOR_PLAYERS,
        INGAME,
        UNKNOWN;

        public static State getStateFromName(String name) {
            try {
                name = ChatColor.translateAlternateColorCodes('&', name).substring(0, name.length() - 2);
                if (name.equalsIgnoreCase(BungeeCord.MOD_INGAME))
                    return State.INGAME;
                else if (name.equalsIgnoreCase(BungeeCord.MOD_RESTARTING))
                    return State.RESTARTING;
                else if (name.equalsIgnoreCase(BungeeCord.MOD_WAITING_FOR_PLAYERS))
                    return State.WAITING_FOR_PLAYERS;
            } catch (Exception ex) {
                return RESTARTING;
            }
            return State.UNKNOWN;
        }
    }

    class Container implements ServerInfo {
        private int playerAmount = 0;
        private int maxPlayerAmount = 0;
        private State state;
        private String serverName;

        Container(String serverName, int playerAmount, int maxPlayerAmount) {
            this.playerAmount = playerAmount;
            this.maxPlayerAmount = maxPlayerAmount;
            this.state = State.UNKNOWN;
            this.serverName = serverName;
        }

        Container(String serverName, String data) throws Exception {
            this.serverName = serverName;
            try {
                String motd = "";
                boolean started = false;
                int i;
                for (i = 0; i < data.length(); i++) {
                    if (data.charAt(i) == '[')
                        started = true;
                    else if (data.charAt(i) == ']')
                        break;
                    else if (started)
                        motd += data.charAt(i);
                }
                state = State.getStateFromName(motd);
                motd = "";
                started = false;
                for (; i < data.length(); i++) {
                    if (data.charAt(i) == '§') {
                        if (started) {
                            break;
                        } else {
                            started = true;
                        }
                    } else if (started) {
                        motd += data.charAt(i);
                    }
                }
                playerAmount = Integer.parseInt(motd);
                motd = "";
                started = false;
                for (; i < data.length(); i++) {
                    if (data.charAt(i) == '§')
                        started = true;
                    else if (data.charAt(i) == '§')
                        break;
                    else if (started)
                        motd += data.charAt(i);
                }
                maxPlayerAmount = Integer.parseInt(motd);
            } catch (Exception ex) {
                state = State.RESTARTING;
                playerAmount = 0;
                maxPlayerAmount = 0;
            }
        }

        @Override
        public int getPlayerAmount() {
            return playerAmount;
        }

        @Override
        public int getMaxPlayerAmount() {
            return maxPlayerAmount;
        }

        @Override
        public State getState() {
            return state;
        }

        @Override
        public String getServerName() {
            return serverName;
        }
    }
}
