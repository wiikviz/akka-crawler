name := """crawler"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.11.7"

libraryDependencies ++= Seq(
  jdbc,
  cache,
  ws,
  "org.jsoup" % "jsoup" % "1.10+",
  "commons-validator" % "commons-validator" % "1.5+",
  "org.scalatest" %% "scalatest" % "2.2.6" % Test,
  "com.typesafe.akka" %% "akka-testkit" % "2.4+" % Test,
  "org.scalatestplus.play" %% "scalatestplus-play" % "1.5+" % Test,
  "org.scalamock" %% "scalamock-scalatest-support" % "3.2+" % Test
)

fork in run := true