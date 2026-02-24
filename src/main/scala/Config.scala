import spray.json._
import utils.readFromFile

case class Config(
    calendarId: String,
    clientId: String,
    clientSecret: String,
    rttUser: String,
    rttApiKey: String,
    attendees: List[String]
)

object ConfigProtocol extends DefaultJsonProtocol {
  implicit val format: RootJsonFormat[Config] = jsonFormat6(
    Config.apply
  )
}

import ConfigProtocol._

def loadConfig(): Config =
  readFromFile("config.json").parseJson.convertTo[Config]
