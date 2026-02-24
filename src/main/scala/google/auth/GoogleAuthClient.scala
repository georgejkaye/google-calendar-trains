package google.auth

import sttp.client4.quick.*
import java.awt.Desktop
import java.net.URI
import scala.io.StdIn
import java.nio.file.{Paths, Files}
import spray.json._
import com.github.nscala_time.time.Imports.*

import GoogleAccessTokenAuthCodeProtocol._
import GoogleAccessTokenRefreshTokenProtocol._
import GoogleTokenProtocol._
import utils.writeToFile
import utils.fileExists
import utils.readFromFile

class GoogleAuthClient(
    accountsOauthBaseUrl: String,
    oauthApiBaseUrl: String,
    clientId: String,
    clientSecret: String,
    tokensFile: String
) extends IGoogleAuthClient:
  def getAccessToken(): String =
    val tokens = getGoogleTokensFromFile() match {
      case None =>
        val authCode = getAuthorisationCode(clientId)
        val newTokens =
          getAccessTokenResponseFromAuthCode(clientId, clientSecret, authCode)
        writeTokensToFile(newTokens)
        newTokens
      case Some(tokens) =>
        if (shouldRefreshToken(tokens)) {
          val newTokens = getAccessTokenResponseFromRefreshToken(
            clientId,
            clientSecret,
            tokens.refreshToken
          )
          writeTokensToFile(newTokens)
          newTokens
        } else {
          tokens
        }
    }
    tokens.accessToken

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
    val url = uri"$accountsOauthBaseUrl/auth?$queryParams"
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
    val url = uri"$oauthApiBaseUrl/token?$queryParams"
    val response = quickRequest
      .post(url)
      .send()
      .body
      .parseJson
      .convertTo[GoogleAccessTokenAuthCodeResponse]
    GoogleTokens(
      response.access_token,
      DateTime.now() + response.expires_in.seconds,
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
    val url = uri"$oauthApiBaseUrl/token?$queryParams"
    val response = quickRequest
      .post(url)
      .send()
      .body
      .parseJson
      .convertTo[GoogleAccessTokenRefreshTokenResponse]
    GoogleTokens(
      response.access_token,
      DateTime.now() + response.expires_in.seconds,
      refreshToken
    )

  def shouldRefreshToken(tokens: GoogleTokens): Boolean =
    DateTime.now() >= tokens.accessTokenExpires

  def writeTokensToFile(tokens: GoogleTokens) =
    writeToFile(tokensFile, tokens.toJson.prettyPrint)

  def getGoogleTokensFromFile(): Option[GoogleTokens] =
    if (!fileExists(tokensFile)) {
      None
    } else {
      val tokens =
        readFromFile(tokensFile).parseJson.convertTo[GoogleTokens]
      Some(tokens)
    }
