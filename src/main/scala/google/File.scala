package google

import java.nio.file.Files
import java.nio.file.Paths
import java.io.FileWriter
import java.io.File
import spray.json._
import utils.readFromFile

import GoogleTokenProtocol._

def getClientIdFromFile(): String =
  io.Source.fromFile("client").getLines().next()

def getOAuthTokenFromFile(): Option[String] =
  if Files.exists(Paths.get("token")) then
    val tokenLine = io.Source.fromFile("token").getLines().next()
    Some(tokenLine)
  else None

def writeTokensToFile(accessToken: String, refreshToken: String) =
  val fileWriter = new FileWriter(new File("access"))
  fileWriter.write(accessToken)
  fileWriter.close()

def getClientSecretFromFile(): String =
  io.Source.fromFile("secret").getLines().next()

def getGoogleTokensFromFile() =
  val token = readFromFile("google_tokens").parseJson.convertTo[GoogleTokens]
