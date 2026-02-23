package google.auth

import sttp.client4.Request

trait IAuthHeaderAdder[T]:
  def addAuthHeader(accessToken: String): T

implicit class RequestAuthHeaderAdder[T](request: Request[T])
    extends IAuthHeaderAdder[Request[T]]:
  def addAuthHeader(accessToken: String): Request[T] =
    request.header("Authorization", s"Bearer $accessToken")
