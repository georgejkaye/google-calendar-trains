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
): Unit =
  val googleAccessToken = authClient.getAccessToken()

  val stationCode = io.StdIn.readLine("Station code: ").toUpperCase()

  if stationCode.length() != 3 then return

  val runDateString = io.StdIn.readLine("Run date: ")

  val runDate =
    DateTimeFormat
      .forPattern("yyyy-MM-dd")
      .parseOption(runDateString) match {
      case None => {
        println("Invalid date format, expected yyyy-MM-dd")
        return
      }
      case Some(rd) => rd
    }

  val depTimeString = io.StdIn.readLine("Departure time: ")

  val depTime =
    DateTimeFormat
      .forPattern("HHmm")
      .parseOption(depTimeString) match {
      case None => {
        println("Invalid time format, expected HHmm")
        return
      }
      case Some(dt) => dt
    }

  val stationDepartures = rttClient.getDeparturesFromStation(
    stationCode,
    runDate.withTime(depTime.getHourOfDay(), depTime.getMinuteOfHour(), 0, 0)
  )
  val stationDeparture = getStationDepartureFromList(stationDepartures) match {
    case None => {
      println("No departure picked")
      return
    }
    case Some(dep) => dep
  }

  val service = rttClient.getServiceFromStationDeparture(stationDeparture)

  val boardCall = getBoardCallFromService(service, stationCode, depTime) match
    case None       => return
    case Some(call) => call

  val alightCall = getAlightCallFromService(service, boardCall) match
    case None       => return
    case Some(call) => call

  val journeyTitle = s"${boardCall.stationName} to ${alightCall.stationName}"

  val seats = io.StdIn.readLine("Seat reservations: ") match {
    case ""         => ""
    case seatString => s"\n\n$seatString"
  }
  val rttLink =
    s"https://www.realtimetrains.co.uk/service/gb-nr:${service.serviceUid}/${service.runDate.toString("yyyy-MM-dd")}/detailed"

  calendarClient.insertCalendarEvent(
    googleAccessToken,
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
