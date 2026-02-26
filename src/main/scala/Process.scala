import scala.util._
import com.github.nscala_time.time.Imports.*
import google.calendar.IGoogleCalendarClient
import google.auth.IGoogleAuthClient
import rtt.client.IRttClient
import rtt.StationDeparture
import rtt.Call
import rtt.Service
import google.calendar.Event
import google.calendar.EventTime
import google.calendar.Attendee

def runProcess(
    config: Config,
    authClient: IGoogleAuthClient,
    calendarClient: IGoogleCalendarClient,
    rttClient: IRttClient
): Either[String, Unit] =
  Right(())
    .flatMap { _ =>
      authClient.getAccessToken() match {
        case Left(error)  => Left(s"Could not get google auth token: ${error}")
        case Right(token) => Right(token)
      }
    }
    .flatMap { googleToken =>
      io.StdIn.readLine("Station code: ").toUpperCase() match {
        case stationCode if stationCode.length == 3 =>
          Right((googleToken, stationCode))
        case _ => Left("Station code must be three characters")
      }
    }
    .flatMap { (googleToken, stationCode) =>
      DateTimeFormat
        .forPattern("yyyy-MM-dd")
        .parseOption(io.StdIn.readLine("Run date: ")) match {
        case None     => Left("Invalid date format, expected yyyy-MM-dd")
        case Some(rd) => Right((googleToken, stationCode, rd))
      }
    }
    .flatMap { (googleToken, stationCode, runDate) =>
      DateTimeFormat
        .forPattern("HHmm")
        .parseOption(io.StdIn.readLine("Departure time: ")) match {
        case None =>
          Left("Invalid time format, expected HHmm")
        case Some(dt) => Right((googleToken, stationCode, runDate, dt))
      }
    }
    .flatMap { (googleToken, stationCode, runDate, depTime) =>
      val stationDepartures = rttClient.getDeparturesFromStation(
        stationCode,
        runDate
          .withTime(depTime.getHourOfDay(), depTime.getMinuteOfHour(), 0, 0)
      )
      getStationDepartureFromList(stationDepartures) match {
        case None            => Left("No departure picked")
        case Some(departure) =>
          Right((googleToken, stationCode, depTime, departure))
      }
    }
    .flatMap { (googleToken, boardStationCode, depTime, departure) =>
      val service = rttClient.getServiceFromStationDeparture(departure)
      getBoardCallFromService(service, boardStationCode, depTime) match
        case None       => Left("Could not get board call")
        case Some(call) => Right((googleToken, service, call))
    }
    .flatMap { (googleToken, service, boardCall) =>
      getAlightCallFromService(service, boardCall) match
        case None             => Left("Could not get alight call")
        case Some(alightCall) =>
          Right((googleToken, service, boardCall, alightCall))
    }
    .flatMap { (googleToken, service, boardCall, alightCall) =>
      val journeyTitle =
        s"${boardCall.stationName} to ${alightCall.stationName}"
      val seats = io.StdIn.readLine("Seat reservations: ") match {
        case ""         => ""
        case seatString => s"\n\n$seatString"
      }
      val rttLink =
        s"https://www.realtimetrains.co.uk/service/gb-nr:${service.serviceUid}/${service.runDate.toString("yyyy-MM-dd")}/detailed"
      val _ = calendarClient.insertCalendarEvent(
        googleToken,
        config.calendarId,
        Event(
          EventTime(dateTime = boardCall.planDep),
          EventTime(dateTime = alightCall.planArr),
          journeyTitle,
          s"$rttLink$seats",
          s"${boardCall.stationName} railway station",
          config.attendees.map(attendee => Attendee(attendee))
        )
      )
      Right(())
    }

def getStationDepartureFromList(
    departures: Vector[StationDeparture]
): Option[StationDeparture] =
  departures.zipWithIndex.foreach((departure, i) =>
    val indexString = s"${i + 1}:".padTo(2, " ").mkString
    println(
      s"${indexString} ${departure.planDep.toString("HH:mm")} to ${departure.destinations.mkString(" and ")}"
    )
  )
  io.StdIn.readLine("Service: ").toIntOption match {
    case None    => None
    case Some(i) => Some(departures(i - 1))
  }

def getBoardCallFromService(
    service: Service,
    stationCode: String,
    depTime: DateTime
): Option[Call] =
  def getBoardCalls(calls: List[Call]): Option[Call] =
    calls match {
      case Nil           => None
      case call :: calls =>
        if call.stationCrs == stationCode then Some(call)
        else getBoardCalls(calls)
    }
  getBoardCalls(service.calls)

def getAlightCallFromService(service: Service, boardCall: Call): Option[Call] =
  def getPossibleAlightCalls(
      calls: List[Call],
      boarded: Boolean,
      acc: IndexedSeq[Call]
  ): IndexedSeq[Call] =
    calls match {
      case Nil     => acc
      case c :: cs =>
        val boardedAtThisCall =
          c.stationCrs == boardCall.stationCrs && c.planDep == boardCall.planDep
        getPossibleAlightCalls(
          cs,
          boarded || boardedAtThisCall,
          if boarded then acc :+ c else acc
        )
    }
  val possibleAlightCalls =
    getPossibleAlightCalls(service.calls, false, IndexedSeq())
  val callStrings = possibleAlightCalls.zipWithIndex.foreach((call, i) =>
    val indexString = s"${i + 1}:".padTo(2, " ").mkString
    println(s"${indexString} ${call.stationName}")
  )
  io.StdIn.readLine("Alight station: ").toIntOption match {
    case None    => None
    case Some(i) => Some(possibleAlightCalls(i - 1))
  }
