package org.mhl.multiplehardcorelives.model.session;

import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerAdvancementDoneEvent;
import org.mhl.multiplehardcorelives.controller.MhlController;
import org.mhl.multiplehardcorelives.model.session.enums.SessionEvents;
import org.mhl.multiplehardcorelives.model.gameLogic.Player;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.logging.Level;

/**
 * A class that contains the logic of the plugin for its sessions.
 */
public class SessionManager {

    /**
     * The plugin's controller
     */
    private final MhlController controller;

    /**
     * A boolean indicating if the session is active or not.
     */
    private boolean isSessionActive = false;

    /**
     * A boolean for knowing if major events can happen or not
     */
    private boolean allowMajorEvents = false;

    /**
     * The list of sessions the server had host since its launch
     */
    private final ArrayList<Session> sessions = new ArrayList<>();

    /**
     * The number of previous sessions
     */
    private final int nbOfPreviousSessions;

    /**
     * The currently running session
     */
    private Session currentSession;

    /**
     * Initialises a SessionManager that will listen to players events.
     *
     * @param nbOfPreviousSessions The number of previous sessions
     * @param controller The plugin's controller
     */
    public SessionManager(int nbOfPreviousSessions, MhlController controller){
        this.nbOfPreviousSessions = nbOfPreviousSessions;
        this.controller = controller;
        Bukkit.getLogger().log(Level.INFO, "Now listening to player informations");
    }

    /**
     * Sends the state of the session.
     * @return The state of the session.
     */
    public boolean isSessionActive() {
        return isSessionActive;
    }

    /**
     * Sends the state of major events allowance
     * @return The state of major events allowance
     */
    public boolean areMajorEventsAllowed() {
        return allowMajorEvents;
    }

    /**
     * Starts the session by setting the field isSessionActive to true.
     */
    public void startSession(){
        //
        if(isSessionActive){
            Bukkit.getLogger().warning("Session has already started");
            return;
        }

        //
        this.sessions.add(new Session(nbOfPreviousSessions + sessions.size() + 1, Calendar.getInstance()));
        this.currentSession = sessions.getLast();

        //
        isSessionActive = true;
        allowMajorEvents = true;

        Bukkit.getLogger().log(Level.INFO, "Session has started");
    }

    /**
     * Ends the sessions by setting the field isSessionActive to false.
     */
    public void endSession(){
        if(!isSessionActive){
            Bukkit.getLogger().warning("Session has already stopped");
            return;
        }

        //
        this.currentSession.setSessionEnd(Calendar.getInstance());
        this.currentSession = null;

        //
        isSessionActive = false;
        allowMajorEvents = false;

        Bukkit.getLogger().log(Level.INFO, "Session has ended");
    }

    /**
     * Stops major events from happening
     */
    public void stopMajorEvents() {
        if(!allowMajorEvents){
            Bukkit.getLogger().log(Level.WARNING, "Major events are already not hallowed");
            return;
        }

        this.allowMajorEvents = false;
        Bukkit.getLogger().log(Level.INFO, "Session cannot have major events anymore");
    }

    /**
     * Sends every session
     * @return Every session
     */
    public ArrayList<Session> getSessions(){
        return this.sessions;
    }

    /**
     * Handles the registration of a player dying
     * @param pde The event of the player dying
     */
    public void playerDied(PlayerDeathEvent pde) {
        this.currentSession.addEvent(SessionEvents.Player_death, Calendar.getInstance(), pde.getDeathMessage());
    }

    /**
     * Handles the registration of a player being definitively dead
     * @param pde The event of a player dying
     */
    public void definitivePlayerDeath(PlayerDeathEvent pde) {
        this.currentSession.addEvent(SessionEvents.Player_definitive_death, Calendar.getInstance(), pde.getDeathMessage());
    }

    /**
     * Handles the registration of a player resurrecting
     * @param player The player who has been resurrected
     */
    public void playerResurrected(Player player) {
        this.currentSession.addEvent(SessionEvents.Player_resurrection, Calendar.getInstance(), "Player " + player.getName() + " has been sent back to life");
    }

    /**
     * Handles the registration of a player who have done an advancement
     * @param pade The event of an advancement which has been done
     */
    public void playerAdvancementDone(PlayerAdvancementDoneEvent pade) {
        PlainTextComponentSerializer a = PlainTextComponentSerializer.plainText();
        this.currentSession.addEvent(SessionEvents.Advancement, Calendar.getInstance(), "Player " + pade.getPlayer().getName() + " just got the advancement " + a.serialize(pade.getAdvancement().displayName()), controller.findPlayerSafelyByUUID(pade.getPlayer().getUniqueId()));
    }

    /**
     * Handles the registration of a player quitting the server
     * @param player The player who has quit
     */
    public void playerQuit(org.bukkit.entity.Player player) {
        this.currentSession.addEvent(SessionEvents.Player_quit, Calendar.getInstance(), "Player " + player.getName() + " has quit the game", controller.findPlayerSafelyByUUID(player.getUniqueId()));
    }

    /**
     * Handles the registration of a player joining the server
     * @param player The player who has joined
     */
    public void playerJoined(org.bukkit.entity.Player player) {
        this.currentSession.addEvent(SessionEvents.Player_join, Calendar.getInstance(), "Player " + player.getName() + " has joined the game", controller.findPlayerSafelyByUUID(player.getUniqueId()));
    }
}
