package utils

import java.io.FileWriter
import java.io.File
import java.nio.file.Files
import java.nio.file.Paths

def writeToFile(filePath: String, contents: String) =
  val fileWriter = new FileWriter(new File(filePath))
  fileWriter.write(contents)
  fileWriter.close()

def readFromFile(filePath: String): String =
  val source = io.Source.fromFile(filePath)
  val lines =
    try source.mkString
    finally source.close()
  lines

def fileExists(filePath: String): Boolean =
  Files.exists(Paths.get(filePath))
