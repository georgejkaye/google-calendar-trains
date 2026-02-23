package rtt

import com.github.nscala_time.time.Imports.*

case class StationDeparture(
    serviceUid: String,
    trainIdentity: String,
    destinations: List[String],
    planDep: DateTime,
    operatorName: String
)
