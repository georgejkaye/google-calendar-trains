package google

import spray.json._
import DefaultJsonProtocol._

case class GoogleAccessTokenAuthCodeResponse(
    access_token: String,
    expires_in: Int,
    refresh_token: String,
    scope: String,
    token_type: String
)

object GoogleAccessTokenAuthCodeProtocol extends DefaultJsonProtocol {
  implicit val format: RootJsonFormat[GoogleAccessTokenAuthCodeResponse] =
    jsonFormat5(
      GoogleAccessTokenAuthCodeResponse.apply
    )
}

case class GoogleAccessTokenRefreshTokenResponse(
    access_token: String,
    expires_in: Int,
    scope: String,
    token_type: String
)

object GoogleAccessTokenRefreshTokenProtocol extends DefaultJsonProtocol {
  implicit val format: RootJsonFormat[GoogleAccessTokenRefreshTokenResponse] =
    jsonFormat4(
      GoogleAccessTokenRefreshTokenResponse.apply
    )
}
