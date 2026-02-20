import http.getOAuthTokenFromFile
import http.getClientIdFromFile
import http.getAccessToken
import http.getClientSecretFromFile
import http.getToken

@main
def main(): Unit =
  val clientId = getClientIdFromFile()
  val clientSecret = getClientSecretFromFile()
  val authToken = getOAuthTokenFromFile()
  getToken(clientId)
  val accessToken = getAccessToken(clientId, clientSecret, authToken)
  println(accessToken)
