package rtt.client

import org.joda.time.DateTime
import rtt.StationDeparture
import rtt.Service
import org.joda.time.LocalDate

trait IRttClient:
  def getDeparturesFromStation(
      station: String,
      searchTime: DateTime
  ): Vector[StationDeparture]

  def getService(serviceUid: String, runDate: LocalDate): Option[Service]
