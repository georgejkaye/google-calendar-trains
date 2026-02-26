import utils.readFromFile
import google.auth.GoogleAuthClient
import google.calendar.GoogleCalendarClient
import google.calendar.Event
import google.calendar.EventTimeProtocol
import google.calendar.EventTime
import com.github.nscala_time.time.Imports._
import rtt.client.RttClient

val accountsOauthBaseUrl = "https://accounts.google.com/o/oauth2/v2"
val oauthApiBaseUrl = "https://oauth2.googleapis.com"

@main
def main(): Unit =
  val config = loadConfig()
  val authClient = GoogleAuthClient(
    accountsOauthBaseUrl,
    oauthApiBaseUrl,
    config.clientId,
    config.clientSecret,
    "google_tokens.json"
  )
  val calendarClient = GoogleCalendarClient(
    "https://www.googleapis.com/calendar/v3"
  )
  val accessToken = authClient.getAccessToken()
  val rttClient =
    RttClient("https://api.rtt.io/api/v1", config.rttUser, config.rttApiKey)
  runProcess(config, authClient, calendarClient, rttClient)
