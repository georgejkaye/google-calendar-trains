package google.calendar

trait ICalendarClient:
  def insertCalendarEvent(
      accessToken: String,
      calendarId: String,
      event: Event
  ): Unit
