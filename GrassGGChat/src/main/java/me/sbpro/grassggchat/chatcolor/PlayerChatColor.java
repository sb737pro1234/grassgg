package me.sbpro.grassggchat.chatcolor;

import net.kyori.adventure.text.format.TextColor;

public enum PlayerChatColor {

    WHITE(
            "White",
            "grassgg.chatcolor.white",
            TextColor.color(0xFFFFFF)
    ),

    GRAY(
            "Gray",
            "grassgg.chatcolor.gray",
            TextColor.color(0xAAAAAA)
    ),

    BLACK(
            "Black",
            "grassgg.chatcolor.black",
            TextColor.color(0x000000)
    ),

    RED(
            "Red",
            "grassgg.chatcolor.red",
            TextColor.color(0xFF5555)
    ),

    ORANGE(
            "Orange",
            "grassgg.chatcolor.orange",
            TextColor.color(0xFFAA00)
    ),

    YELLOW(
            "Yellow",
            "grassgg.chatcolor.yellow",
            TextColor.color(0xFFFF55)
    ),

    LIME(
            "Lime",
            "grassgg.chatcolor.lime",
            TextColor.color(0x55FF55)
    ),

    GREEN(
            "Green",
            "grassgg.chatcolor.green",
            TextColor.color(0x00AA00)
    ),

    AQUA(
            "Aqua",
            "grassgg.chatcolor.aqua",
            TextColor.color(0x55FFFF)
    ),

    BLUE(
            "Blue",
            "grassgg.chatcolor.blue",
            TextColor.color(0x5555FF)
    ),

    PURPLE(
            "Purple",
            "grassgg.chatcolor.purple",
            TextColor.color(0xAA00AA)
    ),

    PINK(
            "Pink",
            "grassgg.chatcolor.pink",
            TextColor.color(0xFF55FF)
    ),

    DARK_GRAY(
            "Dark Gray",
            "grassgg.chatcolor.dark_gray",
            TextColor.color(0x555555)
    ),

    DARK_AQUA(
            "Dark Aqua",
            "grassgg.chatcolor.dark_aqua",
            TextColor.color(0x00AAAA)
    );

    private final String display;
    private final String permission;
    private final TextColor color;

    PlayerChatColor(
            String display,
            String permission,
            TextColor color
    ) {
        this.display = display;
        this.permission = permission;
        this.color = color;
    }

    public String getDisplay() {
        return display;
    }

    public String getPermission() {
        return permission;
    }

    public TextColor getColor() {
        return color;
    }
}