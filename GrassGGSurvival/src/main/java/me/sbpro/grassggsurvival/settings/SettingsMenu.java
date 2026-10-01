package me.sbpro.grassggsurvival.settings;

import io.papermc.paper.dialog.Dialog;
import io.papermc.paper.registry.data.dialog.ActionButton;
import io.papermc.paper.registry.data.dialog.DialogBase;
import io.papermc.paper.registry.data.dialog.action.DialogAction;
import io.papermc.paper.registry.data.dialog.body.DialogBody;
import io.papermc.paper.registry.data.dialog.type.DialogType;
import me.sbpro.grassggsurvival.GrassGGSurvival;
import me.sbpro.grassggteleport.GrassGGTeleport;
import me.sbpro.grassggteleport.message.Messages;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickCallback;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

public class SettingsMenu {

    private static final int BUTTON_WIDTH = 180;

    /*
     * ============================================================
     * MAIN SETTINGS MENU
     * ============================================================
     */

    public static void open(
            GrassGGSurvival plugin,
            Player player
    ) {
        /*
         * Opening the dialog one tick later helps avoid occasional
         * client/server timing hitches when the command is executed.
         */
        plugin.getServer().getScheduler().runTask(
                plugin,
                () -> player.showDialog(createMainDialog(plugin))
        );
    }

    private static Dialog createMainDialog(
            GrassGGSurvival plugin
    ) {

        DialogBase base = DialogBase.builder(
                        Component.text("Settings")
                )
                .canCloseWithEscape(true)
                .pause(false)
                .afterAction(DialogBase.DialogAfterAction.NONE)
                .body(List.of(
                        DialogBody.plainMessage(
                                Component.text(
                                        "Choose a category to manage your settings."
                                ),
                                300
                        )
                ))
                .build();

        ActionButton chatButton = ActionButton.create(
                Component.text("Chat"),
                Component.text("Chat and communication settings"),
                BUTTON_WIDTH,
                openDialogAction(
                        player -> player.showDialog(
                                createChatDialog(plugin, player)
                        )
                )
        );

        ActionButton visualsButton = ActionButton.create(
                Component.text("Visuals"),
                Component.text("Visual and display settings"),
                BUTTON_WIDTH,
                openDialogAction(
                        player -> player.showDialog(
                                createVisualsDialog(plugin, player)
                        )
                )
        );

        ActionButton privacyButton = ActionButton.create(
                Component.text("Privacy"),
                Component.text("Privacy and visibility settings"),
                BUTTON_WIDTH,
                openDialogAction(
                        player -> player.showDialog(
                                createPrivacyDialog(plugin, player)
                        )
                )
        );

        ActionButton miscButton = ActionButton.create(
                Component.text("Misc"),
                Component.text("Miscellaneous options"),
                BUTTON_WIDTH,
                openDialogAction(
                        player -> player.showDialog(
                                createMiscDialog(plugin, player)
                        )
                )
        );

        ActionButton closeButton = createCloseButton();

        return createDialog(
                base,
                List.of(
                        chatButton,
                        visualsButton,
                        privacyButton,
                        miscButton
                ),
                closeButton
        );
    }

    /*
     * ============================================================
     * CHAT
     * ============================================================
     */

    private static Dialog createChatDialog(
            GrassGGSurvival plugin,
            Player player
    ) {

        DialogBase base = createBase(
                "Chat",
                "Manage your chat-related settings."
        );

        ActionButton chatVisibilityButton = ActionButton.create(
                Component.text("Chat Visibility: ")
                        .append(
                                plugin.getChatToggleManager()
                                        .isChatDisabled(
                                                player.getUniqueId()
                                        )
                                        ? Component.text(
                                        "Disabled",
                                        NamedTextColor.RED
                                )
                                        : Component.text(
                                        "Enabled",
                                        NamedTextColor.GREEN
                                )
                        ),
                Component.text("Toggle your chat visibility"),
                BUTTON_WIDTH,
                categoryToggleAction(
                        plugin,
                        clickedPlayer ->
                                plugin.getChatToggleManager()
                                        .toggleChat(
                                                clickedPlayer.getUniqueId()
                                        ),
                        clickedPlayer ->
                                createChatDialog(
                                        plugin,
                                        clickedPlayer
                                )
                )
        );

        return createDialog(
                base,
                List.of(chatVisibilityButton),
                createBackButton(plugin, player)
        );
    }

    /*
     * ============================================================
     * VISUALS
     * ============================================================
     */

    private static Dialog createVisualsDialog(
            GrassGGSurvival plugin,
            Player player
    ) {

        DialogBase base = createBase(
                "Visuals",
                "Manage your visual and display settings."
        );

        ActionButton nightVisionButton = ActionButton.create(
                Component.text("Night Vision: ")
                        .append(
                                plugin.getNightVisionManager()
                                        .isNightVisionEnabled(
                                                player.getUniqueId()
                                        )
                                        ? Component.text(
                                        "Enabled",
                                        NamedTextColor.GREEN
                                )
                                        : Component.text(
                                        "Disabled",
                                        NamedTextColor.RED
                                )
                        ),
                Component.text("Toggle your night vision"),
                BUTTON_WIDTH,
                categoryToggleAction(
                        plugin,
                        clickedPlayer ->
                                plugin.getNightVisionManager()
                                        .toggleNightVision(
                                                clickedPlayer
                                        ),
                        clickedPlayer ->
                                createVisualsDialog(
                                        plugin,
                                        clickedPlayer
                                )
                )
        );

        ActionButton scoreboardButton = ActionButton.create(
                Component.text("Scoreboard: ")
                        .append(
                                plugin.getScoreboardManager()
                                        .isScoreboardEnabled(
                                                player.getUniqueId()
                                        )
                                        ? Component.text(
                                        "Enabled",
                                        NamedTextColor.GREEN
                                )
                                        : Component.text(
                                        "Disabled",
                                        NamedTextColor.RED
                                )
                        ),
                Component.text("Toggle your scoreboard"),
                BUTTON_WIDTH,
                categoryToggleAction(
                        plugin,
                        clickedPlayer ->
                                plugin.getScoreboardManager()
                                        .toggleScoreboard(
                                                clickedPlayer
                                        ),
                        clickedPlayer ->
                                createVisualsDialog(
                                        plugin,
                                        clickedPlayer
                                )
                )
        );

        ActionButton glowButton = ActionButton.create(
                Component.text("Glow: ")
                        .append(
                                plugin.getGlowManager()
                                        .isGlowEnabled(
                                                player.getUniqueId()
                                        )
                                        ? Component.text(
                                        "Enabled",
                                        NamedTextColor.GREEN
                                )
                                        : Component.text(
                                        "Disabled",
                                        NamedTextColor.RED
                                )
                        ),
                Component.text("Toggle your player glow"),
                BUTTON_WIDTH,
                categoryToggleAction(
                        plugin,
                        clickedPlayer ->
                                plugin.getGlowManager()
                                        .toggleGlow(
                                                clickedPlayer
                                        ),
                        clickedPlayer ->
                                createVisualsDialog(
                                        plugin,
                                        clickedPlayer
                                )
                )
        );

        return createDialog(
                base,
                List.of(
                        nightVisionButton,
                        scoreboardButton,
                        glowButton
                ),
                createBackButton(plugin, player)
        );
    }

    /*
     * ============================================================
     * PRIVACY
     * ============================================================
     */

    private static Dialog createPrivacyDialog(
            GrassGGSurvival plugin,
            Player player
    ) {

        DialogBase base = createBase(
                "Privacy",
                "Manage your privacy and visibility settings."
        );

        GrassGGTeleport teleportPlugin = getTeleportPlugin();

        if (teleportPlugin == null) {
            return createDialog(
                    base,
                    List.of(
                            ActionButton.create(
                                    Component.text(
                                            "Teleport Settings Unavailable"
                                    ),
                                    Component.text(
                                            "GrassGGTeleport is not currently available."
                                    ),
                                    BUTTON_WIDTH,
                                    null
                            )
                    ),
                    createBackButton(plugin, player)
            );
        }

        boolean requestsDisabled =
                teleportPlugin
                        .getTeleportPreferencesManager()
                        .isTeleportRequestsDisabled(player);

        boolean autoAccept =
                teleportPlugin
                        .getTeleportPreferencesManager()
                        .isAutoAcceptTpa(player);

        ActionButton teleportRequestsButton = ActionButton.create(
                Component.text("Teleport Requests: ")
                        .append(
                                requestsDisabled
                                        ? Component.text(
                                        "Disabled",
                                        NamedTextColor.RED
                                )
                                        : Component.text(
                                        "Enabled",
                                        NamedTextColor.GREEN
                                )
                        ),
                Component.text(
                        "Control whether players can send you teleport requests"
                ),
                BUTTON_WIDTH,
                privacyToggleAction(
                        plugin,
                        clickedPlayer -> {

                            boolean disabled =
                                    teleportPlugin
                                            .getTeleportPreferencesManager()
                                            .toggleTeleportRequests(
                                                    clickedPlayer
                                            );

                            clickedPlayer.sendMessage(
                                    disabled
                                            ? Messages.tptoggleEnabled()
                                            : Messages.tptoggleDisabled()
                            );
                        }
                )
        );

        ActionButton autoAcceptButton = ActionButton.create(
                Component.text("Auto Accept TPA: ")
                        .append(
                                autoAccept
                                        ? Component.text(
                                        "Enabled",
                                        NamedTextColor.GREEN
                                )
                                        : Component.text(
                                        "Disabled",
                                        NamedTextColor.RED
                                )
                        ),
                Component.text(
                        "Automatically accept incoming /tpa requests"
                ),
                BUTTON_WIDTH,
                privacyToggleAction(
                        plugin,
                        clickedPlayer -> {

                            boolean enabled =
                                    teleportPlugin
                                            .getTeleportPreferencesManager()
                                            .toggleAutoAcceptTpa(
                                                    clickedPlayer
                                            );

                            clickedPlayer.sendMessage(
                                    enabled
                                            ? Messages.tpautoEnabled()
                                            : Messages.tpautoDisabled()
                            );
                        }
                )
        );

        return createDialog(
                base,
                List.of(
                        teleportRequestsButton,
                        autoAcceptButton
                ),
                createBackButton(plugin, player)
        );
    }

    private static GrassGGTeleport getTeleportPlugin() {

        Plugin plugin = Bukkit.getPluginManager()
                .getPlugin("GrassGGTeleport");

        if (plugin instanceof GrassGGTeleport teleportPlugin) {
            return teleportPlugin;
        }

        return null;
    }

    /*
     * ============================================================
     * MISC
     * ============================================================
     */

    private static Dialog createMiscDialog(
            GrassGGSurvival plugin,
            Player player
    ) {

        DialogBase base = createBase(
                "Misc",
                "Miscellaneous server options."
        );

        ActionButton enderChestButton = ActionButton.create(
                Component.text("Ender Chest"),
                Component.text("Open your Ender Chest"),
                BUTTON_WIDTH,
                commandAction("enderchest")
        );

        ActionButton craftingButton = ActionButton.create(
                Component.text("Crafting Table"),
                Component.text("Open the crafting table"),
                BUTTON_WIDTH,
                commandAction("craft")
        );

        return createDialog(
                base,
                List.of(
                        enderChestButton,
                        craftingButton
                ),
                createBackButton(plugin, player)
        );
    }

    /*
     * ============================================================
     * DIALOG HELPERS
     * ============================================================
     */

    private static DialogBase createBase(
            String title,
            String description
    ) {

        return DialogBase.builder(
                        Component.text(title)
                )
                .canCloseWithEscape(true)
                .pause(false)
                .afterAction(DialogBase.DialogAfterAction.NONE)
                .body(List.of(
                        DialogBody.plainMessage(
                                Component.text(description),
                                300
                        )
                ))
                .build();
    }

    private static Dialog createDialog(
            DialogBase base,
            List<ActionButton> buttons,
            ActionButton exitButton
    ) {

        return Dialog.create(factory -> {

            var builder = factory.empty();

            builder.base(base);

            builder.type(
                    DialogType.multiAction(
                            buttons,
                            exitButton,
                            2
                    )
            );
        });
    }

    /*
     * ============================================================
     * ACTIONS
     * ============================================================
     */

    private static DialogAction categoryToggleAction(
            GrassGGSurvival plugin,
            Consumer<Player> action,
            Function<Player, Dialog> dialogSupplier
    ) {

        return DialogAction.customClick(
                (response, audience) -> {

                    if (!(audience instanceof Player player)) {
                        return;
                    }

                    action.accept(player);

                    plugin.getServer().getScheduler().runTask(
                            plugin,
                            () -> player.showDialog(
                                    dialogSupplier.apply(player)
                            )
                    );
                },
                ClickCallback.Options.builder()
                        .uses(ClickCallback.UNLIMITED_USES)
                        .build()
        );
    }

    private static DialogAction privacyToggleAction(
            GrassGGSurvival plugin,
            Consumer<Player> action
    ) {

        return DialogAction.customClick(
                (response, audience) -> {

                    if (!(audience instanceof Player player)) {
                        return;
                    }

                    action.accept(player);

                    plugin.getServer().getScheduler().runTask(
                            plugin,
                            () -> player.showDialog(
                                    createPrivacyDialog(
                                            plugin,
                                            player
                                    )
                            )
                    );
                },
                ClickCallback.Options.builder()
                        .uses(ClickCallback.UNLIMITED_USES)
                        .build()
        );
    }

    private static DialogAction openDialogAction(
            Consumer<Player> action
    ) {

        return DialogAction.customClick(
                (response, audience) -> {

                    if (!(audience instanceof Player player)) {
                        return;
                    }

                    action.accept(player);
                },
                ClickCallback.Options.builder()
                        .uses(ClickCallback.UNLIMITED_USES)
                        .build()
        );
    }

    private static DialogAction commandAction(
            String command
    ) {

        return DialogAction.customClick(
                (response, audience) -> {

                    if (!(audience instanceof Player player)) {
                        return;
                    }

                    player.performCommand(command);
                },
                ClickCallback.Options.builder()
                        .uses(ClickCallback.UNLIMITED_USES)
                        .build()
        );
    }

    /*
     * ============================================================
     * NAVIGATION BUTTONS
     * ============================================================
     */

    private static ActionButton createBackButton(
            GrassGGSurvival plugin,
            Player player
    ) {

        return ActionButton.create(
                Component.text("Back"),
                Component.text("Return to Settings"),
                BUTTON_WIDTH,
                openDialogAction(
                        clickedPlayer ->
                                clickedPlayer.showDialog(
                                        createMainDialog(plugin)
                                )
                )
        );
    }

    private static ActionButton createCloseButton() {

        return ActionButton.create(
                Component.text("Close"),
                Component.text("Close the settings menu"),
                BUTTON_WIDTH,
                DialogAction.customClick(
                        (response, audience) -> {

                            if (!(audience instanceof Player player)) {
                                return;
                            }

                            player.closeDialog();
                        },
                        ClickCallback.Options.builder()
                                .uses(ClickCallback.UNLIMITED_USES)
                                .build()
                )
        );
    }
}