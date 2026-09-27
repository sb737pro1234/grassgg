package me.sbpro.grassggalliances;

import java.util.ArrayList;
import java.util.List;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

/** Compatibility service backed entirely by Messages.java. */
public final class MessageService {
    private static final LegacyComponentSerializer LEGACY = LegacyComponentSerializer.legacySection();

    public void saveDefaultMessages() {
        // Messages are stored in Java, so there is no file to create.
    }

    public void reload() {
        // Messages are stored in Java, so there is nothing to reload.
    }

    public String getRaw(String path) {
        return Messages.get(path);
    }

    public List<String> getStringList(String path) {
        return switch (path) {
            case "info-format" -> Messages.INFO_FORMAT;
            case "top-header" -> Messages.TOP_HEADER;
            default -> List.of();
        };
    }

    public Component getComponent(String path) {
        return color(getRaw(path));
    }

    public Component prefixed(String path) {
        return prefixed(path, input -> input);
    }

    public Component prefixed(String path, PlaceholderReplacer replacer) {
        return color(Messages.PREFIX + replacer.apply(getRaw(path)));
    }

    public List<Component> getComponentList(String path, PlaceholderReplacer replacer) {
        ArrayList<Component> components = new ArrayList<>();
        for (String line : getStringList(path)) {
            components.add(color(replacer.apply(line)));
        }
        return components;
    }

    private Component color(String input) {
        return LEGACY.deserialize(input)
                .decoration(TextDecoration.ITALIC, false);
    }

    @FunctionalInterface
    public interface PlaceholderReplacer {
        String apply(String input);
    }
}
