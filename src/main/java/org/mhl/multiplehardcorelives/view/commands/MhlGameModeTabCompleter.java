package org.mhl.multiplehardcorelives.view.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.mhl.multiplehardcorelives.controller.MhlController;
import org.mhl.multiplehardcorelives.model.gameModes.enums.GameModes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * A TabCompleter class for the command mhlGameMode
 */
public class MhlGameModeTabCompleter implements TabCompleter {
    /**
     * The plugin's controller
     */
    private final MhlController controller;

    /**
     * Creates an instance of MhlGameModeTabCompleter
     * @param controller The plugin's controller
     */
    public MhlGameModeTabCompleter(MhlController controller){
        this.controller = controller;
    }
    /**
     * Recommends options to the player when typing the command mhlGameMode
     * @param commandSender The command sender
     * @param command The command
     * @param s The full sent command
     * @param strings The parameters sent from the command
     * @return The list of possible options at a precise index
     */
    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] strings) {
        List<String> args = new ArrayList<>();
        if(strings.length == 1)
            for(GameModes gameMode : GameModes.values())
                args.add(gameMode.getName());
        else if (strings.length > 1 && Arrays.stream(GameModes.values()).anyMatch(g -> g.getName().equals(strings[0])))
            args.addAll(Objects.requireNonNull(GameModes.toMhlGameMode(controller, GameModes.valueOf(strings[0]))).getCommandTabCompleter(commandSender, command, s, strings));
        return args;
    }
}
