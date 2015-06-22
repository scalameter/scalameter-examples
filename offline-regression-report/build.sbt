lazy val Benchmark = config("bench") extend Test

/**  This allows running ScalaMeter benchmarks in separate sbt configuration.
  *  It means, that when you want run your benchmarks you should type `bench:test` in sbt console.
  */
lazy val offlineRegressionReport = Project(
  "offline-regression-report",
  file("."),
  settings = Defaults.coreDefaultSettings ++ Seq(
    name := "scalameter-examples",
    organization := "com.storm-enroute",
    scalaVersion := "2.11.4",
    scalacOptions ++= Seq("-deprecation", "-unchecked", "-feature", "-Xlint"),
    publishArtifact := false,
    libraryDependencies ++= Seq(
      "com.storm-enroute" %% "scalameter" % version.value % "bench" // ScalaMeter version is set in version.sbt
    ),
    resolvers ++= Seq(
      Opts.resolver.sonatypeReleases,
      Opts.resolver.sonatypeSnapshots
    ),
    testFrameworks += new TestFramework("org.scalameter.ScalaMeterFramework"),
    parallelExecution in Benchmark := false,
    logBuffered := false
  )
) configs (
  Benchmark
) settings(
  inConfig(Benchmark)(Defaults.testSettings): _*
)
