package utils

import spray.json.DefaultJsonProtocol
import com.github.nscala_time.time.Imports.DateTime
import spray.json.RootJsonFormat
import spray.json.JsString
import spray.json.JsValue

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
