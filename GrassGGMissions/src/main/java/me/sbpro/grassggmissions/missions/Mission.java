package me.sbpro.grassggmissions.missions;

import org.bukkit.Material;
import org.bukkit.entity.EntityType;

import java.util.Set;

public class Mission {

    private final String id;
    private final String displayName;

    private final MissionCategory category;
    private final MissionFamily family;
    private final MissionType type;

    private final Set<Material> materials;
    private final EntityType entityType;

    private final int goal;

    public Mission(String id,
                   String displayName,
                   MissionCategory category,
                   MissionFamily family,
                   MissionType type,
                   Set<Material> materials,
                   EntityType entityType,
                   int goal) {

        this.id = id;
        this.displayName = displayName;

        this.category = category;
        this.family = family;
        this.type = type;

        this.materials = materials;
        this.entityType = entityType;

        this.goal = goal;
    }

    public String getId() {
        return id;
    }

    public String getDisplayName() {
        return displayName;
    }

    public MissionCategory getCategory() {
        return category;
    }

    public MissionFamily getFamily() {
        return family;
    }

    public MissionType getType() {
        return type;
    }

    public Set<Material> getMaterials() {
        return materials;
    }

    public EntityType getEntityType() {
        return entityType;
    }

    public int getGoal() {
        return goal;
    }


}