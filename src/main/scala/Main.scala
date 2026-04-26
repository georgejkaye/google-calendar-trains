import utils.readFromFile
import config.ConfigLoader
import config.dotConfigLoader
import config.testConfigLoader
import google.auth.GoogleAuthClient
import google.calendar.GoogleCalendarClient
import google.calendar.Event
import google.calendar.EventTimeProtocol
import google.calendar.EventTime
import com.github.nscala_time.time.Imports._
import rtt.client.RttClient
import better.files._
import File._

val accountsOauthBaseUrl = "https://accounts.google.com/o/oauth2/v2"
val oauthApiBaseUrl = "https://oauth2.googleapis.com"

@main
def main(): Unit =
  Right(())
    .flatMap { _ =>
      val configLoader = dotConfigLoader
      configLoader.getConfig() match {
        case Some(c) => Right(c)
        case None    => Left("Could not get config")
      }
    }
    .flatMap { config =>
      val authClient = GoogleAuthClient(
        accountsOauthBaseUrl,
        oauthApiBaseUrl,
        config.clientId,
        config.clientSecret,
        home / ".config" / "caltrains" / "google_tokens.json"
      )
      val calendarClient = GoogleCalendarClient(
        "https://www.googleapis.com/calendar/v3"
      )
      val accessToken = authClient.getAccessToken()
      val rttClient =
        RttClient("https://api.rtt.io/api/v1", config.rttUser, config.rttApiKey)
      runProcess(config, authClient, calendarClient, rttClient)
    }
