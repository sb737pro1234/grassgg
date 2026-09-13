package me.sbpro.grassggteleport.permission;

public final class Permissions {

    public static final String BASE = "grassgg.teleport";

    public static final String TP = "grassgg.teleport.tp";
    public static final String TP_COORDINATES = "grassgg.teleport.tp.coordinates";
    public static final String TP_OTHERS = "grassgg.teleport.tp.others";
    public static final String TPALL = "grassgg.teleport.tpall";
    public static final String TPALL_BYPASS = "grassgg.teleport.tpall.bypass";
    public static final String TPHERE = "grassgg.teleport.tphere";
    public static final String SPECTATE = "grassgg.teleport.spectate";

    public static final String TPA = "grassgg.teleport.tpa";
    public static final String TPAHERE = "grassgg.teleport.tpahere";
    public static final String TPACCEPT = "grassgg.teleport.tpaccept";
    public static final String TPDENY = "grassgg.teleport.tpdeny";
    public static final String TPACANCEL = "grassgg.teleport.tpacancel";
    public static final String TPTOGGLE = "grassgg.teleport.tptoggle";
    public static final String TPAUTO = "grassgg.teleport.tpauto";

    private Permissions() {
    }

    public static String time(int seconds) {
        return "grassgg.teleport.time." + seconds;
    }
}