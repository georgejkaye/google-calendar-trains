package google

import sttp.client4.quick.*
import java.awt.Desktop
import java.net.URI
import scala.io.StdIn
import java.nio.file.{Paths, Files}
import spray.json._
import com.github.nscala_time.time.Imports.*

import GoogleAccessTokenAuthCodeProtocol._
import GoogleAccessTokenRefreshTokenProtocol._
import utils.writeToFile

val codeVerifier = "hello!"

def getAuthorisationCode(clientId: String): String =
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

def getAccessTokenResponseFromAuthCode(
    clientId: String,
    clientSecret: String,
    authCode: String
): GoogleTokens =
  val queryParams = Map(
    "client_id" -> clientId,
    "client_secret" -> clientSecret,
    "code" -> authCode,
    "grant_type" -> "authorization_code",
    "redirect_uri" -> "http://127.0.0.1",
    "code_verifier" -> codeVerifier
  )
  val url = uri"https://oauth2.googleapis.com/token?$queryParams"
  val response = quickRequest
    .post(url)
    .send()
    .body
    .parseJson
    .convertTo[GoogleAccessTokenAuthCodeResponse]
  GoogleTokens(
    response.access_token,
    DateTime.now() + response.expires_in.minutes,
    response.refresh_token
  )

def getAccessTokenResponseFromRefreshToken(
    clientId: String,
    clientSecret: String,
    refreshToken: String
): GoogleTokens =
  val queryParams = Map(
    "client_id" -> clientId,
    "client_secret" -> clientSecret,
    "refresh_token" -> refreshToken,
    "grant_type" -> "refresh_token",
    "redirect_uri" -> "http://127.0.0.1"
  )
  val url = uri"https://oauth2.googleapis.com/token?$queryParams"
  val response = quickRequest
    .post(url)
    .send()
    .body
    .parseJson
    .convertTo[GoogleAccessTokenRefreshTokenResponse]
  GoogleTokens(
    response.access_token,
    DateTime.now() + response.expires_in.minutes,
    refreshToken
  )

def shouldRefreshToken(tokens: GoogleTokens): Boolean =
  DateTime.now() >= tokens.accessTokenExpires
