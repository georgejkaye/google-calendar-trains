package rtt.client

import com.github.nscala_time.time.Imports.*

import rtt.StationDeparture
import rtt.Service

trait IRttClient:

  def getDeparturesFromStation(
      station: String,
      searchTime: DateTime
  ): Either[String, IndexedSeq[StationDeparture]]

  def getService(serviceUid: String, runDate: DateTime): Either[String, Service]

  def getServiceFromStationDeparture(
      stationDeparture: StationDeparture
  ): Either[String, Service] =
    getService(stationDeparture.serviceUid, stationDeparture.runDate)
