ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "3.4.1"
lazy val root = (project in file("."))
  .settings(
    name := "euibuddy-chatbot",
    libraryDependencies ++= Seq(
      "org.scalatest" %% "scalatest" % "3.2.15" % Test
    ),
    // Add JVM options for proper UTF-8 encoding
    javaOptions ++= Seq(
      "-Dfile.encoding=UTF-8",
      "-Dsun.jnu.encoding=UTF-8"
    ),
    // Set console encoding
    scalacOptions ++= Seq("-encoding", "UTF-8"),
    // Enable forking to apply JVM options
    run / fork := true,
    // Set console output to use UTF-8
    console / scalacOptions ++= Seq("-Dfile.encoding=UTF-8")
  )

