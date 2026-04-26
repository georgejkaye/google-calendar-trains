package config

import spray.json._

case class Config(
    calendarId: String,
    clientId: String,
    clientSecret: String,
    rttUser: String,
    rttApiKey: String,
    attendees: Option[List[String]]
)

object ConfigProtocol extends DefaultJsonProtocol {
  implicit val format: RootJsonFormat[Config] = jsonFormat6(
    Config.apply
  )
}
