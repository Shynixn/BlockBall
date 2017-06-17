package com.github.shynixn.blockball.lib;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;

public abstract class SChatpage {
    protected Player player;
    protected int lastNumber = -1;
    private SChatpage lastInstance;

    protected SChatpage(Player player) {
        super();
        this.player = player;
    }

    protected void setLastInstance(SChatpage page) {
        this.lastInstance = page;
    }

    protected SChatpage getLastInstance() {
        return this.lastInstance;
    }

    public boolean playerPreChatEnter(String text) {
        return true;
    }

    public final void setLastNumber(int number) {
        this.lastNumber = number;
    }

    public void hitBlockEvent(Block block) {
    }

    public abstract void onPlayerSelect(int number);

    public abstract void show();

    protected void open(Player player, SChatpage page) {
        SChatMenuManager.getInstance().openPage(this, player, page);
    }
}
