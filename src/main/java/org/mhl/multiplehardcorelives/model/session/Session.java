package org.mhl.multiplehardcorelives.model.session;

import org.mhl.multiplehardcorelives.model.gameLogic.Player;
import org.mhl.multiplehardcorelives.model.session.enums.SessionEvents;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * A model stocking information about a session
 */
public class Session {
    /**
     * The session's index
     */
    private final int sessionNumber;

    /**
     * The date of the start of the session
     */
    private final Calendar sessionStart;

    /**
     * The date of the end of the session
     */
    private Calendar sessionEnd;

    /**
     * The list of events that happened during the session
     */
    private final List<SessionEvent> events = new ArrayList<>();

    /**
     * Creates a Session instance by providing the session's ID and its start date
     * @param sessionNumber The session's ID
     * @param sessionStart  The date of the start of the session
     */
    public Session(int sessionNumber, Calendar sessionStart){
        this.sessionNumber = sessionNumber;
        this.sessionStart = sessionStart;
    }

    /**
     * Sends the session's ID
     * @return The session's ID
     */
    public int getSessionNumber() {
        return sessionNumber;
    }

    /**
     * Sends the session's date of its start
     * @return The session's date of its start
     */
    public Calendar getSessionStart() {
        return sessionStart;
    }

    /**
     * Sends the session's date of its end
     * @return The session's date of its end
     */
    public Calendar getSessionEnd() {
        return sessionEnd;
    }

    /**
     * Sets the session's date of its end
     * @param sessionEnd The session's date of its end
     */
    public void setSessionEnd(Calendar sessionEnd) {
        this.sessionEnd = sessionEnd;
    }

    /**
     * Sends the list of events the session had received
     * @return The list of events the session had received
     */
    public List<SessionEvent> getEvents() {
        return events;
    }

    /**
     * Adds an event to the session by providing enough information to instantiate a SessionEvent
     * @param eventType The kind of event
     * @param date The event's date of happening
     * @param description The event's description
     */
    public void addEvent(SessionEvents eventType, Calendar date, String description){
        this.events.add(new SessionEvent(eventType, date, this.events.size(), description));
    }

    /**
     * Adds an event to the session by providing enough information to instantiate a SessionEvent
     * @param eventType The kind of event
     * @param date The event's date of happening
     * @param description The event's description
     * @param claimer The player claiming the event
     */
    public void addEvent(SessionEvents eventType, Calendar date, String description, Player claimer){
        this.events.add(new SessionEvent(eventType, date, this.events.size(), description, claimer));
    }

    /**
     * Sends the events of the specified kind
     * @param sessionEvents The kind of session events
     * @return The list of every event of the specified kind
     */
    public List<SessionEvent> getEventsOfType(SessionEvents sessionEvents) {
        return this.events.stream().filter(e -> e.event == sessionEvents).toList();
    }
}
