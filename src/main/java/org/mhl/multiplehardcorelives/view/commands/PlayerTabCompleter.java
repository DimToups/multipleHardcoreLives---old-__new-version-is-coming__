package org.mhl.multiplehardcorelives.view.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

/**
 * A TabCompleter class for the command player
 */
public class PlayerTabCompleter implements TabCompleter {
    /**
     * Creates an instance of PlayerTabCompleter
     */
    public PlayerTabCompleter(){

    }

    /**
     * Recommends options to the player when typing the command player
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
            args.add("list");
            if(commandSender.hasPermission("admin")) {
                args.add("infos");
                args.add("setLives");
            }
            return args;
        } else if (strings.length == 2 && (strings[0].equals("infos") || strings[0].equals("setLives")) && commandSender.hasPermission("admin")) {
            for(Player player : Bukkit.getOnlinePlayers())
                args.add(player.getName());
            return args;
        }

        return args;
    }
}
