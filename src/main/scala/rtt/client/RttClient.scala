package rtt.client

import sttp.client4.quick.*
import spray.json.*
import com.github.nscala_time.time.Imports.*

import rtt.client.IRttClient
import rtt.StationDeparture
import rtt.client.LocationResponse
import rtt.client.LocationResponseProtocol.format
import rtt.client.ServiceResponseProtocol.format
import rtt.Service
import rtt.Call

class RttClient(baseUrl: String, rttUser: String, rttApiKey: String)
    extends IRttClient {
  def getDeparturesFromStation(
      station: String,
      searchTime: DateTime
  ): Either[String, IndexedSeq[StationDeparture]] =
    val url =
      uri"$baseUrl/json/search/$station/${searchTime.toString("yyyy")}/${searchTime.toString("MM")}/${searchTime.toString("dd")}/${searchTime.toString("HHmm")}"
    basicRequest
      .get(url)
      .auth
      .basic(rttUser, rttApiKey)
      .send()
      .body
      .flatMap { body =>
        val response = body.parseJson
          .convertTo[LocationResponse]
        Right(
          response.services.map(service =>
            StationDeparture(
              service.serviceUid,
              DateTimeFormat
                .forPattern("yyyy-MM-dd")
                .parseDateTime(service.runDate),
              service.trainIdentity,
              service.locationDetail.destination
                .map(destination => destination.description),
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
        )
      }

  def getService(
      serviceUid: String,
      runDate: DateTime
  ): Either[String, Service] =
    val url =
      uri"$baseUrl/json/service/$serviceUid/${runDate.toString("yyyy")}/${runDate.toString("MM")}/${runDate.toString("dd")}"
    basicRequest
      .get(url)
      .auth
      .basic(rttUser, rttApiKey)
      .send()
      .body
      .flatMap { body =>
        val response = body.parseJson.convertTo[ServiceResponse]
        val initialDepartureTime =
          DateTimeFormat
            .forPattern("HHmm")
            .parseDateTime(
              response.locations(0).gbttBookedDeparture.getOrElse("0000")
            )
            .withDate(
              runDate.getYear(),
              runDate.getMonthOfYear(),
              runDate.getDayOfMonth()
            )
        Right(
          Service(
            response.serviceUid,
            DateTimeFormat
              .forPattern("yyyy-MM-dd")
              .parseDateTime(response.runDate),
            response.atocName,
            response.origin.map(pair => pair.description),
            response.destination.map(pair => pair.description),
            response.locations
              .filter(location => !location.crs.isEmpty && location.isCall)
              .map(location =>
                Call(
                  location.description,
                  location.crs.getOrElse(""),
                  getCallTime(
                    location.gbttBookedDeparture,
                    initialDepartureTime
                  ),
                  getCallTime(location.gbttBookedArrival, initialDepartureTime)
                )
              )
          )
        )
      }

  def getCallTime(
      timeStringOption: Option[String],
      initialDepartureTime: DateTime
  ): Option[DateTime] =
    timeStringOption match {
      case None             => None
      case Some(timeString) =>
        val planDep =
          DateTimeFormat
            .forPattern("HHmm")
            .parseDateTime(timeString)
            .withDate(
              initialDepartureTime.getYear(),
              initialDepartureTime.getMonthOfYear(),
              initialDepartureTime.getDayOfMonth()
            )
        Some(
          if planDep < initialDepartureTime then planDep + 1.day
          else planDep
        )
    }
}
