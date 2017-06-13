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
            } catch (final Exception ex) {
                return RESTARTING;
            }
            return State.UNKNOWN;
        }
    }

    class Container implements ServerInfo {
        private int playerAmount;
        private int maxPlayerAmount;
        private State state;
        private final String serverName;

        Container(String serverName, int playerAmount, int maxPlayerAmount) {
            super();
            this.playerAmount = playerAmount;
            this.maxPlayerAmount = maxPlayerAmount;
            this.state = State.UNKNOWN;
            this.serverName = serverName;
        }

        Container(String serverName, String data) throws Exception {
            super();
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
                this.state = State.getStateFromName(motd);
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
                this.playerAmount = Integer.parseInt(motd);
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
                this.maxPlayerAmount = Integer.parseInt(motd);
            } catch (final Exception ex) {
                this.state = State.RESTARTING;
                this.playerAmount = 0;
                this.maxPlayerAmount = 0;
            }
        }

        @Override
        public int getPlayerAmount() {
            return this.playerAmount;
        }

        @Override
        public int getMaxPlayerAmount() {
            return this.maxPlayerAmount;
        }

        @Override
        public State getState() {
            return this.state;
        }

        @Override
        public String getServerName() {
            return this.serverName;
        }
    }
}
