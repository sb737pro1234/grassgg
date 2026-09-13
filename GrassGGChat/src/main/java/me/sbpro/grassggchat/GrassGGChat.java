package me.sbpro.grassggchat;

import me.sbpro.grassggchat.chatcolor.ChatColorCommand;
import me.sbpro.grassggchat.chatcolor.ChatColorListener;
import me.sbpro.grassggchat.chatcolor.ChatColorManager;
import org.bukkit.plugin.java.JavaPlugin;

public class GrassGGChat extends JavaPlugin {

    public static final String PREFIX =
            "§x§E§0§5§6§F§D§lCHATCOLOR §8»§f ";

    private ChatColorManager chatColorManager;

    @Override
    public void onEnable() {

        chatColorManager =
                new ChatColorManager(this);

        getCommand("chatcolor").setExecutor(
                new ChatColorCommand(
                        chatColorManager
                )
        );

        getServer()
                .getPluginManager()
                .registerEvents(
                        new ChatColorListener(
                                chatColorManager
                        ),
                        this
                );

        getLogger().info(
                "GrassGGChat enabled!"
        );
    }

    public ChatColorManager getChatColorManager() {
        return chatColorManager;
    }
}