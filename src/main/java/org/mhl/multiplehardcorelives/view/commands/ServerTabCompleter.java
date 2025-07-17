package org.mhl.multiplehardcorelives.view.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.ArrayList;
import java.util.List;

/**
 * A TabCompleter class for the command server
 */
public class ServerTabCompleter implements TabCompleter {
    /**
     * Creates an instance of ServerTabCompleter
     */
    public ServerTabCompleter(){

    }

    /**
     * Recommends options to the player when typing the command server
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
            args.add("reset");
            args.add("defaultNumberOfLives");
        }
        return args;
    }
}
