package rtt

import org.joda.time.DateTime

case class ServiceLocation(
    stationName: String,
    planDep: DateTime,
    associations: Vector[Service]
)

case class Service(
    serviceUid: String,
    runDate: DateTime,
    operatorName: String,
    origins: Vector[String],
    destinations: Vector[String],
    calls: Vector[ServiceLocation]
)
