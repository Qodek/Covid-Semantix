lazy val commonSettings = Seq(
  name := "covid-semantix",
  version := "0.1",
  organization := "als.saude.covid",
  scalaVersion := "2.11.12",
  test in assembly := {}
)

lazy val app = (project in file(".")).
  settings(commonSettings: _*).
  settings(
    libraryDependencies ++= Seq(
      "org.slf4j" % "slf4j-simple" % "1.7.28" % "provided",
      "org.apache.spark" %% "spark-core" % "2.4.4" % "provided",
      "org.apache.spark" %% "spark-sql" % "2.4.4" % "provided"
    ),
    assemblyMergeStrategy in assembly := {
      case PathList("javax", "servlet", xs @ _*)         => MergeStrategy.first
      case PathList(ps @ _*) if ps.last endsWith ".html" => MergeStrategy.first
      case "application.conf"                            => MergeStrategy.concat
      case "unwanted.txt"                                => MergeStrategy.discard
      case x =>
        val oldStrategy = (assemblyMergeStrategy in assembly).value
        oldStrategy(x)
    }
  )

