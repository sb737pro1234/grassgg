package me.sbpro.grassggenchants;

import org.bukkit.enchantments.Enchantment;

import java.util.Set;
import java.util.Set;

public final class Messages {

    private Messages() {
    }

    // GrassGG Enchants colour: Cyan (#00D9FF)
    public static final String CUSTOM = "§x§0§0§D§9§F§F";
    public static final String WHITE = "§f";
    public static final String ERROR = "§c";
    public static final String GRAY = "§7";
    public static final String DARK_GRAY = "§8";
    public static final String GREEN = "§a";
    public static final String RED = "§c";

    public static final String PREFIX = CUSTOM + "§lENCHANTS §8» §f";

    // Custom item
    public static final String ENCHANT_UPGRADE_NAME = CUSTOM + "Enchant Upgrade";
    public static final String[] ENCHANT_UPGRADE_LORE = {
            GRAY + "Apply this to an enchanted item",
            GRAY + "and select an enchant to upgrade."
    };

    // GUI
    public static final String ENCHANT_SELECT_TITLE = CUSTOM + "Enchant Upgrade";
    public static final String CONFIRM_TITLE = CUSTOM + "Confirm Upgrade";

    public static final String CONFIRM_NAME = GREEN + "Confirm";
    public static final String CANCEL_NAME = RED + "Cancel";

    public static final String CONFIRM_LORE = GRAY + "Upgrade this enchant.";
    public static final String CANCEL_LORE = GRAY + "Cancel the upgrade.";

    public static final String DISPLAY_ITEM_LORE = GRAY + "Upgrade: " + WHITE + "%enchant% %current% " + DARK_GRAY + "→ " + WHITE + "%enchant% %next%";

    // Messages
    public static final String GIVEN = PREFIX + "Given " + CUSTOM + "%item%" + WHITE + " to " + CUSTOM + "%player%" + WHITE + ".";
    public static final String PLAYER_NOT_FOUND = PREFIX + ERROR + "That player could not be found.";
    public static final String NO_PERMISSION = PREFIX + ERROR + "You do not have permission to use this command.";
    public static final String INVALID_ITEM = PREFIX + ERROR + "Unknown enchant item. Available: " + WHITE + "enchant_upgrade";
    public static final String NO_ENCHANTMENTS = PREFIX + ERROR + "This item has no enchantments that can be upgraded.";
    public static final String UPGRADED = PREFIX + "Upgraded " + CUSTOM + "%enchant% %current%" + WHITE + " to " + CUSTOM + "%enchant% %next%" + WHITE + ".";
    public static final String NOT_ENCHANTED_ITEM = PREFIX + ERROR + "You must right-click an item that has an eligible enchantment.";
    public static final String NO_ITEM = PREFIX + ERROR + "You must select an item to enchant.";

    // Sounds
    public static final String SUCCESS_SOUND = "ENTITY_PLAYER_LEVELUP";
    public static final String ERROR_SOUND = "ENTITY_VILLAGER_NO";
    public static final float SOUND_VOLUME = 1.0f;
    public static final float SUCCESS_PITCH = 1.2f;
    public static final float ERROR_PITCH = 1.0f;

    /*
     * Configure which vanilla enchantments are allowed to exceed their
     * normal maximum level.
     *
     * An enchantment is only eligible when:
     * 1. It is in this list.
     * 2. Its vanilla maximum level is 2 or higher.
     * 3. The item currently has it at its vanilla maximum level.
     *
     * Enchantments such as Mending, Infinity and Silk Touch are intentionally
     * not included because increasing them beyond their vanilla level would
     * not make sense for this item.
     */
    public static final Set<Enchantment> UPGRADEABLE_ENCHANTMENTS = Set.of(
            Enchantment.SHARPNESS,
            Enchantment.SMITE,
            Enchantment.BANE_OF_ARTHROPODS,
            Enchantment.KNOCKBACK,
            Enchantment.FIRE_ASPECT,
            Enchantment.LOOTING,
            Enchantment.EFFICIENCY,
            Enchantment.UNBREAKING,
            Enchantment.FORTUNE,
            Enchantment.POWER,
            Enchantment.PUNCH,
            Enchantment.FLAME,
            Enchantment.PROTECTION,
            Enchantment.FIRE_PROTECTION,
            Enchantment.FEATHER_FALLING,
            Enchantment.BLAST_PROTECTION,
            Enchantment.PROJECTILE_PROTECTION,
            Enchantment.RESPIRATION,
            Enchantment.AQUA_AFFINITY,
            Enchantment.DEPTH_STRIDER,
            Enchantment.THORNS,
            Enchantment.FROST_WALKER,
            Enchantment.SOUL_SPEED,
            Enchantment.SWIFT_SNEAK,
            Enchantment.LURE,
            Enchantment.LUCK_OF_THE_SEA,
            Enchantment.IMPALING,
            Enchantment.RIPTIDE,
            Enchantment.LOYALTY,
            Enchantment.CHANNELING,
            Enchantment.MULTISHOT,
            Enchantment.QUICK_CHARGE,
            Enchantment.PIERCING
    );

    public static String format(String message, String... replacements) {
        for (int i = 0; i + 1 < replacements.length; i += 2) {
            message = message.replace(replacements[i], replacements[i + 1]);
        }
        return message;
    }
}
