package google

import spray.json._
import com.github.nscala_time.time.Imports._

import utils.DateTimeProtocol.DateTimeFormat

case class GoogleTokens(
    accessToken: String,
    accessTokenExpires: DateTime,
    refreshToken: String
)

object GoogleTokenProtocol extends DefaultJsonProtocol {
  implicit val format: RootJsonFormat[GoogleTokens] = jsonFormat3(
    GoogleTokens.apply
  )
}
