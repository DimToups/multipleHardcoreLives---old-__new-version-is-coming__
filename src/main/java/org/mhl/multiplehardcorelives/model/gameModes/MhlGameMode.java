package org.mhl.multiplehardcorelives.model.gameModes;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.mhl.multiplehardcorelives.controller.MhlController;
import org.mhl.multiplehardcorelives.model.gameModes.enums.GameModes;
import org.mhl.multiplehardcorelives.model.lifeToken.LifeToken;

import java.util.List;

/**
 * An abstract class to make custom GameModes
 */
public abstract class MhlGameMode {

    /**
     * The used LifeToken to represent player's lives
     */
    protected final LifeToken lifeCurrency;

    /**
     * The default death penalty in LifeTokens for players who died
     */
    protected LifeToken defaultDeathPenalty;

    /**
     * The default number of LifeTokens for players when they first play to the plugin
     */
    protected LifeToken defaultNbLifeTokens;

    /**
     * The associated GameModes enum instance
     */
    protected GameModes gameMode;

    /**
     * The plugin's controller.
     * It is used for communicating with the plugin
     */
    protected MhlController controller;

    /**
     * Instantiates a MhlGameMode from a sub-class
     * @param lifeCurrency            The used life currency
     * @param defaultNbLifeTokens     The default number of LifeTokens for players when they first play to the plugin
     * @param defaultSanctionForDeath The default death penalty in LifeTokens for players who died
     * @param gameMode                The associated GameModes enum instance
     */
    public MhlGameMode(LifeToken lifeCurrency, LifeToken defaultNbLifeTokens, LifeToken defaultSanctionForDeath, GameModes gameMode){
        this.lifeCurrency = lifeCurrency;
        this.defaultNbLifeTokens = defaultNbLifeTokens;
        this.defaultDeathPenalty = defaultSanctionForDeath;
        this.gameMode = gameMode;
    }

    /**
     * Instantiates a MhlGameMode from a sub-class
     * @param controller              The plugin's controller
     * @param lifeCurrency            The used life currency
     * @param defaultNbLifeTokens     The default number of LifeTokens for players when they first play to the plugin
     * @param defaultSanctionForDeath The default death penalty in LifeTokens for players who died
     * @param gameMode                The associated GameModes enum instance
     */
    public MhlGameMode(MhlController controller, LifeToken lifeCurrency, LifeToken defaultNbLifeTokens, LifeToken defaultSanctionForDeath, GameModes gameMode){
        this.controller = controller;
        this.lifeCurrency = lifeCurrency;
        this.defaultNbLifeTokens = defaultNbLifeTokens;
        this.defaultDeathPenalty = defaultSanctionForDeath;
        this.gameMode = gameMode;
    }

    /**
     * Sends the GameMode's death penalty
     * @return The GameMode's death penalty
     */
    public LifeToken getDeathPenalty(){
        return this.defaultDeathPenalty;
    }

    /**
     * Sends the GameMode's life currency
     * @return The GameMode's life currency
     */
    public LifeToken getLifeCurrency() {
        return lifeCurrency;
    }

    /**
     * Sends the GameMode's GameModes enum instance
     * @return The GameMode's GameModes enum instance
     */
    public GameModes getGameMode(){
        return this.gameMode;
    }

    /**
     * Sends the GameMode's default number of life tokens
     * @return The GameMode's default number of life tokens
     */
    public LifeToken getDefaultNbLifeTokens(){
        return this.defaultNbLifeTokens;
    }

    /**
     * An abstract method for handling additional logics after the start of a session
     */
    public abstract void onSessionStart();

    /**
     * An abstract method for handling additional logics after the end of a session
     */
    public abstract void onSessionEnd();

    /**
     * An abstract method for handling the consequences of a player's death
     * @param pde The associated PlayerDeathEvent
     */
    public abstract void onPlayerDeath(PlayerDeathEvent pde);

    /**
     * Recommends options to the player when typing the command mhlGameMode to configure it
     * @param commandSender The command sender
     * @param command The command
     * @param s The full sent command
     * @param strings The parameters sent from the command
     * @return The list of possible options at a precise index
     */
    public abstract List<String> getCommandTabCompleter(CommandSender commandSender, Command command, String s, String[] strings);
}
