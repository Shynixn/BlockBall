package com.github.shynixn.blockball.business.bukkit.dependencies.placeholderapi;

import com.github.shynixn.blockball.PlaceHolderType;
import com.github.shynixn.blockball.api.events.PlaceHolderRequestEvent;
import me.clip.placeholderapi.external.EZPlaceholderHook;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public class PlaceHolderApiConnection {


    public static void init(JavaPlugin plugin) {
        final PlaceHolderScoreHooker hooker = new PlaceHolderScoreHooker(plugin);
        hooker.hook();
    }

    private static class PlaceHolderScoreHooker extends EZPlaceholderHook {

        PlaceHolderScoreHooker(Plugin plugin) {
            super(plugin, "blockball");
        }

        @Override
        public String onPlaceholderRequest(Player player, String s) {
            if (player == null)
                return "";
            try {
                if (PlaceHolderType.getTypeFromName(s) != null) {
                    final PlaceHolderRequestEvent event;
                    if (s.split("_")[0].equals("player")) {
                        event = new PlaceHolderRequestEvent(player, PlaceHolderType.getTypeFromName(s), -1);
                    } else {
                        event = new PlaceHolderRequestEvent(player, PlaceHolderType.getTypeFromName(s), Integer.parseInt(s.split("_")[0]));
                    }
                    Bukkit.getPluginManager().callEvent(event);
                    return ChatColor.translateAlternateColorCodes('&', event.getResult());
                }

            } catch (final Exception ignored) {
            }
            return "";
        }
    }
}
