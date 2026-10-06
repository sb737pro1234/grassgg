package me.sbpro.grassggmissions.managers;

import me.sbpro.grassggmissions.missions.Mission;
import me.sbpro.grassggmissions.missions.MissionCategory;
import me.sbpro.grassggmissions.missions.MissionFamily;
import me.sbpro.grassggmissions.missions.MissionRegistry;

import java.util.*;

public class MissionGenerator {

    private static final Random RANDOM = new Random();

    public static List<Mission> generateDailyMissions() {

        List<Mission> available = new ArrayList<>(MissionRegistry.getMissions());
        Collections.shuffle(available);

        List<Mission> selected = new ArrayList<>();

        Set<MissionCategory> usedCategories = new HashSet<>();
        Set<MissionFamily> usedFamilies = new HashSet<>();

        for (Mission mission : available) {

            if (selected.size() >= 4) {
                break;
            }

            if (usedCategories.contains(mission.getCategory())) {
                continue;
            }

            if (usedFamilies.contains(mission.getFamily())) {
                continue;
            }

            selected.add(mission);

            usedCategories.add(mission.getCategory());
            usedFamilies.add(mission.getFamily());

        }

        return selected;

    }
    public static Map<Integer, Mission> generateMissionMap() {

        List<Mission> generated = generateDailyMissions();

        Map<Integer, Mission> map = new HashMap<>();

        for (int i = 0; i < generated.size(); i++) {
            map.put(i + 1, generated.get(i));
        }

        return map;

    }
}