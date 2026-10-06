package me.sbpro.grassggmissions.missions;

import java.util.HashMap;
import java.util.Map;

public class PlayerMissionData {


    private final Map<Integer, PlayerMission> missions;

    private long lastReset;

    public PlayerMissionData() {

        this.lastReset = 0L;
        this.missions = new HashMap<>();

    }

    public Map<Integer, PlayerMission> getMissions() {
        return missions;
    }

    public long getLastReset() {
        return lastReset;
    }

    public void setLastReset(long lastReset) {
        this.lastReset = lastReset;
    }





}