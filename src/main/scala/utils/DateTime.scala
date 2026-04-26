package utils

import spray.json.DefaultJsonProtocol
import com.github.nscala_time.time.Imports.DateTime
import spray.json.RootJsonFormat
import spray.json.JsString
import spray.json.JsValue
import com.github.nscala_time.time.Imports.*

val europeLondonTimeZone = DateTimeZone.forID("Europe/London")

object DateTimeProtocol extends DefaultJsonProtocol {
  implicit object DateTimeFormat extends RootJsonFormat[DateTime] {
    def write(dt: DateTime) =
      JsString(dt.toString())

    def read(value: JsValue) = value match {
      case JsString(string) => DateTime.parse(string)
      case _ => throw new IllegalArgumentException("Invalid date")
    }
  }
}

def parseDateTime(
    input: String,
    format: String,
    tz: DateTimeZone = europeLondonTimeZone
) = {
  DateTimeFormat
    .forPattern(format)
    .withZone(tz)
    .parseDateTime(input)
}

def parseDateTimeOption(
    input: String,
    format: String,
    tz: DateTimeZone = europeLondonTimeZone
) = {
  DateTimeFormat
    .forPattern(format)
    .withZone(tz)
    .parseOption(input)
}

def parseYearMonthDay(
    input: String,
    tz: DateTimeZone = europeLondonTimeZone
) = {
  parseDateTime(input, "yyyy-MM-dd", tz)
}

def parseYearMonthDayOption(
    input: String,
    tz: DateTimeZone = europeLondonTimeZone
) = {
  parseDateTimeOption(input, "yyyy-MM-dd", tz)
}

def parseHourMinute(input: String, tz: DateTimeZone = europeLondonTimeZone) = {
  parseDateTime(input, "HHmm", tz)
}

def parseHourMinuteOption(
    input: String,
    tz: DateTimeZone = europeLondonTimeZone
) = {
  parseDateTimeOption(input, "HHmm", tz)
}
