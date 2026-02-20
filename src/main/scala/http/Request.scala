package http

import sttp.client4.quick.*
import java.awt.Desktop
import java.net.URI
import spray.json._
import DefaultJsonProtocol._

case class AccessTokenResponse(
    access_token: String,
    expires_in: Int,
    refresh_token: String,
    scope: String,
    token_type: String
)

object AccessTokenResponseProtocol extends DefaultJsonProtocol {
  implicit val format: RootJsonFormat[AccessTokenResponse] = jsonFormat5(
    AccessTokenResponse.apply
  )
}

import AccessTokenResponseProtocol._

def getAccessToken(
    clientId: String,
    clientSecret: String,
    token: String
): String =
  val queryParams = Map(
    "client_id" -> clientId,
    "client_secret" -> clientSecret,
    "code" -> token,
    "grant_type" -> "authorization_code",
    "redirect_uri" -> "http://127.0.0.1",
    "code_verifier" -> "hello!"
  )
  val url = uri"https://oauth2.googleapis.com/token?$queryParams"
  val response = quickRequest.post(url).send().body
  val json = response.parseJson.convertTo[AccessTokenResponse]
  return json.access_token

def getToken(clientId: String) =
  val queryParams = Map(
    "client_id" -> clientId,
    "redirect_uri" -> uri"http://127.0.0.1",
    "response_type" -> "code",
    "scope" -> "https://www.googleapis.com/auth/calendar",
    "code_challenge" -> "hello!",
    "code_challenge_method" -> "plain"
  )
  val url = uri"https://accounts.google.com/o/oauth2/v2/auth?$queryParams"
  println(url)
  Desktop.getDesktop.browse(url.toJavaUri)
