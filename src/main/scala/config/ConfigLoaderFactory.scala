package config

import better.files._
import File._

val dotConfigLoader = ConfigLoader(
  home / ".config" / "caltrains" / "config.json"
)

val testConfigLoader = ConfigLoader(File("config.json"))
