package me.sbpro.grassgg.commands.misc;

import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.group.Group;
import net.luckperms.api.model.user.User;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StaffCommand implements CommandExecutor {

    @Override
    public boolean onCommand(
            CommandSender sender,
            Command command,
            String label,
            String[] args
    ) {

        LuckPerms luckPerms = LuckPermsProvider.get();

        List<StaffEntry> staffMembers = new ArrayList<>();

        for (Player player : Bukkit.getOnlinePlayers()) {

            if (!player.hasPermission("grassgg.staff")) {
                continue;
            }

            if (sender instanceof Player viewer
                    && !viewer.canSee(player)) {
                continue;
            }

            User user = luckPerms.getUserManager()
                    .getUser(player.getUniqueId());

            if (user == null) {
                continue;
            }

            Group group = luckPerms.getGroupManager()
                    .getGroup(user.getPrimaryGroup());

            int weight = 0;

            if (group != null) {
                weight = group.getWeight().orElse(0);
            }

            String prefix = user.getCachedData()
                    .getMetaData()
                    .getPrefix();

            if (prefix == null) {
                prefix = "";
            }

            staffMembers.add(
                    new StaffEntry(
                            player.getName(),
                            prefix,
                            weight
                    )
            );
        }

        staffMembers.sort(
                Comparator.comparingInt(
                        StaffEntry::weight
                ).reversed()
        );

        sender.sendMessage(colorize(
                "&2&lStaff Online &8("
                        + staffMembers.size()
                        + ")"
        ));

        sender.sendMessage("");

        if (staffMembers.isEmpty()) {

            sender.sendMessage(colorize(
                    "&cThere are currently no staff online."
            ));

            return true;
        }

        for (StaffEntry entry : staffMembers) {

            sender.sendMessage(
                    colorize(entry.prefix())
                            + entry.name()
            );
        }

        return true;
    }

    private String colorize(String message) {

        Pattern pattern = Pattern.compile("&#([A-Fa-f0-9]{6})");
        Matcher matcher = pattern.matcher(message);

        StringBuffer buffer = new StringBuffer();

        while (matcher.find()) {

            String hex = matcher.group(1);

            StringBuilder replacement = new StringBuilder("§x");

            for (char character : hex.toCharArray()) {
                replacement.append("§").append(character);
            }

            matcher.appendReplacement(
                    buffer,
                    replacement.toString()
            );
        }

        matcher.appendTail(buffer);

        return buffer.toString()
                .replace("&", "§");
    }

    private record StaffEntry(
            String name,
            String prefix,
            int weight
    ) {
    }
}