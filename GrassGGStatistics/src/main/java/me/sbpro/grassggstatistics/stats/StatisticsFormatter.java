package me.sbpro.grassggstatistics.stats;

import org.bukkit.Material;
import org.bukkit.Statistic;
import org.bukkit.entity.Player;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class StatisticsFormatter {

    private static final NumberFormat NUMBER_FORMAT = NumberFormat.getInstance();

    public static String formatNumber(long number) {
        return NUMBER_FORMAT.format(number);
    }

    public static String formatDistance(long centimetres) {

        double metres = centimetres / 100.0;

        if (metres >= 1000) {
            return new DecimalFormat("#.#").format(metres / 1000.0) + "km";
        }

        return (long) metres + "m";
    }

    public static String formatPlaytime(int ticks) {

        long seconds = ticks / 20L;
        long minutes = seconds / 60;
        long hours = minutes / 60;
        long days = hours / 24;

        if (days > 0) {
            return days + "d " + (hours % 24) + "h";
        }

        if (hours > 0) {
            return hours + "h";
        }

        return minutes + "m";
    }

    public static String formatDate(long time) {

        if (time <= 0) {
            return "Unknown";
        }

        return new SimpleDateFormat("dd MMM yyyy").format(new Date(time));
    }

    public static long getBlocksBroken(Player player) {

        long total = 0;

        for (Material material : Material.values()) {

            if (!material.isBlock()) {
                continue;
            }

            try {
                total += player.getStatistic(Statistic.MINE_BLOCK, material);
            } catch (IllegalArgumentException ignored) {
            }
        }

        return total;
    }

    public static long getOreBlocksMined(Player player) {

        return
                player.getStatistic(Statistic.MINE_BLOCK, Material.COAL_ORE)
                        + player.getStatistic(Statistic.MINE_BLOCK, Material.DEEPSLATE_COAL_ORE)

                        + player.getStatistic(Statistic.MINE_BLOCK, Material.IRON_ORE)
                        + player.getStatistic(Statistic.MINE_BLOCK, Material.DEEPSLATE_IRON_ORE)

                        + player.getStatistic(Statistic.MINE_BLOCK, Material.COPPER_ORE)
                        + player.getStatistic(Statistic.MINE_BLOCK, Material.DEEPSLATE_COPPER_ORE)

                        + player.getStatistic(Statistic.MINE_BLOCK, Material.GOLD_ORE)
                        + player.getStatistic(Statistic.MINE_BLOCK, Material.DEEPSLATE_GOLD_ORE)

                        + player.getStatistic(Statistic.MINE_BLOCK, Material.REDSTONE_ORE)
                        + player.getStatistic(Statistic.MINE_BLOCK, Material.DEEPSLATE_REDSTONE_ORE)

                        + player.getStatistic(Statistic.MINE_BLOCK, Material.LAPIS_ORE)
                        + player.getStatistic(Statistic.MINE_BLOCK, Material.DEEPSLATE_LAPIS_ORE)

                        + player.getStatistic(Statistic.MINE_BLOCK, Material.DIAMOND_ORE)
                        + player.getStatistic(Statistic.MINE_BLOCK, Material.DEEPSLATE_DIAMOND_ORE)

                        + player.getStatistic(Statistic.MINE_BLOCK, Material.EMERALD_ORE)
                        + player.getStatistic(Statistic.MINE_BLOCK, Material.DEEPSLATE_EMERALD_ORE)

                        + player.getStatistic(Statistic.MINE_BLOCK, Material.NETHER_GOLD_ORE)
                        + player.getStatistic(Statistic.MINE_BLOCK, Material.NETHER_QUARTZ_ORE)

                        + player.getStatistic(Statistic.MINE_BLOCK, Material.ANCIENT_DEBRIS);
    }

    public static long getBlocksPlaced(Player player) {

        long total = 0;

        for (Material material : Material.values()) {

            if (!material.isBlock()) {
                continue;
            }

            try {
                total += player.getStatistic(Statistic.USE_ITEM, material);
            } catch (IllegalArgumentException ignored) {
            }
        }

        return total;
    }

    public static long getToolsBroken(Player player) {

        long total = 0;

        for (Material material : Material.values()) {

            String name = material.name();

            if (!name.endsWith("_PICKAXE")
                    && !name.endsWith("_AXE")
                    && !name.endsWith("_SHOVEL")
                    && !name.endsWith("_HOE")
                    && !name.endsWith("_SWORD")) {
                continue;
            }

            try {
                total += player.getStatistic(Statistic.BREAK_ITEM, material);
            } catch (IllegalArgumentException ignored) {
            }
        }

        return total;
    }

    public static long getItemsCrafted(Player player) {

        long total = 0;

        for (Material material : Material.values()) {

            try {
                total += player.getStatistic(Statistic.CRAFT_ITEM, material);
            } catch (IllegalArgumentException ignored) {
            }
        }

        return total;
    }

    public static long getCropsHarvested(Player player) {

        return
                player.getStatistic(Statistic.MINE_BLOCK, Material.WHEAT)
                        + player.getStatistic(Statistic.MINE_BLOCK, Material.CARROTS)
                        + player.getStatistic(Statistic.MINE_BLOCK, Material.POTATOES)
                        + player.getStatistic(Statistic.MINE_BLOCK, Material.BEETROOTS)
                        + player.getStatistic(Statistic.MINE_BLOCK, Material.NETHER_WART)
                        + player.getStatistic(Statistic.MINE_BLOCK, Material.COCOA)
                        + player.getStatistic(Statistic.MINE_BLOCK, Material.PUMPKIN)
                        + player.getStatistic(Statistic.MINE_BLOCK, Material.MELON);
    }

    public static long getTotalDistance(Player player) {

        return (long) player.getStatistic(Statistic.WALK_ONE_CM)
                + player.getStatistic(Statistic.SPRINT_ONE_CM)
                + player.getStatistic(Statistic.CROUCH_ONE_CM)
                + player.getStatistic(Statistic.SWIM_ONE_CM)
                + player.getStatistic(Statistic.AVIATE_ONE_CM)
                + player.getStatistic(Statistic.BOAT_ONE_CM)
                + player.getStatistic(Statistic.CLIMB_ONE_CM)
                + player.getStatistic(Statistic.FALL_ONE_CM)
                + player.getStatistic(Statistic.FLY_ONE_CM)
                + player.getStatistic(Statistic.HORSE_ONE_CM)
                + player.getStatistic(Statistic.MINECART_ONE_CM)
                + player.getStatistic(Statistic.PIG_ONE_CM)
                + player.getStatistic(Statistic.STRIDER_ONE_CM)
                + player.getStatistic(Statistic.WALK_ON_WATER_ONE_CM)
                + player.getStatistic(Statistic.WALK_UNDER_WATER_ONE_CM);
    }

}