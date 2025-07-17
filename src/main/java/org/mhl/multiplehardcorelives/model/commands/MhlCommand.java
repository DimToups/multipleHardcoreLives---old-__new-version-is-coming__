package org.mhl.multiplehardcorelives.model.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.mhl.multiplehardcorelives.controller.MhlController;

import java.util.ArrayList;
import java.util.List;

/**
 * An abstract class serving as a template to every command of the plugin.
 */
public abstract class MhlCommand implements CommandExecutor {
    /**
     * The command's controller
     */
    protected final MhlController controller;

    /**
     * A list of Strings used for building paginated responses
     */
    private final List<String> stringResponses = new ArrayList<>();

    /**
     * The number of responses by page in paginated responses
     */
    private final int itemsByPage = 5;

    /**
     * Initialise a MhlCommand class by using the plugin's controller.
     * @param controller The plugin's controller
     */
    public MhlCommand(MhlController controller){
        this.controller = controller;
    }

    /**
     * Adds a new String response to the current pool of responses
     * @param response The response to be added
     */
    protected void addStringResponse(String response){
        stringResponses.add(response);
    }

    /**
     * Adds a List of new String responses to the current pool of responses
     * @param responses The responses to be added
     */
    protected void addAllStringResponses(List<String> responses){
        stringResponses.addAll(responses);
    }

    /**
     * Sends a paginated response to the commandSender, and clears the stringResponses field afterward
     * @param commandSender The commandSender
     * @param commandName   The command name to be displayed to the sender
     * @param page          The wanted page for the response
     */
    protected void sendPaginatedResponse(CommandSender commandSender, String commandName, int page){
        if((page * itemsByPage - itemsByPage) >= stringResponses.size() || page <= 0){
            commandSender.sendMessage(ChatColor.RED + "The specified page is unavailable");
            return;
        }

        StringBuilder response = new StringBuilder();
        response.append(ChatColor.GREEN).append("==========\n").append(commandName).append(" - page ").append(page).append("\n==========\n").append(ChatColor.WHITE);

        int i = 0;
        while (i < itemsByPage && ((page - 1) * itemsByPage + i) < stringResponses.size()){
            response.append("\n").append(stringResponses.get((page - 1) * itemsByPage + i));
            i++;
        }

        response.append(ChatColor.GREEN)
                .append("\n\n==========\nPage ")
                .append(page).append(" of ")
                .append(((Double)(Math.ceil((double) stringResponses.size() / itemsByPage))).intValue());

        commandSender.sendMessage(response.toString());
        stringResponses.clear();
    }
}
