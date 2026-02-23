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

case class Event(
    start: EventTime,
    end: EventTime,
    summary: String,
    description: String,
    location: String
)

object EventProtocol extends DefaultJsonProtocol {
  implicit val format: RootJsonFormat[Event] =
    jsonFormat5(Event.apply)
}
