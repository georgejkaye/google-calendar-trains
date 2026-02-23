package rtt

import sttp.client4.quick.*
import spray.json.*
import com.github.nscala_time.time.Imports.*

import rtt.LocationResponseProtocol.format

class RttClient(baseUrl: String, rttUser: String, rttApiKey: String)
    extends IRttClient {

  def getDeparturesFromStation(
      station: String,
      searchTime: DateTime
  ): List[StationDeparture] =
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
        DateTime.parse(service.locationDetail.gbttBookedDeparture),
        service.atocName
      )
    )
}
