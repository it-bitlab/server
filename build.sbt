ThisBuild / version := "latest"

ThisBuild / scalaVersion := "2.13.14"

enablePlugins(JavaServerAppPackaging, DockerPlugin)

dockerExposedPorts := Seq(5055)
dockerRepository := Some("redzor")
//dockerRepository := Some("cr.selcloud.ru/digitalyard/redzor")
//dockerBaseImage := "openjdk:8-jdk"
dockerBaseImage := "registry.access.redhat.com/ubi9/openjdk-21:1.20-2.1721207866"
dockerExposedVolumes := Seq("/files")


lazy val root = (project in file("."))
  .settings(
    name := "bitlab"
  )

scalacOptions += "-Ymacro-annotations"

val PekkoVersion = "1.0.3"
val PekkoHttpVersion = "1.0.1"
val AkkaHttpCors = "1.2.0"
val SLF4JVersion = "2.0.13"
val CirceVersion = "0.14.9"
val SimpleJavaMailVersion = "8.11.2"

libraryDependencies ++= Seq(
  "org.apache.pekko" %% "pekko-actor-typed" % PekkoVersion,
  "org.apache.pekko" %% "pekko-stream" % PekkoVersion,
  "org.apache.pekko" %% "pekko-http" % PekkoHttpVersion,
  "org.apache.pekko" %% "pekko-slf4j" % PekkoVersion,
  "org.apache.pekko" %% "pekko-http-spray-json" % PekkoHttpVersion,
  "org.apache.pekko" %% "pekko-http-cors" % PekkoHttpVersion,
  "org.slf4j" % "slf4j-api" % SLF4JVersion,
  "org.slf4j" % "slf4j-simple" % SLF4JVersion,
  "io.circe" %% "circe-core" % CirceVersion,
  "io.circe" %% "circe-generic" % CirceVersion,
  "io.circe" %% "circe-parser" % CirceVersion,
  "org.simplejavamail" % "simple-java-mail" %  SimpleJavaMailVersion
)
