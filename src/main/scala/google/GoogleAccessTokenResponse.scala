package google

import spray.json._
import DefaultJsonProtocol._

case class GoogleAccessTokenResponse(
    access_token: String,
    expires_in: Int,
    refresh_token: String,
    scope: String,
    token_type: String
)

object AccessTokenResponseProtocol extends DefaultJsonProtocol {
  implicit val format: RootJsonFormat[GoogleAccessTokenResponse] = jsonFormat5(
    GoogleAccessTokenResponse.apply
  )
}
