package google.calendar

import spray.json.*
import utils.DateTimeProtocol.DateTimeFormat
import com.github.nscala_time.time.Imports.*

case class EventTime(
    date: Option[DateTime] = None,
    dateTime: Option[DateTime] = None,
    timeZone: Option[String] = None
)

object EventTimeProtocol extends DefaultJsonProtocol {
  implicit val format: RootJsonFormat[EventTime] =
    jsonFormat3(EventTime.apply)
}

import EventTimeProtocol.*

case class Attendee(
    email: String
)

object AttendeeProtocol extends DefaultJsonProtocol {
  implicit val format: RootJsonFormat[Attendee] =
    jsonFormat1(Attendee.apply)
}

import AttendeeProtocol.*

case class Event(
    start: EventTime,
    end: EventTime,
    summary: String,
    description: String,
    location: String,
    attendees: List[Attendee]
)

object EventProtocol extends DefaultJsonProtocol {
  implicit val format: RootJsonFormat[Event] =
    jsonFormat6(Event.apply)
}
