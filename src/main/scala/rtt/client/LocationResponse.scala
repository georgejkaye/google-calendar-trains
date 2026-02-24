package rtt.client

import spray.json.*

import PairProtocol._

case class LocationDetail(
    gbttBookedDeparture: String,
    gbttBookedDepartureNextDay: Option[Boolean],
    destination: Vector[Pair]
)

object LocationDetailProtocol extends DefaultJsonProtocol {
  implicit val format: RootJsonFormat[LocationDetail] =
    jsonFormat3(LocationDetail.apply)
}

import LocationDetailProtocol._

case class LocationContainer(
    serviceUid: String,
    trainIdentity: String,
    atocName: String,
    runDate: String,
    locationDetail: LocationDetail
)

object LocationContainerProtocol extends DefaultJsonProtocol {
  implicit val format: RootJsonFormat[LocationContainer] =
    jsonFormat5(LocationContainer.apply)
}

import LocationContainerProtocol._

case class LocationResponse(
    services: Vector[LocationContainer]
)

object LocationResponseProtocol extends DefaultJsonProtocol {
  implicit val format: RootJsonFormat[LocationResponse] =
    jsonFormat1(LocationResponse.apply)
}
