package utils

import java.io.FileWriter
import java.io.File
import java.nio.file.Files
import java.nio.file.Paths

def writeToFile(filePath: String, contents: String) =
  val fileWriter = new FileWriter(new File(filePath))
  fileWriter.write(contents)
  fileWriter.close()

def readFromFile(filePath: String) =
  io.Source.fromFile("secret").getLines().next()

def fileExists(filePath: String) =
  Files.exists(Paths.get(filePath))
