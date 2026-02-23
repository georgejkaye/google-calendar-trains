package rtt.interactive

import rtt.StationDeparture
import scala.io.StdIn

def getStationDepartureFromList(
    departures: Vector[StationDeparture]
): Option[StationDeparture] =
  departures.zipWithIndex.foreach((departure, i) =>
    println(
      s"${i + 1}: ${departure.planDep.toString("HH:mm")} to ${departure.destinations.mkString(" and ")}"
    )
  )
  val choice = StdIn.readLine().toIntOption
  choice match {
    case None    => None
    case Some(i) => Some(departures(i - 1))
  }
