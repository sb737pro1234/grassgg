package me.sbpro.grassgg.commands.misc;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class NightVisionCommand implements CommandExecutor {

    /*
     * ============================
     *          MESSAGES
     * ============================
     */

    private static final String PREFIX = "§2§lGRASS.GG §8» §f";

    private static final String NO_PERMISSION_SELF =
            PREFIX + "§cYou do not have permission to use night vision.";

    private static final String NO_PERMISSION_OTHERS =
            PREFIX + "§cYou do not have permission to use night vision on other players.";

    private static final String PLAYER_NOT_FOUND =
            PREFIX + "§cThat player is not online.";

    private static final String SELF_ENABLED =
            PREFIX + "§fYour night vision has been §aenabled§f.";

    private static final String SELF_DISABLED =
            PREFIX + "§fYour night vision has been §cdisabled§f.";

    private static final String OTHER_ENABLED =
            PREFIX + "§2%player%§f's night vision has been §aenabled§f.";

    private static final String OTHER_DISABLED =
            PREFIX + "§2%player%§f's night vision has been §cdisabled§f.";

    private static final String PLAYER_ONLY =
            PREFIX + "§cOnly players can use this command.";

    /*
     * ============================
     *        COMMAND LOGIC
     * ============================
     */

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        // /nightvision [PLAYER]
        if (args.length >= 1) {

            if (!sender.hasPermission("grassgg.nightvision.others")) {
                sender.sendMessage(NO_PERMISSION_OTHERS);
                return true;
            }

            Player target = Bukkit.getPlayerExact(args[0]);

            if (target == null) {
                sender.sendMessage(PLAYER_NOT_FOUND);
                return true;
            }

            // Toggle night vision
            if (target.hasPotionEffect(PotionEffectType.NIGHT_VISION)) {

                target.removePotionEffect(PotionEffectType.NIGHT_VISION);

                sender.sendMessage(
                        OTHER_DISABLED.replace("%player%", target.getName())
                );

                if (!target.equals(sender)) {
                }

            } else {

                target.addPotionEffect(new PotionEffect(
                        PotionEffectType.NIGHT_VISION,
                        PotionEffect.INFINITE_DURATION,
                        0,
                        false,
                        false,
                        false
                ));

                sender.sendMessage(
                        OTHER_ENABLED.replace("%player%", target.getName())
                );

                if (!target.equals(sender)) {
                }
            }

            return true;
        }

        // /nightvision
        if (!(sender instanceof Player player)) {
            sender.sendMessage(PLAYER_ONLY);
            return true;
        }

        if (!player.hasPermission("grassgg.nightvision.self")) {
            player.sendMessage(NO_PERMISSION_SELF);
            return true;
        }

        // Toggle own night vision
        if (player.hasPotionEffect(PotionEffectType.NIGHT_VISION)) {

            player.removePotionEffect(PotionEffectType.NIGHT_VISION);

            player.sendMessage(SELF_DISABLED);

        } else {

            player.addPotionEffect(new PotionEffect(
                    PotionEffectType.NIGHT_VISION,
                    PotionEffect.INFINITE_DURATION,
                    0,
                    false,
                    false,
                    false
            ));

            player.sendMessage(SELF_ENABLED);
        }

        return true;
    }
}