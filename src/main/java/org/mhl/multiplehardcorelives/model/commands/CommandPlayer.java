package org.mhl.multiplehardcorelives.model.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.mhl.multiplehardcorelives.controller.MhlController;
import org.mhl.multiplehardcorelives.model.gameLogic.Player;
import org.mhl.multiplehardcorelives.model.lifeToken.NumericLifeToken;

import java.util.List;
import java.util.logging.Level;

/**
 * A command class having the logic of the command player.
 */
public class CommandPlayer extends MhlCommand {
    /**
     * Initialise a CommandPlayer by using the plugin's controller.
     * @param controller The plugin's controller
     */
    public CommandPlayer(MhlController controller) {
        super(controller);
    }

    /**
     * Detects the command mhlPlayer and asks the controller to handle it.
     * @param commandSender Source of the command
     * @param command       Command which was executed
     * @param s             Alias of the command which was used
     * @param strings       Passed command arguments
     * @return              True if a valid command, otherwise false
     */
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if(strings.length > 0){
            switch (strings[0]){
                case "list" : {
                    int page = 1;
                    if(strings.length == 2){
                        try {
                            page = Integer.parseInt(strings[1]);
                        } catch (Exception e) {
                            commandSender.sendMessage(ChatColor.RED + "Could not find the page " + strings[1]);
                            Bukkit.getLogger().log(Level.WARNING, "The command ended wrongly due to invalid page \"" + strings[1] + "\"");
                            return true;
                        }
                    }
                    List<Player> loadedPlayers, unloadedPlayers;
                    loadedPlayers = controller.getServer().getPlayers();

                    if(commandSender.hasPermission("admin"))
                        for(Player player : loadedPlayers)
                            addStringResponse("- " + player.getName() + " (" + player.isOnlineToString() + ") - " + player.getUuid());
                    else
                        for(Player player : loadedPlayers)
                            addStringResponse("- " + player.getName() + " (" + player.isOnlineToString() + ")");

                    if(commandSender.hasPermission("admin")){
                        unloadedPlayers = controller.getDatabasePlayers();
                        for(Player player : loadedPlayers)
                            unloadedPlayers.removeIf(dbPlayer -> dbPlayer.getUuid() == player.getUuid());
                        for(Player player : unloadedPlayers)
                            addStringResponse("\n- " + player.getName() + " (" + player.isOnlineToString() + ") - " + player.getUuid() + "(player is unloaded)");
                    }

                    commandSender.sendMessage("Every player of the server: ");
                    this.sendPaginatedResponse(commandSender, "Player list", page);
                    return true;
                }
                case "infos" : {
                    if(strings.length > 1 && commandSender.hasPermission("admin")){
                        this.controller.displayPlayerInformations(commandSender, strings[1]);
                        return true;
                    }
                }
                case "setLives" : {
                    if(strings.length > 2 && commandSender.hasPermission("admin")){
                        //
                        int numberOfLives;
                        Player player = controller.findPlayerSafelyByName(strings[1]);

                        if(player == null){
                            commandSender.sendMessage("Player \"" + strings[1] + "\" has not been found. He may did not connect to the server yet.");
                            return false;
                        }

                        try{
                            numberOfLives = Integer.parseInt(strings[2]);
                        } catch (Exception e){
                            commandSender.sendMessage("\"" + strings[2] + "\" is not a number");
                            return false;
                        }

                        controller.addPlayer(player);
                        if(controller.getGameMode().getLifeCurrency().getClass() == NumericLifeToken.class) {
                            controller.setNbLivesOfPlayer(player, new NumericLifeToken(numberOfLives));
                            return true;
                        }
                    }
                }
                default:
                    Bukkit.getLogger().log(Level.WARNING, "Argument \"" + strings[0] + "\" is not a valid argument");
            }
        }
        return false;
    }
}
