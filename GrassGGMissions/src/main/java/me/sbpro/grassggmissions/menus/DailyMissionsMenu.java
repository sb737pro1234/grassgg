package me.sbpro.grassggmissions.menus;

import me.sbpro.grassggmissions.GrassGGMissions;
import me.sbpro.grassggmissions.managers.ResetManager;
import me.sbpro.grassggmissions.missions.PlayerMission;
import me.sbpro.grassggmissions.missions.PlayerMissionData;
import me.sbpro.grassggmissions.utils.ItemBuilder;
import me.sbpro.grassggmissions.utils.ProgressBarUtils;
import me.sbpro.grassggmissions.utils.TimeUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class DailyMissionsMenu {

    private static final int[] MISSION_SLOTS = {10, 12, 14, 16};
    private static final int[] REROLL_SLOTS = {19, 21, 23, 25};

    public static final String TITLE = "§2§lDaily Missions";

    public static void open(Player player) {

        Inventory inventory = Bukkit.createInventory(null, 54, TITLE);

        PlayerMissionData data = GrassGGMissions.getInstance()
                .getPlayerMissionManager()
                .getPlayerData(player.getUniqueId());

        createBorder(inventory);
        createInformationItem(inventory);
        createRerollButtons(inventory, data);
        createResetItem(player, inventory);
        createStatusItem(inventory);

        int index = 0;

        for (int i = 1; i <= 4; i++) {

            PlayerMission mission = data.getMissions().get(i);

            if (mission == null) {
                continue;
            }


            ItemStack item;

            if (mission.isCompleted()) {
                item = new ItemStack(Material.ENCHANTED_BOOK);
            } else {
                item = new ItemStack(Material.BOOK);
            }

            ItemMeta meta = item.getItemMeta();

            meta.setDisplayName("§a§l" + mission.getMission().getDisplayName());

            List<String> lore = new ArrayList<>();

            lore.add("§7Complete this mission to earn rewards!");
            lore.add("");

            lore.add("§eProgress");
            lore.add(" §f" + mission.getProgressString());
            lore.add(" " + ProgressBarUtils.getProgressBar(
                    mission.getProgress(),
                    mission.getMission().getGoal()
            ));

            lore.add("");

            lore.add("§6Reward");
            lore.add(" §f£" + String.format("%,.0f", mission.getMission().getReward()));

            lore.add("");

            if (mission.isCompleted()) {

                lore.add("§a✔ Mission Completed!");


            } else {

                lore.add("§7Keep going!");

            }

            meta.setLore(lore);

            item.setItemMeta(meta);

            if (index >= MISSION_SLOTS.length) {
                break;
            }

            inventory.setItem(MISSION_SLOTS[index], item);

            index++;



        }

        player.openInventory(inventory);

    }
    private static void createBorder(Inventory inventory) {

        ItemStack pane = new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE)
                .setName(" ")
                .build();

        int[] slots = {
                0,1,2,3,4,5,6,7,8,
                9,17,
                18,26,
                27,35,
                36,37,39,41,43,44,
                45,46,47,48,49,50,51,52,53
        };

        for (int slot : slots) {
            inventory.setItem(slot, pane);
        }

    }

    private static void createInformationItem(Inventory inventory) {

        ItemStack item = new ItemBuilder(Material.BOOK)
                .setName("§e§lInformation")
                .setLore(List.of(
                        "",
                        "§7Complete all four daily missions",
                        "§7to earn rewards.",
                        "",
                        "§7• Missions reset every 24 hours.",
                        "§7• Rewards are given instantly.",
                        "§7• Each player gets unique missions.",
                        "§7• Completed missions show enchant.",
                        "§7• For crafting missions, using /craft will not work.",
                        "",
                        "§eGood luck!"
                ))
                .build();

        inventory.setItem(38, item);

    }
    private static void createRerollButtons(Inventory inventory, PlayerMissionData data) {

        for (int i = 1; i <= 4; i++) {

            PlayerMission mission = data.getMissions().get(i);

            if (mission == null) {
                continue;
            }

            ItemStack item = new ItemBuilder(Material.COMPASS)
                    .setName("§a§l↻ Reroll Mission")
                    .setLore(List.of(
                            "",
                            "§7Replace this mission with",
                            "§7a brand new random mission.",
                            "",
                            "§7Cost: §6£35,000",
                            "",
                            "§eClick to reroll."
                    ))
                    .build();

            inventory.setItem(REROLL_SLOTS[i - 1], item);

        }

    }

    private static void createResetItem(Player player, Inventory inventory) {

        ItemStack item = new ItemBuilder(Material.CLOCK)
                .setName("§6§lNext Reset")
                .setLore(List.of(
                        "",
                        "§7Time Remaining",
                        "§f" + TimeUtils.format(
                                ResetManager.getTimeUntilReset(player)
                        ),
                        "",
                        "§7Daily missions reset",
                        "§7every 24 hours."
                ))
                .build();

        inventory.setItem(42, item);

    }

    private static void createStatusItem(Inventory inventory) {

        ItemStack item = new ItemBuilder(Material.EMERALD)
                .setName("§a§lDaily Missions")
                .setLore(List.of(
                        "",
                        "§7Complete your daily missions",
                        "§7to earn money and progress.",
                        "",
                        "§7Or click one of the four",
                        "§7buttons below a mission",
                        "§7to reroll it.",
                        "",
                        "§aGood luck!"
                ))
                .build();

        inventory.setItem(40, item);

    }


}