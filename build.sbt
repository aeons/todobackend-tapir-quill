Global / cancelable := true
Global / onChangedBuildSource := ReloadOnSourceChanges
ThisBuild / turbo := true

lazy val root = project
  .in(file("."))
  .settings(
    // quill does not support 2.13
    scalaVersion := "2.12.10",
    libraryDependencies ++= List(
      // All used dependencies listed thanks to sbt-explicit-dependencies
      "ch.qos.logback"         % "logback-classic"      % "1.2.3" % Runtime,
      "com.propensive"         %% "magnolia"            % "0.12.0",
      "com.propensive"         %% "mercator"            % "0.2.1",
      "com.softwaremill.tapir" %% "tapir-core"          % "0.11.3",
      "com.softwaremill.tapir" %% "tapir-http4s-server" % "0.11.3",
      "com.softwaremill.tapir" %% "tapir-json-circe"    % "0.11.3",
      "com.zaxxer"             % "HikariCP"             % "3.4.1",
      "io.chrisdavenport"      %% "log4cats-core"       % "1.0.0",
      "io.chrisdavenport"      %% "log4cats-slf4j"      % "1.0.0",
      "io.circe"               %% "circe-core"          % "0.12.1",
      "io.circe"               %% "circe-derivation"    % "0.12.0-M7",
      "io.getquill"            %% "quill-core"          % "3.4.9",
      "io.getquill"            %% "quill-jdbc"          % "3.4.9",
      "io.getquill"            %% "quill-sql"           % "3.4.9",
      "org.flywaydb"           % "flyway-core"          % "6.0.4",
      "org.http4s"             %% "http4s-blaze-server" % "0.20.11",
      "org.http4s"             %% "http4s-core"         % "0.20.11",
      "org.http4s"             %% "http4s-server"       % "0.20.11",
      "org.slf4j"              % "slf4j-api"            % "1.7.28",
      "org.tpolecat"           %% "doobie-core"         % "0.8.4",
      "org.tpolecat"           %% "doobie-free"         % "0.8.4",
      "org.tpolecat"           %% "doobie-hikari"       % "0.8.4",
      "org.tpolecat"           %% "doobie-postgres"     % "0.8.4",
      "org.tpolecat"           %% "doobie-quill"        % "0.8.4",
      "org.typelevel"          %% "cats-core"           % "2.0.0",
      "org.typelevel"          %% "cats-effect"         % "2.0.0",
      "org.typelevel"          %% "cats-free"           % "2.0.0",
      compilerPlugin("com.olegpy" %% "better-monadic-for" % "0.3.1"),
    ),
    // Log queries at compile time
    Runtime / javaOptions += "-Dquill.binds.log=true",
  )
