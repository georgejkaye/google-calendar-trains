package google.auth

trait IGoogleAuthClient:
  def getAccessToken(): String
