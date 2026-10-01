
package me.sbpro.grassggsurvival.enchants;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;

import java.util.*;

public class AutoSmeltEnchant implements Listener {
    private final NamespacedKey key;

    public AutoSmeltEnchant(Plugin plugin){
        key=new NamespacedKey(plugin,"autosmelt");
        Bukkit.getPluginManager().registerEvents(this,plugin);
    }

    public void apply(ItemStack item){
        if(item==null||item.getType().isAir()) return;
        if(!item.getType().name().endsWith("_PICKAXE")) return;
        if(hasEnchant(item)) return;
        ItemMeta meta=item.getItemMeta();
        List<String> lore=meta.hasLore()?new ArrayList<>(meta.getLore()):new ArrayList<>();
        lore.add("§6AutoSmelt");
        meta.setLore(lore);
        meta.getPersistentDataContainer().set(key, PersistentDataType.BYTE,(byte)1);
        item.setItemMeta(meta);
    }

    public boolean hasEnchant(ItemStack item){
        return item!=null&&item.hasItemMeta()&&item.getItemMeta().getPersistentDataContainer().has(key,PersistentDataType.BYTE);
    }

    @EventHandler(ignoreCancelled=true)
    public void onBreak(BlockBreakEvent e){
        Player p=e.getPlayer();
        ItemStack tool=p.getInventory().getItemInMainHand();
        if(!hasEnchant(tool)) return;
        if(!tool.getType().name().endsWith("_PICKAXE")) return;
        if(tool.containsEnchantment(Enchantment.SILK_TOUCH)) return;

        Block b=e.getBlock();
        Collection<ItemStack> drops=b.getDrops(tool,p);

        List<ItemStack> result=new ArrayList<>();
        boolean modified=false;

        for(ItemStack drop:drops){
            switch(drop.getType()){
                case RAW_IRON -> {result.add(new ItemStack(Material.IRON_INGOT,drop.getAmount())); modified=true;}
                case RAW_GOLD -> {result.add(new ItemStack(Material.GOLD_INGOT,drop.getAmount())); modified=true;}
                case RAW_COPPER -> {result.add(new ItemStack(Material.COPPER_INGOT,drop.getAmount())); modified=true;}
                case RAW_IRON_BLOCK -> {result.add(new ItemStack(Material.IRON_INGOT,drop.getAmount()*9)); modified=true;}
                case RAW_GOLD_BLOCK -> {result.add(new ItemStack(Material.GOLD_INGOT,drop.getAmount()*9)); modified=true;}
                case RAW_COPPER_BLOCK -> {result.add(new ItemStack(Material.COPPER_INGOT,drop.getAmount()*9)); modified=true;}
                case ANCIENT_DEBRIS -> {result.add(new ItemStack(Material.NETHERITE_SCRAP,drop.getAmount())); modified=true;}
                default -> result.add(drop.clone());
            }
        }

        if(!modified) return;

        e.setDropItems(false);
        for(ItemStack is:result){
            b.getWorld().dropItemNaturally(b.getLocation(),is);
        }
        b.getWorld().spawn(b.getLocation(), ExperienceOrb.class).setExperience(3);
    }
}
