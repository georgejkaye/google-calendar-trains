import google.getOAuthTokenFromFile
import google.getClientIdFromFile
import google.getClientSecretFromFile
import google.getAuthToken
import google.getAccessTokenResponse

@main
def main(): Unit =
  val clientId = getClientIdFromFile()
  val clientSecret = getClientSecretFromFile()
  val googleTokens = getGoogleTokens
    case None        => getAuthToken(clientId)
  }

  val accessToken = getAccessTokenResponse(clientId, clientSecret, authToken)
  println(accessToken)
