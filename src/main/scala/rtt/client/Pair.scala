package rtt.client

import spray.json.*

case class Pair(
    description: String
)

object PairProtocol extends DefaultJsonProtocol {
  implicit val format: RootJsonFormat[Pair] =
    jsonFormat1(Pair.apply)
}
