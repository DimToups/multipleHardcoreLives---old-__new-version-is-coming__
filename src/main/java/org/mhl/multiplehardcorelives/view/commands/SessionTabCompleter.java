package org.mhl.multiplehardcorelives.view.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.TabCompleter;
import org.mhl.multiplehardcorelives.controller.MhlController;
import org.mhl.multiplehardcorelives.model.gameLogic.Player;
import org.mhl.multiplehardcorelives.model.session.SessionEvent;

import java.util.ArrayList;
import java.util.List;

/**
 * A TabCompleter class for the command session
 */
public class SessionTabCompleter implements TabCompleter {
    private final MhlController controller;

    /**
     * Creates an instance of SessionTabCompleter
     * @param controller The plugin's controller
     */
    public SessionTabCompleter(MhlController controller) {
        this.controller = controller;
    }

    /**
     * Recommends options to the player when typing the command session
     * @param commandSender The command sender
     * @param command The command
     * @param s The full sent command
     * @param strings The parameters sent from the command
     * @return The list of possible options at a precise index
     */
    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] strings) {
        List<String> args = new ArrayList<>();
        if(strings.length == 1){
            if((commandSender instanceof org.bukkit.entity.Player && commandSender.hasPermission("admin")) || commandSender instanceof ConsoleCommandSender) {
                args.add("end");
                args.add("start");
            }
            args.add("events");
        }
        if(strings.length == 2){
            if(strings[0].equals("events")) {
                if((commandSender instanceof org.bukkit.entity.Player && commandSender.hasPermission("admin")) || commandSender instanceof ConsoleCommandSender)
                    args.add("assignDeathClaim");
                args.add("revokeEventClaim");
                args.add("claimEvent");
                args.add("list");
            }
        }
        if(strings.length == 3){
            if(strings[1].equals("claimEvent")
                    && controller.getCurrentSession() != null
                    && controller.getCurrentSession().getEvents() != null)
                for(SessionEvent event : controller.getCurrentSession().getEvents())
                    args.add(String.valueOf(event.eventId));
            if(strings[1].equals("revokeEventClaim")
                    && controller.getCurrentSession() != null
                    && controller.getCurrentSession().getEvents() != null)
                for(SessionEvent event : controller.getCurrentSession().getEvents())
                    if(event.getClaimer() != null
                            && ((commandSender.hasPermission("admin") || commandSender instanceof ConsoleCommandSender)
                                ||  (commandSender.getName().equals(event.getClaimer().getName()) && commandSender.hasPermission("player"))))
                        args.add(String.valueOf(event.eventId));
        }
        if(strings.length == 3)
            if (commandSender instanceof org.bukkit.entity.Player
                    && commandSender.hasPermission("admin")
                    && strings[1].equals("assignDeathClaim")
                    && controller.getCurrentSession() != null
                    && controller.getCurrentSession().getEvents() != null)
                for(SessionEvent event : controller.getCurrentSession().getEvents())
                    args.add(String.valueOf(event.eventId));
        if(strings.length == 4)
            if (commandSender instanceof org.bukkit.entity.Player
                    && commandSender.hasPermission("admin")
                    && strings[1].equals("assignDeathClaim")
                    && controller.getCurrentSession() != null
                    && controller.getCurrentSession().getEvents() != null)
                for(Player player : controller.getServer().getPlayers())
                    args.add(player.getName());
        return args;
    }
}
