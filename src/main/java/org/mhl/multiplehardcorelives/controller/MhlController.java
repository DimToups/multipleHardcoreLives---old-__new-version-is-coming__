package org.mhl.multiplehardcorelives.controller;

import org.bukkit.*;
import org.bukkit.command.CommandSender;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerAdvancementDoneEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.scheduler.BukkitRunnable;
import org.mhl.multiplehardcorelives.MultipleHardcoreLives;
import org.mhl.multiplehardcorelives.model.PlayerListener;
import org.mhl.multiplehardcorelives.model.database.DatabaseHandler;
import org.mhl.multiplehardcorelives.model.gameLogic.Player;
import org.mhl.multiplehardcorelives.model.gameLogic.Server;
import org.mhl.multiplehardcorelives.model.gameModes.MhlGameMode;
import org.mhl.multiplehardcorelives.model.gameModes.enums.GameModes;
import org.mhl.multiplehardcorelives.model.gameModes.impostor.Impostor;
import org.mhl.multiplehardcorelives.model.lifeToken.LifeToken;
import org.mhl.multiplehardcorelives.model.session.Session;
import org.mhl.multiplehardcorelives.model.session.SessionEvent;
import org.mhl.multiplehardcorelives.model.session.SessionManager;
import org.mhl.multiplehardcorelives.view.PlayerCommunicator;
import org.mhl.multiplehardcorelives.view.PlayerList;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

/**
 * The plugin's controller. It handles almost every aspect of the plugin's life.
 */
public class MhlController {
    /**
     * The MultipleHardcoreLives current running plugin instance.
     */
    private final MultipleHardcoreLives plugin;

    /**
     * The current running server model.
     */
    private final Server server;

    /**
     * The plugin's session manager. It will handle events from its session.
     */
    private final SessionManager sessionManager;

    /**
     * A model made to interact with the plugin's database.
     */
    private final DatabaseHandler databaseHandler;

    /**
     * The plugin's custom scoreboard
     */
    private final PlayerList playerList;

    /**
     * The event listener about players data.
     */
    private final PlayerListener playerListener;

    /**
     * The controller's view for displaying information to players
     */
    private final PlayerCommunicator playerCommunicator;

    private MhlGameMode gameMode;

    /**
     * Creates a MhlController that will initialise its databaseHandler, server, and sessionManager.
     * @param plugin The MultipleHardcoreLives current running plugin instance.
     */
    public MhlController(MultipleHardcoreLives plugin){
        Bukkit.getLogger().log(Level.INFO, "Initialisation of the plugin...");
        //
        this.plugin = plugin;

        //
        playerListener = new PlayerListener(this);
        PluginManager pm = Bukkit.getServer().getPluginManager();
        pm.registerEvents(playerListener, plugin);

        //
        databaseHandler = new DatabaseHandler(this, plugin.getDataFolder().getAbsolutePath());
        if(!plugin.getDataFolder().exists())
            plugin.getDataFolder().mkdirs();
        databaseHandler.createDatabase();
        //
        this.gameMode = GameModes.toMhlGameMode(this, databaseHandler.lastPlayedGameMode());
        //
        Server foundServer = databaseHandler.findServer(Bukkit.getServer().getName());
        if(foundServer == null) {
            server = new Server(Bukkit.getServer().getName(), this.gameMode.getDefaultNbLifeTokens());
        }
        else {
            server = foundServer;
        }

        //
        try{
            World w = Bukkit.getWorld("world");
            if(w.getWorldBorder().getSize() > 0 && server.getWorldBorderLength() == 0)
                server.setWorldBorderLength((int)w.getWorldBorder().getSize());
        } catch (Exception e){
            Bukkit.getLogger().log(Level.WARNING, "Could not set the world border length correctly");
        }
        this.reloadWorldBorder();

        //
        this.sessionManager = new SessionManager(databaseHandler.getNbOfPreviousSessions(), this);
        this.playerCommunicator = new PlayerCommunicator();
        this.playerList = new PlayerList(this);
    }

    /**
     * Resets the server by ending the session if its running, and by setting the default number of lives back to 5.
     */
    public void resetServer(){
        Bukkit.getLogger().log(Level.WARNING, "Resetting the server's data.");
        if(this.sessionManager.isSessionActive())
            this.sessionManager.endSession();
        this.setDefaultNumberOfLives(this.gameMode.getDefaultNbLifeTokens());
    }

    /**
     * Starts the session by telling it to the sessionManager.
     */
    public void startSession(){
        if(!sessionManager.isSessionActive()) {
            gameMode.onSessionStart();
            playerCommunicator.tellSessionStart();
        }
        sessionManager.startSession();
    }

    /**
     * Ends the session by telling it to the sessionManager and by asking to write changes into the database.
     */
    public void endSession(){
        if(sessionManager.areMajorEventsAllowed()) {
            this.playerCommunicator.tellSessionNearlyEnded();
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    try{
                        sessionManager.stopMajorEvents();
                        TimeUnit.SECONDS.sleep(20);
                        playerCommunicator.tellTimeLeft(10);
                        TimeUnit.SECONDS.sleep(10);
                        gameMode.onSessionEnd();
                        sessionManager.endSession();
                    } catch(Exception e){
                        Bukkit.getLogger().log(Level.WARNING, "Timer could not wait for 30 seconds:\n" + e);
                    } finally {
                        playerCommunicator.tellSessionEnd();
                        writeChanges();
                    }

                }
            });
            thread.start();
        }
        else
            Bukkit.getLogger().warning("Session has already stopped, or cannot be stopped");
    }

    /**
     * Handle the event of a player joining the server. it will find the player in the database then it will add it to this Server instance.
     * @param player The player who has joined the server.
     */
    public void playerJoin(org.bukkit.entity.Player player){
        Player newPlayer = findPlayerSafelyByUUID(player.getUniqueId());
        if(newPlayer == null){
            Bukkit.getLogger().log(Level.INFO, "The player " + player.getName() + " with the UUID " + player.getUniqueId() + " has may not connected yet on the server. Instantiating a new Player...");
            newPlayer = new Player(player.getUniqueId(), player.getName(), this.server.getDefaultNbLivesTokens());
        }
        else
            Bukkit.getLogger().log(Level.INFO, "The player " + newPlayer.getName() + " with the UUID " + player.getUniqueId() + " has been found");
        if(sessionManager.isSessionActive())
            sessionManager.playerJoined(player);
        addPlayer(newPlayer);
        newPlayer.setToOnline();
        Bukkit.getLogger().log(Level.INFO, "Player " + player.getName() + " with the UUID " + player.getUniqueId() + " is now registered as online.");
        this.playerList.updatePlayerList();
    }

    /**
     * Adds a new player into the server by searching the Bukkit Player instance into the database and by sending the new player to the server if it does not contain it already.
     * @param player The Bukkit Player instance to add into the server.
     */
    public void addPlayer(org.bukkit.entity.Player player){
        Player newPlayer =  findPlayerSafelyByUUID(player.getUniqueId());
        if(newPlayer != null && !server.hasPlayerWithUUID(newPlayer.getUuid()))
            server.addPlayer(newPlayer);
    }

    /**
     * Adds a new player into the server by sending the new player to the server if it does not contain it already.
     * @param newPlayer The new player instance to add into the server.
     */
    public void addPlayer(Player newPlayer) {
        if(!server.hasPlayerWithUUID(newPlayer.getUuid()))
            server.addPlayer(newPlayer);
    }

    /**
     * Sets a number of lifeTokens to a specified player.
     * @param player The targeted player.
     * @param lifeTokens  The wanted number of lifeTokens.
     */
    public void setNbLivesOfPlayer(Player player, LifeToken lifeTokens){
        if(player.isOnline() && player.getLivesTokens().isNull() && !lifeTokens.isNull()){
            Bukkit.getLogger().log(Level.INFO, "Resurrecting " + player.getName() +"...");
            try{
                if(sessionManager.isSessionActive()) {
                    this.sessionManager.playerResurrected(player);
                }
                BukkitRunnable tellResurrectionToPlayer = new BukkitRunnable() {
                    @Override
                    public void run() {
                        org.bukkit.entity.Player bPlayer = Bukkit.getPlayer(player.getUuid());
                        try{
                            bPlayer.sendTitle("", ChatColor.RED + "You are getting resurrected...", 20 / 2, 20 * 4, 20 /2);
                            TimeUnit.SECONDS.sleep(5);
                        } catch (Exception e){
                            Bukkit.getLogger().log(Level.WARNING, "The bukkit runnable could not inform correctly the player about their resurrection :\n" + e);
                        } finally {
                            bPlayer.teleport(bPlayer.getRespawnLocation() != null ? bPlayer.getRespawnLocation() : Bukkit.getServer().getWorld("world").getSpawnLocation());
                            Objects.requireNonNull(Bukkit.getPlayer(player.getUuid())).setGameMode(GameMode.SURVIVAL);
                        }
                    }
                };
                tellResurrectionToPlayer.run();
            } catch (Exception e){
                Bukkit.getLogger().log(Level.WARNING, "Could not resurrect player " + player.getName() + ". You may have to set its gameMode to survival manually.\n" + e);
            }
        }
        player.setLivesTokens(lifeTokens);
        Bukkit.getLogger().log(Level.INFO, "Player \"" + player.getName() + "\" has now " + lifeTokens + " lifeTokens");
        this.playerList.updatePlayerNumberOfLives(player);
        if(lifeTokens.isSuperior(server.getDefaultNbLivesTokens()))
            Bukkit.getLogger().log(Level.WARNING, "Player \"" + player.getName() + "\" has more lifeTokens than the default number of " + server.getDefaultNbLivesTokens());
    }

    /**
     * Reduces the number of lives by one to a specified player.
     * @param bukkitPlayer The Bukkit Player instance which lost a life.
     */
    public void decrementLivesOfPlayer(org.bukkit.entity.Player bukkitPlayer){
        //
        if(!this.sessionManager.isSessionActive()){
            Bukkit.getLogger().warning("Tried to decrement number of lives of a player while the session has not started yet");
            return;
        }

        //
        Player deadPlayer = findPlayerInServer(bukkitPlayer.getUniqueId());

        //
        if(deadPlayer == null){
            Bukkit.getLogger().warning("Player with the UUID " + bukkitPlayer.getUniqueId() + " is not in the MhlController's Server instance");
            return;
        }

        //
        this.setNbLivesOfPlayer(deadPlayer, deadPlayer.getLivesTokens().minus(this.gameMode.getDeathPenalty()));

        //
        if(deadPlayer.getLivesTokens().isNull())
            definitiveKill(deadPlayer, bukkitPlayer);
    }

    /**
     * Finds a Player instance inside the constroller's server field by searching the player with its UUID.
     * @param playerUUID The player's UUID
     * @return The corresponding Player instance
     */
    @Nullable
    private Player findPlayerInServer(UUID playerUUID) {
        for(Player player : server.getPlayers())
            if(Objects.equals(player.getUuid(), playerUUID))
                return player;
        return null;
    }

    /**
     * Kills permanently a player by setting its game mode to spectator
     * @param player       The dead player.
     * @param bukkitPlayer The dead Bukkit player.
     */
    private void definitiveKill(Player player, org.bukkit.entity.Player bukkitPlayer){
        playerCommunicator.informPlayerDefinitiveDeath(bukkitPlayer);
        bukkitPlayer.setGameMode(GameMode.SPECTATOR);
        Bukkit.getLogger().log(Level.INFO, player.getName() + " has definitively died");
    }

    /**
     * Sends the plugin.
     * @return The plugin.
     */
    public Plugin getPlugin(){
        return this.plugin;
    }

    /**
     * Closes the server by ending the current session.
     */
    public void serverClosing() {
        Bukkit.getLogger().log(Level.INFO, "Closing the server...");
        if(!sessionManager.isSessionActive())
            this.writeChanges();
        else
            this.endSession();
    }

    /**
     * Sets the default number of lives to every player who has ever joined the server only if the session is not running.
     * @param defaultNbLifeToken The wanted default number of lives.
     */
    public void setDefaultNumberOfLives(LifeToken defaultNbLifeToken) {
        if(sessionManager.isSessionActive()){
            Bukkit.getLogger().log(Level.WARNING, "Cannot change the default number of lives of the server, the session is still running.");
            return;
        }
        this.databaseHandler.setNumberOfLivesToEveryPlayer(defaultNbLifeToken);
        this.server.setDefaultNbLivesTokens(defaultNbLifeToken);
    }

    /**
     * Write every change into the database by asking the databaseHandler to do it.
     */
    public void writeChanges() {
        this.databaseHandler.writeChanges(this.server);
    }

    /**
     * Finds a player by searching it with its name.
     * @param name The player's name.
     * @return     The corresponding player.
     */
    @Nullable
    public Player findPlayerSafelyByName(String name) {
        for(Player player : server.getPlayers())
            if(Objects.equals(player.getName(), name))
                return player;
        return findPlayerInDatabaseByName(name);
    }

    /**
     * Finds a player by searching it with its name.
     * @param playerUUID The player's UUID.
     * @return           The corresponding player.
     */
    @Nullable
    public Player findPlayerSafelyByUUID(UUID playerUUID) {
        for(Player player : server.getPlayers())
            if(Objects.equals(player.getUuid(), playerUUID))
                return player;
        return findPlayerInDatabaseByUUID(playerUUID);
    }

    /**
     * Finds a player by asking the databaseHandler to search it with the player's name.
     * @param name The player's name.
     * @return     The wanted player.
     */
    @Nullable
    public Player findPlayerInDatabaseByName(String name) {
        return databaseHandler.findPlayerByName(name);
    }

    /**
     * Finds a player by asking the databaseHandler to search it with the player's name.
     * @param playerUUID The player's name.
     * @return           The wanted player.
     */
    @Nullable
    public Player findPlayerInDatabaseByUUID(UUID playerUUID) {
        return databaseHandler.findPlayerByUUID(playerUUID);
    }

    /**
     * Displays information about a specified player like its number of lives to the entity who called the method.
     * @param commandSender The sender of the command.
     * @param playerName    The wanted player's name.
     */
    public void displayPlayerInformations(CommandSender commandSender, String playerName) {
        Player player = findPlayerSafelyByName(playerName);

        if(player == null){
            commandSender.sendMessage("Player \"" + playerName + "\" has not been found.");
            return;
        }

        commandSender.sendMessage("Player \"" + playerName + "\" has " + player.getLivesTokens() + " lives.");
    }

    /**
     * Handle the event of a player quitting the server. It will find the player in this Server instance and set its state to offline.
     * @param player The gone player.
     */
    public void playerQuit(org.bukkit.entity.Player player) {
        Player gonePlayer = findPlayerSafelyByUUID(player.getUniqueId());
        if(gonePlayer == null) {
            Bukkit.getLogger().log(Level.WARNING, "Player " + player.getName() + " with the UUID " + player.getUniqueId() + " is null. Could not register the player as offline");
            return;
        }
        if(sessionManager.isSessionActive())
            sessionManager.playerQuit(player);
        gonePlayer.setToOffline();
        Bukkit.getLogger().log(Level.INFO, "Player " + player.getName() + " with the UUID " + player.getUniqueId() + " is now registered as offline");
    }

    /**
     * Verifies if the server's information is coherent.
     */
    public void verifyServerState() {
        Bukkit.getLogger().log(Level.INFO, "Starting the verification of the server's informations...");
        List<Player> players = server.getPlayers();
        for(int i = 0; i < players.size(); i++){
            for (int j = i + 1; j < players.size(); j++){
                if(players.get(i).getUuid() == players.get(j).getUuid() && Objects.equals(players.get(i).getName(), players.get(j).getName()) && players.get(i).getLivesTokens() == players.get(j).getLivesTokens())
                    Bukkit.getLogger().log(Level.WARNING, "Two registered players have the same UUID, name, and number of lives. They both have this identity :\n\t" + players.get(i).getName() + " (" + players.get(i).getUuid() + ") : " + players.get(i).getLivesTokens() + " lives");
                if(players.get(i).getUuid() == players.get(j).getUuid())
                    Bukkit.getLogger().log(Level.WARNING, "Two registered players have the same UUID. Their name are " + players.get(i).getName() + " and " + players.get(j).getName());
                if(Objects.equals(players.get(i).getName(), players.get(j).getName()))
                    Bukkit.getLogger().log(Level.WARNING, "Two registered players have the same name. Their name are " + players.get(i).getName() + " and their UUID are " + players.get(i).getUuid() + ", and " + players.get(j).getUuid());
            }
        }
        Bukkit.getLogger().log(Level.INFO, "Finished the verification of the server's players");
        if(!Objects.equals(server.getAddress(), Bukkit.getServer().getName()))
            Bukkit.getLogger().log(Level.WARNING, "The MhlController's Server instance and the current server have not the same address. The MhlController's Server instance has for address \"" + server.getAddress() + "\" and the current server has for address + \"" + Bukkit.getServer().getName() + "\"");
        Bukkit.getLogger().log(Level.INFO, "Finished the verification of the server's address");
        if(server.getDefaultNbLivesTokens().isNull())
            Bukkit.getLogger().log(Level.WARNING, "The server have a non positive default number of lives. It has " + server.getDefaultNbLivesTokens() + " as a default number of lives");
        Bukkit.getLogger().log(Level.INFO, "Finished the verification of the server's default number of lives");
        Bukkit.getLogger().log(Level.INFO, "Finished the verification of the server");
    }

    /**
     * Displays to the command sender the default number of lives
     * @param sender The command sender
     */
    public void displayDefaultNumberOfLives(CommandSender sender) {
        sender.sendMessage("Default number of lives is set to " + server.getDefaultNbLivesTokens());
    }

    /**
     * Sets the world border length to the parameter's value
     * @param length The wanted world border length
     */
    public void setWorldBorderLength(int length){
        Bukkit.getLogger().log(Level.INFO, "Setting the new world border length to " + length + " blocks");
        try{
            World surface = Bukkit.getWorld("world");
            WorldBorder ws = surface.getWorldBorder();
            ws.setCenter(surface.getSpawnLocation());
            ws.setSize(length);

            World nether = Bukkit.getWorld("world_nether");
            WorldBorder wn = nether.getWorldBorder();
            wn.setCenter(new Location(nether, nether.getSpawnLocation().getX() / 8, nether.getSpawnLocation().getY() / 8, nether.getSpawnLocation().getZ()));
            wn.setSize(length);

            this.server.setWorldBorderLength(length);
            Bukkit.getLogger().log(Level.INFO, "World border length has been set to " + length + " block");
        } catch (Exception e) {
            Bukkit.getLogger().log(Level.SEVERE, "Could not set the world border length\n" + e);
        }
    }

    /**
     * Sends the actual world border length
     * @return The actual world border length
     */
    public Double getWorldBorderLength() {
        try{
            return Bukkit.getWorld("world").getWorldBorder().getSize();
        } catch (Exception e) {
            Bukkit.getLogger().log(Level.SEVERE, "Could not set the world border length\n" + e);
        }
        return null;
    }

    /**
     * Reloads the world border with the server's parameters
     */
    public void reloadWorldBorder() {
        this.setWorldBorderLength(this.server.getWorldBorderLength());
    }

    /**
     * Handles the event of a player dying
     * @param pde The event
     */
    public void playerDeath(PlayerDeathEvent pde) {
        this.decrementLivesOfPlayer(pde.getEntity());
        if(sessionManager.isSessionActive()){
            this.sessionManager.playerDied(pde);
            this.gameMode.onPlayerDeath(pde);
            if(Objects.requireNonNull(findPlayerSafelyByUUID(pde.getEntity().getUniqueId())).getLivesTokens().isNull()) {
                this.sessionManager.definitivePlayerDeath(pde);
            }
        }
    }

    /**
     * Sends every session the server instance has
     * @return Every session the server instance has
     */
    public List<Session> getSessions() {
        return this.sessionManager.getSessions();
    }

    /**
     * Handles the event of a player having an advancement done
     * @param pade The event
     */
    public void playerAdvancementDone(PlayerAdvancementDoneEvent pade) {
        if(sessionManager.isSessionActive())
            sessionManager.playerAdvancementDone(pade);
    }

    /**
     * Sends the current GameMode
     * @return The current GameMode
     */
    public MhlGameMode getGameMode() {
        return this.gameMode;
    }

    /**
     * Sends the controller's Server instance
     * @return The controller's Server instance
     */
    public Server getServer() {
        return this.server;
    }

    /**
     * Sets the GameMode to the specified one.
     * It only works when a session is not active.
     * It loads the data of every player on the new GameMode
     * @param gameModeEnum  The kind of GameMode
     * @param commandSender The command sender
     */
    public void setGameMode(GameModes gameModeEnum, CommandSender commandSender) {
        if(this.sessionManager.isSessionActive())
            commandSender.sendMessage("You cannot change the GameMode during an active session");
        else if (gameModeEnum != this.getGameMode().getGameMode()){
            writeChanges();
            MhlGameMode gameMode = GameModes.toMhlGameMode(this, gameModeEnum);
            this.gameMode = gameMode;
            this.server.setDefaultNbLivesTokens(gameMode.getDefaultNbLifeTokens());
            for(Player player : this.server.getPlayers()){
                LifeToken lt = databaseHandler.getPlayerLifeTokensFromGameMode(player, gameModeEnum);
                player.setLivesTokens(lt == null ? GameModes.toMhlGameMode(this, gameModeEnum).getDefaultNbLifeTokens() : lt);
            }
            this.playerList.updatePlayerList();
            commandSender.sendMessage("The GameMode has been set to " + gameModeEnum.getName());
        }
        else
            commandSender.sendMessage("The current gameMode has already been set to " + gameModeEnum);
    }

    /**
     * Tell to the playerCommunicator to tell to everyone their role for the impostor GameMode
     * @param impostor The Player instance of the impostor
     */
    public void tellWhoIsImposter(Player impostor) {
        this.playerCommunicator.tellWhoIsImposter(server.getOnlinePlayers(), impostor);
    }

    /**
     * Sends the Player instance of the imposter for the Imposter GameMode
     * @return The Player instance of the imposter
     */
    public Player getImpostor() {
        return ((Impostor)(this.gameMode)).getImpostor();
    }

    /**
     * Shows to the command sender who is the imposter
     * @param commandSender The command sender
     */
    public void showImpostor(CommandSender commandSender) {
        if(sessionManager.isSessionActive()){
            Player impostor = getImpostor();
            if(impostor == null)
                commandSender.sendMessage("There is no impostor currently");
            else
                commandSender.sendMessage(impostor.getName());
        }
        else
            commandSender.sendMessage("The session must be active for the command to show you who are the impostors");
    }

    /**
     * Sends the current active session
     * @return The current active session if it exists, null otherwise
     */
    public Session getCurrentSession() {
        if(this.sessionManager.isSessionActive())
            return this.sessionManager.getSessions().getLast();
        else
            return null;
    }

    /**
     * Set the claimer of an event to the command sender
     * @param commandSender The claimer
     * @param eventId       The id of the event
     */
    public void claimEvent(CommandSender commandSender, int eventId) {
        // Checking the parameters
        if(!sessionManager.isSessionActive()) {
            commandSender.sendMessage("The session has not started yet");
            return;
        }
        List<SessionEvent> events = this.sessionManager.getSessions().getLast().getEvents();
        if(events.size() <= eventId) {
            commandSender.sendMessage("Invalid eventId");
            return;
        }
        if(server.getPlayers().stream().noneMatch(p -> p.getName().equals(commandSender.getName()))) {
            commandSender.sendMessage("No players with the name " + commandSender.getName() + " has been found on the server");
            return;
        }

        // Claiming the event
        SessionEvent event = events.get(eventId);
        Player player = this.findPlayerSafelyByName(commandSender.getName());
        if(!event.setClaimer(player))
            commandSender.sendMessage("A player has already claimed this event");
        else
            commandSender.sendMessage("The event has been claimed");
    }

    /**
     * Revokes the claim of a player to an event
     * @param commandSender The claimer revoking the event
     * @param eventId       The event id
     */
    public void revokeEvent(CommandSender commandSender, int eventId) {
        // Checking the parameters
        if(!sessionManager.isSessionActive()) {
            commandSender.sendMessage("The session has not started yet");
            return;
        }
        List<SessionEvent> events = this.sessionManager.getSessions().getLast().getEvents();
        if(events.size() <= eventId) {
            commandSender.sendMessage("Invalid eventId");
            return;
        }

        // Revoking the event claim
        SessionEvent event = events.get(eventId);
        if(event.getClaimer() == null)
            commandSender.sendMessage("Nobody claimed the event");
        else {
            commandSender.sendMessage("The event has been claimed");
            event.revokeEventClaim();
        }
    }

    /**
     * Assign the death claim of an event to the specified player
     * @param commandSender The command sender
     * @param eventId       The event id
     * @param claimer       The player claiming an event
     */
    public void assignDeathClaim(CommandSender commandSender, int eventId, Player claimer) {
        // Checking the parameters
        if(!sessionManager.isSessionActive()) {
            commandSender.sendMessage("The session has not started yet");
            return;
        }
        List<SessionEvent> events = this.sessionManager.getSessions().getLast().getEvents();
        if(events.size() <= eventId) {
            commandSender.sendMessage("Invalid eventId");
            return;
        }

        // Claiming the event
        SessionEvent event = events.get(eventId);
        if(!event.setClaimer(claimer))
            commandSender.sendMessage("A player has already claimed this event");
        else
            commandSender.sendMessage("The event has been claimed");
    }

    /**
     * Sends the players registered in the database
     * @return The players registered in the database
     */
    public List<Player> getDatabasePlayers() {
        return databaseHandler.getPlayers();
    }
}
