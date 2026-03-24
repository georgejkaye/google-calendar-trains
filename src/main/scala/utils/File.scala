package utils

import java.io.FileWriter
import java.nio.file.Files
import java.nio.file.Paths
import better.files.File

def writeToFile(file: File, contents: String) =
  file.overwrite(contents)

def readFromFile(file: File): String =
  file.contentAsString()

def fileExists(file: File): Boolean =
  file.exists()
