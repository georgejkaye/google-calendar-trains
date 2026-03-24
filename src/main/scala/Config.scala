import spray.json._
import utils.readFromFile
import better.files._
import File._

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
  val configPath = home / ".config" / "caltrains" / "config.json"
  readFromFile(configPath).parseJson.convertTo[Config]
