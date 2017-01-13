package com.github.shynixn.blockball.api.events;

import com.github.shynixn.blockball.lib.SEvent;
import com.github.shynixn.blockball.api.entities.PlaceHolderType;
import org.bukkit.entity.Player;

/**
 * Created by Shynixn
 */
public class PlaceHolderRequestEvent extends SEvent {
    private String result;
    private Player player;
    private PlaceHolderType type;
    private int game;

    public PlaceHolderRequestEvent(Player player,PlaceHolderType type, int game) {
        this.player = player;
        this.game = game;
        this.type = type;
    }

    public Player getPlayer() {
        return player;
    }

    public int getGame() {
        return game;
    }

    public PlaceHolderType getType() {
        return type;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }
}
