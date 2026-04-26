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
import utils.parseDateTime
import utils.parseYearMonthDay
import utils.parseHourMinute
import scala.annotation.init

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
          response.services
            .filter(service => service.trainIdentity.isDefined)
            .map(service => {
              val runDate = parseYearMonthDay(service.runDate)
              val depTime = parseHourMinute(
                service.locationDetail.gbttBookedDeparture
              ).withDate(runDate.toLocalDate())
              val depTimeNextDayOffset =
                service.locationDetail.gbttBookedDepartureNextDay match {
                  case None    => 0.day
                  case Some(b) => if b then 1.day else 0.day
                }
              StationDeparture(
                service.serviceUid,
                parseYearMonthDay(service.runDate),
                service.trainIdentity.get,
                service.locationDetail.destination
                  .map(destination => destination.description),
                depTime + depTimeNextDayOffset,
                service.atocName
              )
            })
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
        val initialDepartureTime = parseHourMinute(
          response.locations(0).gbttBookedDeparture.getOrElse("0000")
        )
          .withDate(runDate.toLocalDate())
        Right(
          Service(
            response.serviceUid,
            parseYearMonthDay(response.runDate),
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
        val planDep = parseHourMinute(timeString)
          .withDate(initialDepartureTime.toLocalDate())
        Some(
          if planDep < initialDepartureTime then planDep + 1.day
          else planDep
        )
    }
}
