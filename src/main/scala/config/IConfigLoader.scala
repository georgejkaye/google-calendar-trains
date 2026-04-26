package config

trait IConfigLoader:
  def getConfig(): Option[Config]
