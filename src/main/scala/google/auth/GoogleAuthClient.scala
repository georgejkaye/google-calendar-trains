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
  def getAccessToken(): Either[String, String] =
    for {
      tokens <- getGoogleTokensFromFile() match {
        case None =>
          val authCode = getAuthorisationCode(clientId)
          getAccessTokenResponseFromAuthCode(
            clientId,
            clientSecret,
            authCode
          ).flatMap { newTokens =>
            writeTokensToFile(newTokens)
            Right(newTokens)
          }
        case Some(tokens) =>
          shouldRefreshToken(tokens) match {
            case true =>
              getAccessTokenResponseFromRefreshToken(
                clientId,
                clientSecret,
                tokens.refreshToken
              ).flatMap { newTokens =>
                writeTokensToFile(newTokens)
                Right(newTokens)
              }
            case false => Right(tokens)
          }
      }
      token <- Right(tokens.accessToken)
    } yield token

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
  ): Either[String, GoogleTokens] =
    val queryParams = Map(
      "client_id" -> clientId,
      "client_secret" -> clientSecret,
      "code" -> authCode,
      "grant_type" -> "authorization_code",
      "redirect_uri" -> "http://127.0.0.1",
      "code_verifier" -> codeVerifier
    )
    val url = uri"$oauthApiBaseUrl/token?$queryParams"
    basicRequest
      .post(url)
      .send()
      .body
      .flatMap { result =>
        val response = result.parseJson
          .convertTo[GoogleAccessTokenAuthCodeResponse]
        Right(
          GoogleTokens(
            response.access_token,
            DateTime.now() + response.expires_in.seconds,
            response.refresh_token
          )
        )
      }

  def getAccessTokenResponseFromRefreshToken(
      clientId: String,
      clientSecret: String,
      refreshToken: String
  ): Either[String, GoogleTokens] =
    val queryParams = Map(
      "client_id" -> clientId,
      "client_secret" -> clientSecret,
      "refresh_token" -> refreshToken,
      "grant_type" -> "refresh_token",
      "redirect_uri" -> "http://127.0.0.1"
    )
    val url = uri"$oauthApiBaseUrl/token?$queryParams"
    basicRequest
      .post(url)
      .send()
      .body
      .flatMap { body =>
        val response =
          body.parseJson
            .convertTo[GoogleAccessTokenRefreshTokenResponse]
        Right(
          GoogleTokens(
            response.access_token,
            DateTime.now() + response.expires_in.seconds,
            refreshToken
          )
        )
      }

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
