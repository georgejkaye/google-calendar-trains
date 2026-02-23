package google.calendar

import sttp.client4.quick.*
import spray.json.*

import google.calendar.EventProtocol.format
import google.auth.RequestAuthHeaderAdder

class CalendarClient(baseUrl: String) extends ICalendarClient:
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
        .addAuthHeader(accessToken)
        .send()
        .body
    println(response)
