package me.sbpro.grassggsurvival.commands.ranks;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public class RanksCommand implements CommandExecutor {

    public static final String RANK1COLOR = "§f";
    public static final String RANK2COLOR = "§x§6§e§e§7§b§7";
    public static final String RANK3COLOR = "§x§6§0§a§5§f§a";
    public static final String RANK4COLOR = "§x§a§7§8§b§f§a";
    public static final String RANK5COLOR = "§x§f§4§7§2§b§6";
    public static final String RANK6COLOR = "§x§f§b§9§2§3§c";
    public static final String RANK7COLOR = "§x§f§d§e§0§4§7";
    public static final String RANK8COLOR = "§x§3§3§8§6§5§0";
    public static final String WHITE = "§f";
    public static final String TITLE = "§2§lRanks";

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (sender instanceof Player player){

            Inventory ranks = Bukkit.createInventory(player, 45, TITLE);

            ItemStack rank1 = new ItemStack(Material.BOOK);
            ItemStack rank2 = new ItemStack(Material.BOOK);
            ItemStack rank3 = new ItemStack(Material.BOOK);
            ItemStack rank4 = new ItemStack(Material.BOOK);
            ItemStack rank5 = new ItemStack(Material.BOOK);
            ItemStack rank6 = new ItemStack(Material.BOOK);
            ItemStack rank7 = new ItemStack(Material.BOOK);
            ItemStack rank8 = new ItemStack(Material.BOOK);


            ItemMeta rank1Meta = rank1.getItemMeta();
            rank1Meta.setDisplayName(RANK1COLOR + "Player");
            rank1Meta.setLore(List.of(
                    RANK1COLOR + "◆ " + WHITE + "Can access to 2 homes.",
                    RANK1COLOR + "◆ " + WHITE + "Can select a cosmetic tag.",
                    RANK1COLOR + "◆ " + WHITE + "Can rename items in an anvil using color codes.",
                    RANK1COLOR + "◆ " + WHITE + "Can change nickname."));
            rank1.setItemMeta(rank1Meta);

            ItemMeta rank2Meta = rank2.getItemMeta();
            rank2Meta.setDisplayName(RANK2COLOR + "Jester");
            rank2Meta.setLore(List.of(
                    RANK2COLOR + "◆ " + WHITE + "Can use chat colors.",
                    RANK2COLOR + "◆ " + WHITE + "Can clear own inventory.",
                    RANK2COLOR + "◆ " + WHITE + "Can use the /craft command."));
            rank2.setItemMeta(rank2Meta);

            ItemMeta rank3Meta = rank3.getItemMeta();
            rank3Meta.setDisplayName(RANK3COLOR + "Squire");
            rank3Meta.setLore(List.of(
                    RANK3COLOR + "◆ " + WHITE + "Can hideplayers from view.",
                    RANK3COLOR + "◆ " + WHITE + "Can use the /nightvision command.",
                    RANK3COLOR + "◆ " + WHITE + "Can use the /smithingtable command.",
                    RANK3COLOR + "◆ " + WHITE + "Can use the /stonecutter command.",
                    RANK3COLOR + "◆ " + WHITE + "Can use the /cartographytable command.",
                    RANK3COLOR + "◆ " + WHITE + "Can use the /loom command.",
                    RANK3COLOR + "◆ " + WHITE + "Can use the /rename command.",
                    RANK3COLOR + "◆ " + WHITE + "Can use the /anvil command."));
            rank3.setItemMeta(rank3Meta);

            ItemMeta rank4Meta = rank4.getItemMeta();
            rank4Meta.setDisplayName(RANK4COLOR + "Bishop");
            rank4Meta.setLore(List.of(
                    RANK4COLOR + "◆ " + WHITE + "Can set up to 3 homes.",
                    RANK4COLOR + "◆ " + WHITE + "Can use fastplace with a range of 16 blocks."));
            rank4.setItemMeta(rank4Meta);


            ItemMeta rank5Meta = rank5.getItemMeta();
            rank5Meta.setDisplayName(RANK5COLOR + "Knight");
            rank5Meta.setLore(List.of(
                    RANK5COLOR + "◆ " + WHITE + "Join and leave messages visible by all players.",
                    RANK5COLOR + "◆ " + WHITE + "Can set up to 4 homes.",
                    RANK5COLOR + "◆ " + WHITE + "Can use fastplace with a range of 32 blocks."));
            rank5.setItemMeta(rank5Meta);

            ItemMeta rank6Meta = rank6.getItemMeta();
            rank6Meta.setDisplayName(RANK6COLOR + "Lord");
            rank6Meta.setLore(List.of(
                    RANK6COLOR + "◆ " + WHITE + "Can use the /feed command.",
                    RANK6COLOR + "◆ " + WHITE + "Can set up to 5 homes.",
                    RANK6COLOR + "◆ " + WHITE + "Can use up to 4 enderchest rows.",
                    RANK6COLOR + "◆ " + WHITE + "Can use fastplace with a range of 64 blocks."));
            rank6.setItemMeta(rank6Meta);

            ItemMeta rank7Meta = rank7.getItemMeta();
            rank7Meta.setDisplayName(RANK7COLOR + "King");
            rank7Meta.setLore(List.of(
                    RANK7COLOR + "◆ " + WHITE + "Can use the /heal command.",
                    RANK7COLOR + "◆ " + WHITE + "Can set up to 6 homes.",
                    RANK7COLOR + "◆ " + WHITE + "Can use up to 5 enderchest rows.",
                    RANK7COLOR + "◆ " + WHITE + "Can use fastplace with a range of 128 blocks."));
            rank7.setItemMeta(rank7Meta);

            ItemMeta rank8Meta = rank8.getItemMeta();
            rank8Meta.setDisplayName(RANK8COLOR + "Grass");
            rank8Meta.setLore(List.of(
                    RANK8COLOR + "◆ " + WHITE + "Can set up to 7 homes.",
                    RANK8COLOR + "◆ " + WHITE + "Can use up to 6 enderchest rows."));
            rank8.setItemMeta(rank8Meta);

            ranks.setItem(10, rank1);
            ranks.setItem(12, rank2);
            ranks.setItem(14, rank3);
            ranks.setItem(16, rank4);
            ranks.setItem(28, rank5);
            ranks.setItem(30, rank6);
            ranks.setItem(32, rank7);
            ranks.setItem(34, rank8);

            player.openInventory(ranks);

        } else {
            sender.sendMessage("§cOnly players can use this command!");
        }
        return true;
    }
}