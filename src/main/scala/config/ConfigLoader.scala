package config

import utils.readFromFile
import better.files._
import File._
import spray.json._

import ConfigProtocol._

class ConfigLoader(configPath: File) extends IConfigLoader:
  def getConfig(): Option[Config] =
    Some(readFromFile(configPath).parseJson.convertTo[Config])
