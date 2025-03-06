package org.mhl.multiplehardcorelives.model.session;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mhl.multiplehardcorelives.model.session.enums.SessionEvents;

import java.util.Calendar;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class SessionTest {
    private Session session;
    @Nested
    class ConstructorTest {
        @ParameterizedTest
        @ValueSource(ints = {0, 1, -1, Integer.MAX_VALUE, Integer.MIN_VALUE})
        public void isSessionNumberValid (int sessionNumber) {
            session = new Session(sessionNumber, Calendar.getInstance());
            assertEquals(sessionNumber, session.getSessionNumber());
        }

        @Test
        public void isSessionStartValid () {
            Calendar sessionStart = Calendar.getInstance();
            session = new Session(0, sessionStart);
            assertEquals(sessionStart, session.getSessionStart());
        }

        @Test
        public void isSessionEndNotInitialised () {
            session = new Session(0, Calendar.getInstance());
            assertNull(session.getSessionEnd());
        }

        @Test
        public void isEventListInitialised () {
            session = new Session(0, Calendar.getInstance());
            assertNotNull(session.getEvents());
        }

        @Test
        public void isEventListEmpty () {
            session = new Session(0, Calendar.getInstance());
            assertTrue(session.getEvents().isEmpty());
        }
    }

    @Nested
    class AddEventTest {
        @BeforeEach
        public void setup () {
            session = new Session(0, Calendar.getInstance());
        }

        @Test
        public void isEventAdded () {
            session.addEvent(SessionEvents.Player_death, Calendar.getInstance(), "description");
            assertEquals(1, session.getEvents().size());
        }

        @Test
        public void areEventIdsCorrect () {
            session.addEvent(SessionEvents.Player_death, Calendar.getInstance(), "description");
            session.addEvent(SessionEvents.Player_death, Calendar.getInstance(), "description");

            List<SessionEvent> sessionEvents = session.getEvents();
            assertTrue(
                sessionEvents.getFirst().eventId == 0
                && sessionEvents.getLast().eventId == 1
            );
        }
    }
}
