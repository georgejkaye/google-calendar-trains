package rtt

import org.joda.time.DateTime

trait IRttClient:
  def getDeparturesFromStation(
      station: String,
      searchTime: DateTime
  ): List[StationDeparture]
