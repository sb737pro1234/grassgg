package me.sbpro.grassggsupertools.tools;

import org.bukkit.Material;

public enum SuperToolType {
    PICKAXE_SILK("Netherite Super Pickaxe (Silk Touch)", "super_pickaxe_silk", Material.NETHERITE_PICKAXE, "Pickaxe", "Breaks blocks in a 3x3 area"),
    PICKAXE_FORTUNE("Netherite Super Pickaxe (Fortune III)", "super_pickaxe_fortune", Material.NETHERITE_PICKAXE, "Pickaxe", "Breaks blocks in a 3x3 area"),
    SHOVEL_SILK("Netherite Super Shovel (Silk Touch)", "super_shovel_silk", Material.NETHERITE_SHOVEL, "Shovel", "Breaks blocks in a 3x3 area"),
    SHOVEL_FORTUNE("Netherite Super Shovel (Fortune III)", "super_shovel_fortune", Material.NETHERITE_SHOVEL, "Shovel", "Breaks blocks in a 3x3 area"),
    AXE("Netherite Super Axe", "super_axe", Material.NETHERITE_AXE, "Axe", "Breaks connected logs"),
    HOE("Netherite Super Hoe", "super_hoe", Material.NETHERITE_HOE, "Hoe", "Automatically replants crops");

    private final String displayName;
    private final String id;
    private final Material material;
    private final String shortName;
    private final String information;

    SuperToolType(String displayName, String id, Material material, String shortName, String information) {
        this.displayName = displayName;
        this.id = id;
        this.material = material;
        this.shortName = shortName;
        this.information = information;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getId() {
        return id;
    }

    public Material getMaterial() {
        return material;
    }

    public String getShortName() {
        return shortName;
    }

    public String getInformation() {
        return information;
    }
}
