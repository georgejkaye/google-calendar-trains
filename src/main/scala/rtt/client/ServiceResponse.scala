package rtt.client

import spray.json.*
import PairProtocol.format

case class Location(
    description: String,
    gbttBookedDeparture: Option[String]
)

object LocationProtocol extends DefaultJsonProtocol {
  implicit val format: RootJsonFormat[Location] =
    jsonFormat2(Location.apply)
}

import LocationProtocol.format

case class ServiceResponse(
    serviceUid: String,
    runDate: String,
    atocName: String,
    origin: Vector[Pair],
    destination: Vector[Pair],
    locations: Vector[Location]
)

object ServiceResponseProtocol extends DefaultJsonProtocol {
  implicit val format: RootJsonFormat[ServiceResponse] =
    jsonFormat6(ServiceResponse.apply)
}
