package me.sbpro.grassggquests.shop;

import org.bukkit.inventory.ItemStack;

public record ShopItem(String id, String displayName, int cost, String command, int slot, ItemStack displayItem) {}
