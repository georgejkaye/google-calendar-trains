import google.getClientIdFromFile
import google.getClientSecretFromFile
import google.getGoogleTokensFromFile
import google.getAuthorisationCode
import google.getAccessTokenResponseFromAuthCode
import google.getAccessTokenResponseFromRefreshToken
import google.writeTokensToFile
import google.shouldRefreshToken

@main
def main(): Unit =
  val clientId = getClientIdFromFile()
  val clientSecret = getClientSecretFromFile()
  val googleTokens = getGoogleTokensFromFile() match {
    case None =>
      val authCode = getAuthorisationCode(clientId)
      val tokens =
        getAccessTokenResponseFromAuthCode(clientId, clientSecret, authCode)
      writeTokensToFile(tokens)
      tokens
    case Some(tokens) =>
      if (shouldRefreshToken(tokens)) {
        val newTokens = getAccessTokenResponseFromRefreshToken(
          clientId,
          clientSecret,
          tokens.refreshToken
        )
        writeTokensToFile(newTokens)
        newTokens
      } else {
        tokens
      }
  }
  println(googleTokens.accessToken)
