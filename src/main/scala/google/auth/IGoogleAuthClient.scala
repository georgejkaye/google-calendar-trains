package google.auth

trait IGoogleAuthClient:
  def getAccessToken(): Either[String, String]
