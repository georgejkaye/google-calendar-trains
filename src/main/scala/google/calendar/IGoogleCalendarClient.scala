package google.calendar

trait IGoogleCalendarClient:
  def insertCalendarEvent(
      accessToken: String,
      calendarId: String,
      event: Event
  ): Unit
