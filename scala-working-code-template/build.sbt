ThisBuild / version := "1.0.0"

ThisBuild / scalaVersion := "2.12.14"

lazy val root = (project in file("."))
  .settings(
    name := "BruteForce"
  )



// https://mvnrepository.com/artifact/org.apache.spark/spark-core
libraryDependencies += "org.apache.spark" %% "spark-core" % "3.1.2" % "provided"
// https://mvnrepository.com/artifact/org.apache.spark/spark-streaming-kafka-0-10
libraryDependencies += "org.apache.spark" %% "spark-streaming-kafka-0-10" % "3.1.2"
// https://mvnrepository.com/artifact/org.apache.spark/spark-streaming
libraryDependencies += "org.apache.spark" %% "spark-streaming" % "3.1.2" % "provided"
// https://mvnrepository.com/artifact/org.apache.spark/spark-sql
libraryDependencies += "org.apache.spark" %% "spark-sql" % "3.1.2" % "provided"
libraryDependencies += "org.apache.spark" %% "spark-sql-kafka-0-10" % "3.1.2" % "provided"
// https://mvnrepository.com/artifact/io.socket/socket.io-client
libraryDependencies += "io.socket" % "socket.io-client" % "2.0.1"
val AkkaVersion = "2.6.19"
libraryDependencies += "com.typesafe.akka" %% "akka-actor" % AkkaVersion
libraryDependencies += "com.typesafe.akka" %% "akka-stream" % AkkaVersion

resolvers += "Akka Repository" at "https://repo.akka.io/releases/"

