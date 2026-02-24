package rtt

import org.joda.time.DateTime

case class Call(
    stationName: String,
    planDep: Option[DateTime]
)

case class Service(
    serviceUid: String,
    runDate: DateTime,
    operatorName: String,
    origins: Vector[String],
    destinations: Vector[String],
    calls: Vector[Call]
)
