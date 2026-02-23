import spray.json._
import utils.readFromFile

case class Config(calendarId: String)

object ConfigProtocol extends DefaultJsonProtocol {
  implicit val format: RootJsonFormat[Config] = jsonFormat1(
    Config.apply
  )
}

import ConfigProtocol._

def loadConfig(): Config =
  readFromFile("config.json").parseJson.convertTo[Config]
