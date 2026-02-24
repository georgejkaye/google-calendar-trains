package rtt.client

import spray.json.*
import PairProtocol.format

case class Location(
    description: String,
    crs: Option[String],
    isCall: Boolean,
    gbttBookedDeparture: Option[String],
    gbttBookedArrival: Option[String]
)

object LocationProtocol extends DefaultJsonProtocol {
  implicit val format: RootJsonFormat[Location] =
    jsonFormat5(Location.apply)
}

import LocationProtocol.format

case class ServiceResponse(
    serviceUid: String,
    runDate: String,
    atocName: String,
    origin: Vector[Pair],
    destination: Vector[Pair],
    locations: List[Location]
)

object ServiceResponseProtocol extends DefaultJsonProtocol {
  implicit val format: RootJsonFormat[ServiceResponse] =
    jsonFormat6(ServiceResponse.apply)
}
