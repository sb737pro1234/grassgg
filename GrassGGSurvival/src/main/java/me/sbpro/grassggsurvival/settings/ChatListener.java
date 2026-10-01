package me.sbpro.grassggsurvival.settings;

import me.sbpro.grassggsurvival.GrassGGSurvival;
import io.papermc.paper.event.player.AsyncChatEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class ChatListener implements Listener {

    private final GrassGGSurvival plugin;

    public ChatListener(GrassGGSurvival plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onChat(AsyncChatEvent event) {

        event.viewers().removeIf(audience -> {
            if (!(audience instanceof Player player)) {
                return false;
            }

            return plugin.getChatToggleManager().isChatDisabled(player.getUniqueId());
        });
    }
}