package org.mhl.multiplehardcorelives.model.session;

import org.bukkit.Bukkit;
import org.mhl.multiplehardcorelives.model.gameLogic.Player;
import org.mhl.multiplehardcorelives.model.session.enums.SessionEvents;

import java.util.Calendar;
import java.util.logging.Level;

/**
 * A model for describing an event in a session
 */
public class SessionEvent {
    /**
     * The kind of event
     */
    public final SessionEvents event;

    /**
     * The event's date of happening
     */
    public final Calendar date;

    /**
     * The event's ID
     */
    public final int eventId;

    /**
     * The event's description
     */
    public final String description;

    /**
     * The player claiming the event
     */
    private Player claimer;

    /**
     * Creates an instance of SessionEvent
     * @param event The kind of event
     * @param date The event's date of happening
     * @param eventId The event's ID
     * @param description The event's description
     */
    public SessionEvent(SessionEvents event, Calendar date, int eventId, String description){
        this.event = event;
        this.date = date;
        this.eventId = eventId;
        this.description = description;
    }

    /**
     * Creates an instance of SessionEvent
     * @param event The kind of event
     * @param date The event's date of happening
     * @param eventId The event's ID
     * @param description The event's description
     * @param claimer The player claiming the event
     */
    public SessionEvent(SessionEvents event, Calendar date, int eventId, String description, Player claimer){
        this.event = event;
        this.date = date;
        this.eventId = eventId;
        this.description = description;
        this.claimer = claimer;
    }

    /**
     * Sends the Player instance of the claimer
     * @return The Player instance of the claimer
     */
    public Player getClaimer() {
        return claimer;
    }

    /**
     * Sets the claimer to the event
     * @param claimer The player claiming the event
     * @return true if the claim has gone right, false otherwise
     */
    public boolean setClaimer(Player claimer) {
        if(this.claimer == null) {
            this.claimer = claimer;
            Bukkit.getLogger().log(Level.INFO, "The event " + this.eventId + " has been claimed by " + claimer.getName());
            return true;
        }
        return false;
    }

    /**
     * Revokes the claim
     */
    public void revokeEventClaim() {
        this.claimer = null;
    }
}
