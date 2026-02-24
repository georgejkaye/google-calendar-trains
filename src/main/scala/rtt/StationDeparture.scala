package rtt

import com.github.nscala_time.time.Imports.*

case class StationDeparture(
    serviceUid: String,
    runDate: DateTime,
    trainIdentity: String,
    destinations: Vector[String],
    planDep: DateTime,
    operatorName: String
)
