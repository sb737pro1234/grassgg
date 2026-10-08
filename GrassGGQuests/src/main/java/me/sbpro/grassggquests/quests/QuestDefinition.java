package me.sbpro.grassggquests.quests;

public class QuestDefinition {
    private final String id;
    private final String title;
    private final String description;
    private final QuestType type;
    private final String target;
    private final int requiredAmount;

    public QuestDefinition(String id, String title, String description, QuestType type, String target, int requiredAmount) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.type = type;
        this.target = target;
        this.requiredAmount = requiredAmount;
    }

    public String getId() { return id; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public QuestType getType() { return type; }
    public String getTarget() { return target; }
    public int getRequiredAmount() { return requiredAmount; }
}
