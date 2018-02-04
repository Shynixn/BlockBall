package com.github.shynixn.blockball.bukkit.logic.business.entity.bungeecord;

import com.github.shynixn.blockball.api.business.entity.BungeeCordServerStatus;
import com.github.shynixn.blockball.api.business.enumeration.BungeeCordServerState;
import org.bukkit.ChatColor;

/**
 * Created by Shynixn 2017.
 * <p>
 * Version 1.1
 * <p>
 * MIT License
 * <p>
 * Copyright (c) 2017 by Shynixn
 * <p>
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * <p>
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * <p>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
public class BungeeCordServerStats implements BungeeCordServerStatus {

    private final int playerAmount;
    private final int maxPlayerAmount;
    private final BungeeCordServerState state;
    private final String serverName;

    /**
     * Initializes a new bungeecord server state with the given parameters.
     *
     * @param serverName      serverName
     * @param playerAmount    playerAmount
     * @param maxPlayerAmount maxPlayerAmount
     */
    public BungeeCordServerStats(String serverName, int playerAmount, int maxPlayerAmount, BungeeCordServerState state) {
        super();
        this.playerAmount = playerAmount;
        this.maxPlayerAmount = maxPlayerAmount;
        this.state = state;
        this.serverName = serverName;
    }

    /**
     * Returns the amount of players on the server.
     *
     * @return amount
     */
    @Override
    public int getPlayerAmount() {
        return this.playerAmount;
    }


    /**
     * Returns the status of the bungeecord server.
     *
     * @return status
     */
    @Override
    public BungeeCordServerState getStatus() {
        return this.state;
    }

    /**
     * Returns the name of the server.
     *
     * @return name
     */
    @Override
    public String getServerName() {
        return this.serverName;
    }

    /**
     * Parses the bungeeCord server stats from the given modt
     *
     * @param serverName serverName
     * @param data       data
     * @return stats
     */
    public static BungeeCordServerStats from(String serverName, String data) {
        try {
            BungeeCordServerState state = BungeeCordServerState.UNKNOWN;
            String motd;
            final StringBuilder motdBuilder = new StringBuilder();
            boolean started = false;
            int i;
            for (i = 0; i < data.length(); i++) {
                if (data.charAt(i) == '[') {
                    started = true;
                } else if (data.charAt(i) == ']') {
                    break;
                } else if (started) {
                    motdBuilder.append(data.charAt(i));
                }
            }
            try {
                motd = ChatColor.translateAlternateColorCodes('&', motdBuilder.toString()).substring(0, motdBuilder.length() - 2);
              /* if (motd.equalsIgnoreCase(BungeeCord.MOD_INGAME))
                    state = BungeeCordServerState.INGAME;
                else if (motd.equalsIgnoreCase(BungeeCord.MOD_RESTARTING))
                    state = BungeeCordServerState.RESTARTING;
                else if (motd.equalsIgnoreCase(BungeeCord.MOD_WAITING_FOR_PLAYERS))
                    state = BungeeCordServerState.WAITING_FOR_PLAYERS;*/
            } catch (final Exception ex) {
                state = BungeeCordServerState.RESTARTING;
            }
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
            final int playerAmount = Integer.parseInt(motd);
            motd = "";
            started = false;
            for (; i < data.length(); i++) {
                if (data.charAt(i) == '§')
                    started = true;
                else if (started)
                    motd += data.charAt(i);
            }
            final int maxPlayerAmount = Integer.parseInt(motd);
            return new BungeeCordServerStats(serverName, playerAmount, maxPlayerAmount, state);
        } catch (final Exception ex) {
            return new BungeeCordServerStats(serverName, 0, 0, BungeeCordServerState.RESTARTING);
        }
    }

    @Override
    public int getPlayerMaxAmount() {
        return this.maxPlayerAmount;
    }
}
