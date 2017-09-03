package com.github.shynixn.blockball.api.events;

import com.github.shynixn.blockball.api.entities.PlaceHolderType;
import com.github.shynixn.blockball.lib.SEvent;
import org.bukkit.entity.Player;

public class PlaceHolderRequestEvent extends SEvent {
    private String result;
    private final Player player;
    private final PlaceHolderType type;
    private final int game;

    public PlaceHolderRequestEvent(Player player,PlaceHolderType type, int game) {
        super();
        this.player = player;
        this.game = game;
        this.type = type;
    }

    public Player getPlayer() {
        return this.player;
    }

    public int getGame() {
        return this.game;
    }

    public PlaceHolderType getType() {
        return this.type;
    }

    public String getResult() {
        return this.result;
    }

    public void setResult(String result) {
        this.result = result;
    }
}
