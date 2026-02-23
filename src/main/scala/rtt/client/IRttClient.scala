package rtt.client

import org.joda.time.DateTime
import rtt.StationDeparture

trait IRttClient:
  def getDeparturesFromStation(
      station: String,
      searchTime: DateTime
  ): Vector[StationDeparture]
