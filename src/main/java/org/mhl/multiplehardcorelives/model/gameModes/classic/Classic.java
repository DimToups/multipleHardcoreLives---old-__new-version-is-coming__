package org.mhl.multiplehardcorelives.model.gameModes.classic;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.mhl.multiplehardcorelives.model.gameModes.MhlGameMode;
import org.mhl.multiplehardcorelives.model.gameModes.enums.GameModes;
import org.mhl.multiplehardcorelives.model.lifeToken.NumericLifeToken;

import java.util.List;

/**
 * The classic GameMode for the plugin
 * Every player have 5 lives and have to be the last survivor
 */
public class Classic extends MhlGameMode {
    /**
     * Makes an instance of Classic
     */
    public Classic(){
        super(new NumericLifeToken(), new NumericLifeToken(5), new NumericLifeToken(1), GameModes.Classic);
    }

    @Override
    public void onSessionStart() {

    }

    @Override
    public void onSessionEnd() {

    }

    @Override
    public void onPlayerDeath(PlayerDeathEvent pde) {

    }

    @Override
    public List<String> getCommandTabCompleter(CommandSender commandSender, Command command, String s, String[] strings) {
        return List.of();
    }
}
