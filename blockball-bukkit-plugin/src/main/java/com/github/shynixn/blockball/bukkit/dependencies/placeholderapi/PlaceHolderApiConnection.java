package com.github.shynixn.blockball.bukkit.dependencies.placeholderapi;

import com.github.shynixn.blockball.api.bukkit.business.event.PlaceHolderRequestEvent;
import me.clip.placeholderapi.external.EZPlaceholderHook;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

/**
 * Created by Shynixn 2018.
 * <p>
 * Version 1.2
 * <p>
 * MIT License
 * <p>
 * Copyright (c) 2018 by Shynixn
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
public final class PlaceHolderApiConnection {

    private static PlaceHolderScoreHooker hooker;

    /**
     * Initialize
     */
    private PlaceHolderApiConnection() {
        super();
    }

    /**
     * Initializes a new api hook
     *
     * @param plugin plugin
     */
    public static void initializeHook(Plugin plugin) {
        if (hooker == null) {
            hooker = new PlaceHolderScoreHooker(plugin);
            hooker.hook();
        }
    }

    /**
     * Custom Score Hooker
     */
    private static class PlaceHolderScoreHooker extends EZPlaceholderHook {

        /**
         * Initializes a new score Hooker
         *
         * @param plugin plugin
         */
        PlaceHolderScoreHooker(Plugin plugin) {
            super(plugin, "blockball");
        }

        /**
         * OnPlaceHolder Request
         *
         * @param player player
         * @param s      customText
         * @return result
         */
        @Override
        public String onPlaceholderRequest(Player player, String s) {
            if (player == null)
                return "";
            try {
                final PlaceHolderRequestEvent event = new PlaceHolderRequestEvent(player, s);
                Bukkit.getPluginManager().callEvent(event);
                return event.getResult();
            } catch (final Exception ignored) {
            }
            return null;
        }
    }
}
