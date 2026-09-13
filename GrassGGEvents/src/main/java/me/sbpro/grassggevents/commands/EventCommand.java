package me.sbpro.grassggevents.commands;

import me.sbpro.grassggevents.GrassGGEvents;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class EventCommand implements CommandExecutor {

    private final GrassGGEvents plugin;

    public EventCommand(GrassGGEvents plugin) {
        this.plugin = plugin;
    }


    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {


        if (args.length == 0) {
            sender.sendMessage("§cUsage: /event sumo <pos1|pos2|save|start|end>");
            return true;
        }


        if (args[0].equalsIgnoreCase("sumo")) {


            if (args.length < 2) {
                sender.sendMessage("§cUsage: /event sumo <pos1|pos2|save|start|end>");
                return true;
            }


            switch (args[1].toLowerCase()) {


                case "pos1":

                    if (!(sender instanceof Player player)) {
                        sender.sendMessage("§cOnly players can do this.");
                        return true;
                    }

                    plugin.getSumoManager().setPos1(player.getLocation());

                    player.sendMessage("§aSumo position 1 set.");
                    break;



                case "pos2":

                    if (!(sender instanceof Player player)) {
                        sender.sendMessage("§cOnly players can do this.");
                        return true;
                    }

                    plugin.getSumoManager().setPos2(player.getLocation());

                    player.sendMessage("§aSumo position 2 set.");
                    break;



                case "save":

                    plugin.getSumoManager().saveArena();

                    sender.sendMessage("§aSaved Sumo arena.");
                    break;



                case "start":

                    plugin.getSumoManager().start();

                    sender.sendMessage("§aStarted Sumo.");
                    break;



                case "end":
                    if (!plugin.getSumoManager().isRunning()) {
                        sender.sendMessage("§cThere isn't a Sumo event running.");
                        return true;
                    }

                    plugin.getSumoManager().end();

                    sender.sendMessage("§cEnded Sumo.");
                    break;



                default:

                    sender.sendMessage("§cUnknown option.");

            }

            return true;
        }


        sender.sendMessage("§cUnknown event.");
        return true;
    }
}