package google

import sttp.client4.quick.*
import java.awt.Desktop
import java.net.URI
import scala.io.StdIn
import java.nio.file.{Paths, Files}
import spray.json._

import AccessTokenResponseProtocol._
import utils.writeToFile

val codeVerifier = "hello!"

def getAuthToken(clientId: String) =
  val queryParams = Map(
    "client_id" -> clientId,
    "redirect_uri" -> uri"http://127.0.0.1",
    "response_type" -> "code",
    "scope" -> "https://www.googleapis.com/auth/calendar",
    "code_challenge" -> codeVerifier,
    "code_challenge_method" -> "plain"
  )
  val url = uri"https://accounts.google.com/o/oauth2/v2/auth?$queryParams"
  println(s"Visit $url and then paste the token below:")
  StdIn.readLine()

def getAccessTokenResponse(
    clientId: String,
    clientSecret: String,
    token: String
): GoogleAccessTokenResponse =
  val queryParams = Map(
    "client_id" -> clientId,
    "client_secret" -> clientSecret,
    "code" -> token,
    "grant_type" -> "authorization_code",
    "redirect_uri" -> "http://127.0.0.1",
    "code_verifier" -> codeVerifier
  )
  val url = uri"https://oauth2.googleapis.com/token?$queryParams"
  val response = quickRequest.post(url).send().body
  response.parseJson.convertTo[GoogleAccessTokenResponse]
