package com.github.shynixn.blockball.bukkit.logic.business.commandexecutor.menu;

import com.github.shynixn.blockball.bukkit.logic.business.helper.ChatBuilder;
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
public enum ClickableComponent {
    WORLDEDIT(" [worldedit..]", ChatColor.GOLD),
    EDIT(" [edit..]", ChatColor.GREEN),
    COPY_ARMOR(" [copy armor..]", ChatColor.GOLD),
    PAGE(" [page..]", ChatColor.YELLOW),
    PREVIEW(" [preview..]", ChatColor.GRAY),
    ADD(" [add..]", ChatColor.BLUE),
    DELETE(" [delete..]", ChatColor.DARK_RED),
    SELECT(" [select..]", ChatColor.AQUA),
    LOCATION(" [location..]", ChatColor.BLUE),

    INVALID(" [page..]", ChatColor.BLACK),
    TOGGLE(" [toggle..]", ChatColor.LIGHT_PURPLE)
    ;
    private String text;
    private ChatColor color;

    ClickableComponent(String text, ChatColor color) {
      this.text = text;
      this.color = color;
    }

    public String getText() {
        return this.text;
    }

    public ChatColor getColor() {
        return this.color;
    }

    public ChatBuilder.Component addComponent(ChatBuilder builder)  {
        return builder.component(this.text).setColor(color);
    }
}
