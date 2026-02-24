package rtt

import org.joda.time.DateTime

case class Call(
    stationName: String,
    stationCrs: String,
    planDep: Option[DateTime],
    planArr: Option[DateTime]
)

case class Service(
    serviceUid: String,
    runDate: DateTime,
    operatorName: String,
    origins: Vector[String],
    destinations: Vector[String],
    calls: List[Call]
)
