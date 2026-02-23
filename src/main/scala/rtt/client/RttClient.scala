package rtt.client

import sttp.client4.quick.*
import spray.json.*
import com.github.nscala_time.time.Imports.*

import rtt.client.IRttClient
import rtt.StationDeparture
import rtt.client.LocationResponse
import rtt.client.LocationResponseProtocol.format
import org.joda.time.format.DateTimeFormatter

class RttClient(baseUrl: String, rttUser: String, rttApiKey: String)
    extends IRttClient {

  def getDeparturesFromStation(
      station: String,
      searchTime: DateTime
  ): Vector[StationDeparture] =
    val url =
      uri"$baseUrl/json/search/$station/${searchTime.toString("yyyy")}/${searchTime.toString("MM")}/${searchTime.toString("dd")}/${searchTime.toString("HHmm")}"
    println(url)
    val response =
      quickRequest
        .get(url)
        .auth
        .basic(rttUser, rttApiKey)
        .send()
        .body
        .parseJson
        .convertTo[LocationResponse]
    response.services.map(service =>
      StationDeparture(
        service.serviceUid,
        service.trainIdentity,
        service.locationDetail.destination.map(destination =>
          destination.description
        ),
        DateTimeFormat
          .forPattern("HHmm")
          .parseDateTime(service.locationDetail.gbttBookedDeparture)
          .withDate(
            DateTimeFormat
              .forPattern("yyyy-MM-dd")
              .parseLocalDate(service.runDate)
          ) + (service.locationDetail.gbttBookedDepartureNextDay match {
          case None    => 0.day
          case Some(b) => if b then 1.day else 0.day
        }),
        service.atocName
      )
    )
}
