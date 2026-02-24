package rtt.client

import com.github.nscala_time.time.Imports.*

import rtt.StationDeparture
import rtt.Service

trait IRttClient:

  def getDeparturesFromStation(
      station: String,
      searchTime: DateTime
  ): Vector[StationDeparture]

  def getService(serviceUid: String, runDate: DateTime): Service

  def getServiceFromStationDeparture(
      stationDeparture: StationDeparture
  ): Service =
    getService(stationDeparture.serviceUid, stationDeparture.runDate)
