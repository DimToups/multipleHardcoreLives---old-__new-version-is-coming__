package org.mhl.multiplehardcorelives;

import org.bukkit.plugin.java.JavaPlugin;
import org.mhl.multiplehardcorelives.controller.MhlController;
import org.mhl.multiplehardcorelives.model.commands.*;
import org.mhl.multiplehardcorelives.model.commands.CommandVerify;
import org.mhl.multiplehardcorelives.view.commands.*;
import org.mhl.multiplehardcorelives.model.commands.CommandMhlWorldBorder;
import org.mhl.multiplehardcorelives.model.commands.CommandServer;
import org.mhl.multiplehardcorelives.model.commands.CommandSession;

/**
 * The main class of the plugin.
 */
public final class MultipleHardcoreLives extends JavaPlugin {
    /**
     * Creates an instance of MultipleHardcoreLives
     */
    public MultipleHardcoreLives(){

    }

    /**
     * The plugin's controller. It will manage almost every aspect of the plugin once it's initialised.
     */
    private MhlController controller;

    /**
     *  Initialises the plugin by initialising its controller and loading every custom command.
     */
    @Override
    public void onEnable() {
        //
        controller = new MhlController(this);

        //
        this.loadCommands();
    }

    /**
     *  Loads every custom command from the plugin.
     *  Each command need to be added manually in this method for it to be loaded.
     */
    private void loadCommands(){
        this.getCommand("writeChanges").setExecutor(new CommandWriteChanges(controller));

        this.getCommand("verify").setExecutor(new CommandVerify(controller));
        this.getCommand("verify").setTabCompleter(new VerifyTabCompleter());

        this.getCommand("player").setExecutor(new CommandPlayer(controller));
        this.getCommand("player").setTabCompleter(new PlayerTabCompleter());

        this.getCommand("server").setExecutor(new CommandServer(controller));
        this.getCommand("server").setTabCompleter(new ServerTabCompleter());

        this.getCommand("session").setExecutor(new CommandSession(controller));
        this.getCommand("session").setTabCompleter(new SessionTabCompleter(controller));

        this.getCommand("mhlWorldBorder").setExecutor(new CommandMhlWorldBorder(controller));
        this.getCommand("mhlWorldBorder").setTabCompleter(new MhlWorldBorderTabCompeter());

        this.getCommand("mhlGameMode").setExecutor(new CommandMhlGameMode(controller));
        this.getCommand("mhlGameMode").setTabCompleter(new MhlGameModeTabCompleter(controller));
    }

    /**
     *  Disable the plugin. It asks the controller to end properly the plugin.
     */
    @Override
    public void onDisable() {
        controller.serverClosing();
    }
}
