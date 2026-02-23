import utils.readFromFile
import google.auth.AuthClient
import google.calendar.CalendarClient
import google.calendar.Event
import google.calendar.EventTimeProtocol
import google.calendar.EventTime
import com.github.nscala_time.time.Imports._
import rtt.client.RttClient
import rtt.interactive.getStationDepartureFromList

val accountsOauthBaseUrl = "https://accounts.google.com/o/oauth2/v2"
val oauthApiBaseUrl = "https://oauth2.googleapis.com"

@main
def main(): Unit =
  val config = loadConfig()

  val authClient = AuthClient(
    accountsOauthBaseUrl,
    oauthApiBaseUrl,
    config.clientId,
    config.clientSecret,
    "google_tokens.json"
  )
  val calendarClient = CalendarClient("https://www.googleapis.com/calendar/v3")

  val accessToken = authClient.getAccessToken()

  val rttClient =
    RttClient("https://api.rtt.io/api/v1", config.rttUser, config.rttApiKey)

  val stationDepartures =
    rttClient.getDeparturesFromStation("BHM", DateTime.now())
  println(getStationDepartureFromList(stationDepartures))
