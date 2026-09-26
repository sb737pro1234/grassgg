package me.sbpro.grassggstaff;

import me.sbpro.grassggstaff.commands.OtherCommand;
import me.sbpro.grassggstaff.commands.chatlock.ChatLockCommand;
import me.sbpro.grassggstaff.commands.chatlock.ChatLockListener;
import me.sbpro.grassggstaff.commands.clearchat.ClearChatCommand;
import me.sbpro.grassggstaff.commands.clearchat.ClearChatListener;
import me.sbpro.grassggstaff.commands.sign.SignCommand;
import me.sbpro.grassggstaff.config.PunishmentConfig;
import me.sbpro.grassggstaff.database.DatabaseManager;
import me.sbpro.grassggstaff.database.OffenceRepository;
import me.sbpro.grassggstaff.database.PlayerRepository;
import me.sbpro.grassggstaff.listeners.PlayerIdentityListener;
import me.sbpro.grassggstaff.listeners.PunishmentChatListener;
import me.sbpro.grassggstaff.listeners.PunishmentLoginListener;
import me.sbpro.grassggstaff.offend.OffenceCommand;
import me.sbpro.grassggstaff.offend.OffendCommand;
import me.sbpro.grassggstaff.punishment.PunishmentCalculator;
import me.sbpro.grassggstaff.punishment.PunishmentEnforcementManager;
import me.sbpro.grassggstaff.punishment.PunishmentManager;
import me.sbpro.grassggstaff.reason.ReasonManager;
import me.sbpro.grassggstaff.staffchat.StaffChatCommand;
import me.sbpro.grassggstaff.staffchat.StaffChatListener;
import me.sbpro.grassggstaff.staffchat.StaffChatManager;
import me.sbpro.grassggstaff.sus.SusCommand;
import me.sbpro.grassggstaff.sus.SusManager;
import me.sbpro.grassggstaff.sus.SusMenuListener;
import me.sbpro.grassggstaff.sus.SusRepository;
import org.bukkit.plugin.java.JavaPlugin;

public final class GrassGGStaff extends JavaPlugin {

    private DatabaseManager databaseManager;
    private OffenceRepository offenceRepository;
    private PunishmentConfig punishmentConfig;
    private ReasonManager reasonManager;
    private PunishmentCalculator punishmentCalculator;
    private PunishmentManager punishmentManager;
    private PunishmentEnforcementManager enforcementManager;
    private PlayerRepository playerRepository;

    private SusRepository susRepository;
    private SusManager susManager;
    private StaffChatManager staffChatManager;

    @Override
    public void onEnable() {

        saveDefaultConfig();
        saveResource("punishments.yml", false);

        /*
         * ============================================================
         *                    CONFIGURATION
         * ============================================================
         */

        this.punishmentConfig =
                new PunishmentConfig(this);

        this.reasonManager =
                new ReasonManager(
                        this,
                        punishmentConfig
                );

        this.punishmentCalculator =
                new PunishmentCalculator();

        /*
         * ============================================================
         *                       DATABASE
         * ============================================================
         */

        this.databaseManager =
                new DatabaseManager(this);

        if (!databaseManager.start()) {

            getLogger().severe(
                    "Database startup failed. Disabling GrassGGStaff."
            );

            getServer()
                    .getPluginManager()
                    .disablePlugin(this);

            return;
        }

        /*
         * ============================================================
         *                    OFFENCE DATABASE
         * ============================================================
         */

        this.offenceRepository =
                new OffenceRepository(
                        databaseManager
                );

        if (!offenceRepository.createTables()) {

            getLogger().severe(
                    "Database schema creation failed. Disabling GrassGGStaff."
            );

            getServer()
                    .getPluginManager()
                    .disablePlugin(this);

            return;
        }

        /*
         * ============================================================
         *                    PLAYER DATABASE
         * ============================================================
         */

        this.playerRepository =
                new PlayerRepository(
                        databaseManager
                );

        if (!playerRepository.createTable()) {

            getLogger().severe(
                    "Player database table creation failed. Disabling GrassGGStaff."
            );

            getServer()
                    .getPluginManager()
                    .disablePlugin(this);

            return;
        }

        /*
         * ============================================================
         *                         SUS SYSTEM
         * ============================================================
         */

        this.susRepository =
                new SusRepository(
                        databaseManager
                );

        if (!susRepository.createTable()) {

            getLogger().severe(
                    "SUS database table creation failed. Disabling GrassGGStaff."
            );

            getServer()
                    .getPluginManager()
                    .disablePlugin(this);

            return;
        }

        this.susManager =
                new SusManager(
                        this,
                        susRepository
                );

        /*
         * ============================================================
         *                       STAFF CHAT
         * ============================================================
         */

        // Staff Chat

        this.staffChatManager =
                new StaffChatManager(this);

        staffChatManager.start();

        StaffChatCommand staffChatCommand =
                new StaffChatCommand(
                        staffChatManager
                );

        getCommand("staffchat")
                .setExecutor(staffChatCommand);

        getCommand("staffchat")
                .setTabCompleter(staffChatCommand);

        getServer()
                .getPluginManager()
                .registerEvents(
                        new StaffChatListener(
                                staffChatManager
                        ),
                        this
                );

        /*
         * ============================================================
         *                    PUNISHMENT SYSTEM
         * ============================================================
         */

        this.punishmentManager =
                new PunishmentManager(
                        this,
                        offenceRepository,
                        punishmentConfig,
                        reasonManager,
                        punishmentCalculator
                );

        OffendCommand offendCommand =
                new OffendCommand(this);

        OffenceCommand offenceCommand =
                new OffenceCommand(this);

        SusCommand susCommand =
                new SusCommand(this);

        /*
         * ============================================================
         *                      /OFFEND
         * ============================================================
         */

        getCommand("offend")
                .setExecutor(offendCommand);

        getCommand("offend")
                .setTabCompleter(offendCommand);

        /*
         * ============================================================
         *                      /OFFENCE
         * ============================================================
         */

        getCommand("offence")
                .setExecutor(offenceCommand);

        getCommand("offence")
                .setTabCompleter(offenceCommand);

        /*
         * ============================================================
         *                        /SUS
         * ============================================================
         */

        getCommand("sus")
                .setExecutor(susCommand);

        getCommand("sus")
                .setTabCompleter(susCommand);

        /*
         * ============================================================
         *                 PUNISHMENT ENFORCEMENT
         * ============================================================
         */

        this.enforcementManager =
                new PunishmentEnforcementManager(this);

        getServer()
                .getPluginManager()
                .registerEvents(
                        new PunishmentLoginListener(
                                this,
                                enforcementManager
                        ),
                        this
                );

        getServer()
                .getPluginManager()
                .registerEvents(
                        new PunishmentChatListener(
                                this,
                                enforcementManager
                        ),
                        this
                );

        /*
         * ============================================================
         *                    PLAYER IDENTITY
         * ============================================================
         */

        getServer()
                .getPluginManager()
                .registerEvents(
                        new PlayerIdentityListener(this),
                        this
                );

        /*
         * ============================================================
         *                       SUS GUI
         * ============================================================
         */

        getServer()
                .getPluginManager()
                .registerEvents(
                        new SusMenuListener(
                                this,
                                susManager
                        ),
                        this
                );

        /*
         * ============================================================
         *                      START SYSTEMS
         * ============================================================
         */

        enforcementManager.start();
        susManager.start();

        /*
         * ============================================================
         *                    OTHER COMMANDS
         * ============================================================
         */

        OtherCommand otherCommand =
                new OtherCommand(this);

        getCommand("ban")
                .setExecutor(otherCommand);

        getCommand("mute")
                .setExecutor(otherCommand);

        getCommand("warn")
                .setExecutor(otherCommand);

        getCommand("punish")
                .setExecutor(otherCommand);

        /*
         * ============================================================
         *                      CHAT LOCK
         * ============================================================
         */

        getCommand("chatlock")
                .setExecutor(
                        new ChatLockCommand()
                );

        getServer()
                .getPluginManager()
                .registerEvents(
                        new ChatLockListener(),
                        this
                );

        /*
         * ============================================================
         *                      CLEAR CHAT
         * ============================================================
         */

        ClearChatCommand clearChatCommand =
                new ClearChatCommand();

        getCommand("clearchat")
                .setExecutor(clearChatCommand);

        getServer()
                .getPluginManager()
                .registerEvents(
                        new ClearChatListener(),
                        this
                );


        getCommand("sign").setExecutor(new SignCommand());



        getLogger().info(
                "GrassGGStaff enabled."
        );
    }

    @Override
    public void onDisable() {

        if (enforcementManager != null) {
            enforcementManager.shutdown();
        }

        if (susManager != null) {
            susManager.shutdown();
        }

        if (offenceRepository != null) {
            offenceRepository.shutdown();
        }

        if (susRepository != null) {
            susRepository.shutdown();
        }

        if (databaseManager != null) {
            databaseManager.close();
        }

        if (playerRepository != null) {
            playerRepository.shutdown();
        }

        if (staffChatManager != null) {
            staffChatManager.stop();
        }
    }

    public DatabaseManager getDatabaseManager() {
        return databaseManager;
    }

    public OffenceRepository getOffenceRepository() {
        return offenceRepository;
    }

    public PunishmentConfig getPunishmentConfig() {
        return punishmentConfig;
    }

    public ReasonManager getReasonManager() {
        return reasonManager;
    }

    public PunishmentCalculator getPunishmentCalculator() {
        return punishmentCalculator;
    }

    public PunishmentManager getPunishmentManager() {
        return punishmentManager;
    }

    public PunishmentEnforcementManager getEnforcementManager() {
        return enforcementManager;
    }

    public PlayerRepository getPlayerRepository() {
        return playerRepository;
    }

    public SusRepository getSusRepository() {
        return susRepository;
    }

    public SusManager getSusManager() {
        return susManager;
    }

    public StaffChatManager getStaffChatManager() {
        return staffChatManager;
    }
}