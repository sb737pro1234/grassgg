package me.sbpro.grassggchat.chatcolor;

import io.papermc.paper.dialog.Dialog;
import io.papermc.paper.registry.data.dialog.ActionButton;
import io.papermc.paper.registry.data.dialog.DialogBase;
import io.papermc.paper.registry.data.dialog.action.DialogAction;
import io.papermc.paper.registry.data.dialog.type.DialogType;
import me.sbpro.grassggchat.GrassGGChat;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickCallback;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class ChatColorMenu {

    public static void open(
            Player player,
            ChatColorManager manager
    ) {
        player.showDialog(
                createDialog(player, manager)
        );
    }

    private static Dialog createDialog(
            Player player,
            ChatColorManager manager
    ) {

        List<ActionButton> buttons = new ArrayList<>();

        PlayerChatColor current =
                manager.getColor(
                        player.getUniqueId()
                );

        /*
         * =========================
         * CHAT COLOURS
         * =========================
         */

        for (PlayerChatColor color :
                PlayerChatColor.values()) {

            boolean hasPermission =
                    player.hasPermission(
                            color.getPermission()
                    );

            boolean selected =
                    current == color;

            Component name;

            /*
             * Players can see every colour.
             *
             * Colours they have permission for
             * are shown normally.
             *
             * Colours they don't have permission
             * for are shown grey.
             */
            if (hasPermission) {

                name = Component.text(
                        color.getDisplay(),
                        color.getColor()
                );

                /*
                 * Only show the tick if the player
                 * currently has this colour.
                 */
                if (selected) {

                    name = name.append(
                            Component.text(
                                    " ✓",
                                    TextColor.color(0x55FF55)
                            )
                    );
                }

            } else {

                name = Component.text(
                        color.getDisplay(),
                        TextColor.color(0x777777)
                );
            }

            ActionButton button =
                    ActionButton.create(

                            name,

                            hasPermission
                                    ? Component.text(
                                    "Select " +
                                            color.getDisplay()
                            )
                                    : Component.text(
                                    "You don't have permission."
                            ),

                            250,

                            DialogAction.customClick(

                                    (response, audience) -> {

                                        if (!(audience
                                                instanceof Player clickedPlayer)) {
                                            return;
                                        }

                                        /*
                                         * Check the permission again
                                         * when the player clicks.
                                         */
                                        if (!clickedPlayer
                                                .hasPermission(
                                                        color.getPermission()
                                                )) {

                                            /*
                                             * Do NOT change the player's
                                             * current colour.
                                             */
                                            clickedPlayer.sendMessage(
                                                    GrassGGChat.PREFIX +
                                                            "You don't have permission to use this chat color."
                                            );

                                            clickedPlayer.playSound(
                                                    clickedPlayer
                                                            .getLocation(),
                                                    Sound.ENTITY_VILLAGER_NO,
                                                    1,
                                                    1
                                            );

                                            /*
                                             * Reopen the menu.
                                             *
                                             * Because we haven't changed
                                             * their colour, their original
                                             * colour remains selected.
                                             */
                                            open(
                                                    clickedPlayer,
                                                    manager
                                            );

                                            return;
                                        }

                                        /*
                                         * Player has permission.
                                         *
                                         * Change their colour.
                                         */
                                        manager.setColor(
                                                clickedPlayer
                                                        .getUniqueId(),
                                                color
                                        );

                                        /*
                                         * Build the success message
                                         * as an Adventure Component so
                                         * the colour name can actually
                                         * have the selected colour.
                                         */
                                        Component message =
                                                Component.text(
                                                                GrassGGChat.PREFIX
                                                        )
                                                        .append(
                                                                Component.text(
                                                                        "Your chat color has been updated to "
                                                                )
                                                        )
                                                        .append(
                                                                Component.text(
                                                                        color.getDisplay(),
                                                                        color.getColor()
                                                                )
                                                        )
                                                        .append(
                                                                Component.text(
                                                                        "."
                                                                )
                                                        );

                                        clickedPlayer.sendMessage(
                                                message
                                        );

                                        clickedPlayer.playSound(
                                                clickedPlayer
                                                        .getLocation(),
                                                Sound.ENTITY_PLAYER_LEVELUP,
                                                1,
                                                1
                                        );

                                        /*
                                         * Reopen the menu with the
                                         * newly selected colour.
                                         */
                                        open(
                                                clickedPlayer,
                                                manager
                                        );
                                    },

                                    ClickCallback.Options.builder()
                                            .uses(
                                                    ClickCallback
                                                            .UNLIMITED_USES
                                            )
                                            .build()
                            )
                    );

            buttons.add(button);
        }

        /*
         * =========================
         * BOLD
         * =========================
         */

        boolean bold =
                manager.isBold(
                        player.getUniqueId()
                );

        boolean canUseBold =
                player.hasPermission(
                        "grassgg.chatcolor.bold"
                );

        Component boldName =
                Component.text(
                        "Bold",
                        TextColor.color(0xFFAA00)
                );

        /*
         * Only show the tick if the player
         * actually has permission to use Bold.
         */
        if (canUseBold && bold) {

            boldName = boldName.append(
                    Component.text(
                            " ✓",
                            TextColor.color(0x55FF55)
                    )
            );
        }

        /*
         * Grey out Bold if the player doesn't
         * have the permission.
         */
        if (!canUseBold) {

            boldName =
                    Component.text(
                            "Bold",
                            TextColor.color(0x777777)
                    );
        }

        ActionButton boldButton =
                ActionButton.create(

                        boldName,

                        canUseBold
                                ? Component.text(
                                "Toggle bold chat"
                        )
                                : Component.text(
                                "You don't have permission."
                        ),

                        250,

                        DialogAction.customClick(

                                (response, audience) -> {

                                    if (!(audience
                                            instanceof Player clickedPlayer)) {
                                        return;
                                    }

                                    /*
                                     * Check the permission again
                                     * when clicked.
                                     */
                                    if (!clickedPlayer
                                            .hasPermission(
                                                    "grassgg.chatcolor.bold"
                                            )) {

                                        clickedPlayer.sendMessage(
                                                GrassGGChat.PREFIX +
                                                        "You don't have permission to use bold."
                                        );

                                        clickedPlayer.playSound(
                                                clickedPlayer
                                                        .getLocation(),
                                                Sound.ENTITY_VILLAGER_NO,
                                                1,
                                                1
                                        );

                                        open(
                                                clickedPlayer,
                                                manager
                                        );

                                        return;
                                    }

                                    /*
                                     * Toggle Bold.
                                     */
                                    boolean newValue =
                                            !manager.isBold(
                                                    clickedPlayer
                                                            .getUniqueId()
                                            );

                                    manager.setBold(
                                            clickedPlayer
                                                    .getUniqueId(),
                                            newValue
                                    );

                                    clickedPlayer.sendMessage(
                                            GrassGGChat.PREFIX +
                                                    "Bold has been " +
                                                    (
                                                            newValue
                                                                    ? "enabled."
                                                                    : "disabled."
                                                    )
                                    );

                                    clickedPlayer.playSound(
                                            clickedPlayer
                                                    .getLocation(),
                                            Sound.ENTITY_EXPERIENCE_ORB_PICKUP,
                                            1,
                                            1
                                    );

                                    /*
                                     * Reopen the menu with the
                                     * updated Bold state.
                                     */
                                    open(
                                            clickedPlayer,
                                            manager
                                    );
                                },

                                ClickCallback.Options.builder()
                                        .uses(
                                                ClickCallback
                                                        .UNLIMITED_USES
                                        )
                                        .build()
                        )
                );

        buttons.add(boldButton);

        /*
         * =========================
         * DIALOG
         * =========================
         */

        return Dialog.create(builder -> builder
                .empty()
                .base(
                        DialogBase.builder(
                                        Component.text(
                                                "Chat Color"
                                        )
                                )
                                .canCloseWithEscape(true)
                                .build()
                )
                .type(
                        DialogType.multiAction(
                                buttons,
                                null,
                                2
                        )
                )
        );
    }
}