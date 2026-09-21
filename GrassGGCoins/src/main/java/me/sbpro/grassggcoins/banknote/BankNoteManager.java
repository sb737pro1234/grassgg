package me.sbpro.grassggcoins.banknote;

import me.sbpro.grassggcoins.GrassGGCoins;
import me.sbpro.grassggcoins.Messages;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.io.File;
import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

public final class BankNoteManager {

    private final GrassGGCoins plugin;
    private final File file;
    private final YamlConfiguration data;

    private final NamespacedKey noteIdKey;
    private final NamespacedKey amountKey;
    private final NamespacedKey withdrawnByUuidKey;
    private final NamespacedKey withdrawnByNameKey;

    public BankNoteManager(GrassGGCoins plugin) {
        this.plugin = plugin;
        this.file = new File(plugin.getDataFolder(), "banknotes.yml");
        this.data = YamlConfiguration.loadConfiguration(file);

        this.noteIdKey = new NamespacedKey(plugin, "bank_note_id");
        this.amountKey = new NamespacedKey(plugin, "bank_note_amount");
        this.withdrawnByUuidKey =
                new NamespacedKey(plugin, "bank_note_withdrawn_by_uuid");
        this.withdrawnByNameKey =
                new NamespacedKey(plugin, "bank_note_withdrawn_by_name");
    }

    public ItemStack createBankNote(Player player, long amount) {
        UUID noteId = UUID.randomUUID();

        ItemStack item = new ItemStack(Messages.BANK_NOTE_MATERIAL);
        ItemMeta meta = item.getItemMeta();

        if (meta == null) {
            return item;
        }

        meta.setDisplayName(Messages.bankNoteName());
        meta.setLore(Messages.bankNoteLore(amount, player.getName()));

        PersistentDataContainer pdc = meta.getPersistentDataContainer();

        pdc.set(
                noteIdKey,
                PersistentDataType.STRING,
                noteId.toString()
        );

        pdc.set(
                amountKey,
                PersistentDataType.LONG,
                amount
        );

        pdc.set(
                withdrawnByUuidKey,
                PersistentDataType.STRING,
                player.getUniqueId().toString()
        );

        pdc.set(
                withdrawnByNameKey,
                PersistentDataType.STRING,
                player.getName()
        );

        item.setItemMeta(meta);

        return item;
    }

    public Optional<BankNoteData> read(ItemStack item) {

        if (item == null || item.getType().isAir()) {
            return Optional.empty();
        }

        ItemMeta meta = item.getItemMeta();

        if (meta == null) {
            return Optional.empty();
        }

        PersistentDataContainer pdc = meta.getPersistentDataContainer();

        String noteIdString =
                pdc.get(noteIdKey, PersistentDataType.STRING);

        Long amount =
                pdc.get(amountKey, PersistentDataType.LONG);

        String withdrawnByUuidString =
                pdc.get(withdrawnByUuidKey, PersistentDataType.STRING);

        String withdrawnByName =
                pdc.get(withdrawnByNameKey, PersistentDataType.STRING);

        if (noteIdString == null
                || amount == null
                || withdrawnByUuidString == null
                || withdrawnByName == null
                || amount <= 0L) {

            return Optional.empty();
        }

        try {

            UUID noteId = UUID.fromString(noteIdString);
            UUID withdrawnByUuid =
                    UUID.fromString(withdrawnByUuidString);

            return Optional.of(
                    new BankNoteData(
                            noteId,
                            amount,
                            withdrawnByUuid,
                            withdrawnByName
                    )
            );

        } catch (IllegalArgumentException exception) {
            return Optional.empty();
        }
    }

    public boolean isBankNote(ItemStack item) {
        return read(item).isPresent();
    }

    public synchronized boolean isRedeemed(UUID noteId) {
        return data.getBoolean(
                "redeemed." + noteId,
                false
        );
    }

    public synchronized boolean markRedeemed(UUID noteId) {

        if (isRedeemed(noteId)) {
            return false;
        }

        String path = "redeemed." + noteId;

        data.set(path, true);

        if (!saveInternal()) {
            data.set(path, null);
            return false;
        }

        return true;
    }

    public synchronized void save() {
        saveInternal();
    }

    private boolean saveInternal() {

        try {
            data.save(file);
            return true;

        } catch (IOException exception) {

            plugin.getLogger().severe(
                    "Could not save banknotes.yml: "
                            + exception.getMessage()
            );

            return false;
        }
    }
}