package org.mhl.multiplehardcorelives.model.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.mhl.multiplehardcorelives.controller.MhlController;
import org.mhl.multiplehardcorelives.model.gameModes.enums.GameModes;

import java.util.Arrays;

/**
 * A command class having the logic of the command mhlGameMode
 */
public class CommandMhlGameMode extends MhlCommand{
    /**
     * Initialise a CommandMhlGameMode by using the plugin's controller.
     * @param controller The plugin's controller
     */
    public CommandMhlGameMode(MhlController controller) {
        super(controller);
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if(strings.length == 0) {
            commandSender.sendMessage("The current GameMode is " + controller.getGameMode().getGameMode());
            return true;
        }
        else if (strings.length == 1) {
            try{
                GameModes gameMode = GameModes.valueOf(strings[0]);
                controller.setGameMode(gameMode, commandSender);
                return true;
            } catch (Exception e){
                commandSender.sendMessage("The argument " + strings[0] + " is not a valid GameMode");
            }
        }
        else if (strings.length == 2 && Arrays.stream(GameModes.values()).anyMatch(g -> g.getName().equals(strings[0]))){
            if(strings[0].equalsIgnoreCase("impostor")) {
                if (strings[1].equals("showImpostor")) {
                    controller.showImpostor(commandSender);
                    return true;
                }
            }
        }
        return false;
    }
}
