val scala3Version = "3.8.1"

lazy val root = project
  .in(file("."))
  .settings(
    name := "Google Calendar Trains",
    version := "0.1.0-SNAPSHOT",

    scalaVersion := scala3Version,

    libraryDependencies += "org.scalameta" %% "munit" % "1.0.0" % Test,
    libraryDependencies += "org.scala-lang" %% "toolkit" % "0.7.0",
    libraryDependencies += "com.github.nscala-time" %% "nscala-time" % "3.0.0",
    libraryDependencies += "io.spray" %% "spray-json" % "1.3.6"
  )
