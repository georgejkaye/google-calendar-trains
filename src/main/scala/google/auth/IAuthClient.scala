package google.auth

trait IAuthClient:
  def getAccessToken(): String
