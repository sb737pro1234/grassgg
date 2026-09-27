package me.sbpro.grassggalliances.gui;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import me.sbpro.grassggalliances.GrassGGAlliances;
import me.sbpro.grassggalliances.Messages;
import me.sbpro.grassggalliances.model.Alliance;
import me.sbpro.grassggalliances.service.AllianceXpService;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

public final class AllianceInfoGui {
    private static final LegacyComponentSerializer LEGACY = LegacyComponentSerializer.legacySection();

    private AllianceInfoGui() {
    }

    public static void open(GrassGGAlliances plugin, Player player, Alliance alliance) {
        Inventory inventory = Bukkit.createInventory(
                (InventoryHolder) new AllianceInfoGuiHolder(alliance.getName()),
                45,
                plugin.getMessageService().getComponent("info-gui-title")
        );

        inventory.setItem(4, createOverviewItem(plugin, alliance));

        // Leave the rest of the menu empty so the GUI has a clean look.

        // Three central action buttons.
        inventory.setItem(20, createSimpleItem(Material.PLAYER_HEAD,
                Messages.INFO_GUI_MEMBERS,
                List.of("§fView everyone in your alliance.")));
        inventory.setItem(22, createSimpleItem(Material.EXPERIENCE_BOTTLE,
                Messages.INFO_GUI_LEVEL,
                List.of(
                        "§fAlliance Level: " + Messages.ALLIANCE_COLOR + alliance.getLevel(),
                        "§fAlliance XP: " + Messages.ALLIANCE_COLOR + Math.round(alliance.getXp()),
                        "§fProgress: " + plugin.getXpService().getProgressBar(alliance),
                        " ",
                        Messages.ALLIANCE_COLOR + "Click to open the Alliance Level menu."
                )));
        inventory.setItem(24, createSimpleItem(Material.CHEST,
                Messages.INFO_GUI_XP_VALUES,
                List.of("§fView how your alliance earns XP.")));
        inventory.setItem(40, createSimpleItem(Material.BARRIER,
                plugin.getMessageService().getRaw("go-back"),
                List.of("§fClose this menu")));

        player.openInventory(inventory);
    }

    public static void openMembers(GrassGGAlliances plugin, Player player, Alliance alliance) {
        Inventory inventory = Bukkit.createInventory(
                (InventoryHolder) new AllianceMembersGuiHolder(alliance.getName()),
                27,
                plugin.getMessageService().getComponent("members-gui-title")
        );

        List<UUID> members = new ArrayList<>(alliance.getMembers());
        members.sort(Comparator.comparing(uuid -> {
            OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(uuid);
            String name = offlinePlayer.getName();
            if (uuid.equals(alliance.getOwner())) {
                return "0-" + (name == null ? uuid.toString() : name);
            }
            return "1-" + (name == null ? uuid.toString() : name);
        }, String.CASE_INSENSITIVE_ORDER));

        int slot = 0;
        for (UUID memberId : members) {
            if (slot >= 18) {
                break;
            }

            OfflinePlayer member = Bukkit.getOfflinePlayer(memberId);
            String name = member.getName() == null ? memberId.toString().substring(0, 8) : member.getName();
            String role = memberId.equals(alliance.getOwner()) ? Messages.ALLIANCE_COLOR + "§fOwner" : "§r§fMember";
            List<String> lore = List.of(role, member.isOnline() ? Messages.ALLIANCE_COLOR + "§a● Online" : "§7● Offline");
            inventory.setItem(slot++, createPlayerHead(member, name, lore));
        }

        inventory.setItem(22, createSimpleItem(Material.ARROW,
                plugin.getMessageService().getRaw("go-back"),
                List.of("§fReturn to alliance information")));

        player.openInventory(inventory);
    }

    private static ItemStack createOverviewItem(GrassGGAlliances plugin, Alliance alliance) {
        AllianceXpService xpService = plugin.getXpService();
        OfflinePlayer owner = Bukkit.getOfflinePlayer(alliance.getOwner());
        String ownerName = owner.getName() == null ? alliance.getOwner().toString() : owner.getName();

        List<String> lore = List.of(
                "§fOwner: " + Messages.ALLIANCE_COLOR + ownerName,
                "§fMembers: " + Messages.ALLIANCE_COLOR + alliance.getMembers().size() + "§f/5",
                "§fAlliance Level: " + Messages.ALLIANCE_COLOR + alliance.getLevel(),
                "§fAlliance XP: " + Messages.ALLIANCE_COLOR + Math.round(alliance.getXp()),
                "§fProgress: " + xpService.getProgressBar(alliance)
        );

        return createSimpleItem(Material.GRASS_BLOCK,
                Messages.ALLIANCE_COLOR + alliance.getName(),
                lore);
    }

    private static ItemStack createPlayerHead(OfflinePlayer player, String name, List<String> loreLines) {
        ItemStack item = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta meta = (SkullMeta) item.getItemMeta();
        meta.setOwningPlayer(player);
        meta.displayName(LEGACY.deserialize("§f" + name).decoration(TextDecoration.ITALIC, false));
        meta.lore(loreLines.stream().map(LEGACY::deserialize).map(component -> component.decoration(TextDecoration.ITALIC, false)).map(component -> (Component) component).toList());
        item.setItemMeta(meta);
        return item;
    }

    private static ItemStack createSimpleItem(Material material, String name, List<String> loreLines) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        meta.displayName(LEGACY.deserialize(name).decoration(TextDecoration.ITALIC, false));
        meta.lore(loreLines.stream().map(LEGACY::deserialize).map(component -> component.decoration(TextDecoration.ITALIC, false)).map(component -> (Component) component).toList());
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        item.setItemMeta(meta);
        return item;
    }
}
