package org.mhl.multiplehardcorelives.model.session;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mhl.multiplehardcorelives.model.session.enums.SessionEvents;

import java.util.Calendar;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SessionEventTest {
  private SessionEvent event;

  @Nested
  class ConstructorTests {
    @Test
    public void isSessionEventCorrect() {
      SessionEvents sessionEvent = SessionEvents.Advancement;
      event = new SessionEvent(sessionEvent, Calendar.getInstance(), 0, "");
      assertEquals(sessionEvent, event.event);
    }

    @Test
    public void isDateCorrect() {
      Calendar date = Calendar.getInstance();
      event = new SessionEvent(SessionEvents.Advancement, date, 0, "");
      assertEquals(date, event.date);
    }

    @Test
    public void isEventIDCorrect() {
      int eventID = 1;
      event = new SessionEvent(SessionEvents.Advancement, Calendar.getInstance(), eventID, "");
      assertEquals(eventID, event.eventId);
    }

    @Test
    public void isDescriptionCorrect() {
      String description = "description";
      event = new SessionEvent(SessionEvents.Advancement, Calendar.getInstance(), 0, description);
      assertEquals(description, event.description);
    }
  }
}
