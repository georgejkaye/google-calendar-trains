package google.calendar

import sttp.client4.quick.*
import spray.json.*

import google.calendar.EventProtocol.format

class GoogleCalendarClient(baseUrl: String) extends IGoogleCalendarClient:
  def insertCalendarEvent(
      accessToken: String,
      calendarId: String,
      event: Event
  ): Unit =
    val url = uri"$baseUrl/calendars/$calendarId/events"
    val response =
      basicRequest
        .post(url)
        .body(event.toJson.prettyPrint)
        .auth
        .bearer(accessToken)
        .send()
        .body
