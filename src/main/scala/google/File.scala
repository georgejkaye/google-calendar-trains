package google

import java.nio.file.Files
import java.nio.file.Paths
import java.io.FileWriter
import java.io.File
import spray.json._
import utils.readFromFile
import utils.fileExists

import GoogleTokenProtocol._
import utils.writeToFile

val googleTokensFile = "google_tokens.json"

def getClientIdFromFile(): String =
  readFromFile("client")

def getClientSecretFromFile(): String =
  readFromFile("secret")

def writeTokensToFile(tokens: GoogleTokens) =
  writeToFile(googleTokensFile, tokens.toJson.prettyPrint)

def getGoogleTokensFromFile(): Option[GoogleTokens] =
  if (!fileExists(googleTokensFile)) {
    None
  } else {
    val tokens =
      readFromFile(googleTokensFile).parseJson.convertTo[GoogleTokens]
    Some(tokens)
  }
