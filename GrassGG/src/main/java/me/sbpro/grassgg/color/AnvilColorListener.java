package me.sbpro.grassgg.color;

import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AnvilColorListener implements Listener {

    private static final Pattern HEX_PATTERN = Pattern.compile("&#([A-Fa-f0-9]{6})");

    @EventHandler
    public void onPrepareAnvil(PrepareAnvilEvent event) {

        if (event.getResult() == null) {
            return;
        }

        String renameText = event.getView().getRenameText();

        if (renameText == null || renameText.isEmpty()) {
            return;
        }

        if (!renameText.contains("&")) {
            return;
        }

        ItemStack result = event.getResult().clone();
        ItemMeta meta = result.getItemMeta();

        if (meta == null) {
            return;
        }

        String coloredName = applyColors(renameText);

        meta.setDisplayName(coloredName);
        result.setItemMeta(meta);

        event.setResult(result);
    }

    private String applyColors(String text) {

        Matcher matcher = HEX_PATTERN.matcher(text);
        StringBuffer buffer = new StringBuffer();

        while (matcher.find()) {
            String hex = matcher.group(1);

            StringBuilder replacement = new StringBuilder("§x");

            for (char character : hex.toCharArray()) {
                replacement.append('§').append(character);
            }

            matcher.appendReplacement(
                    buffer,
                    Matcher.quoteReplacement(replacement.toString())
            );
        }

        matcher.appendTail(buffer);

        return ChatColor.translateAlternateColorCodes('&', buffer.toString());
    }
}