name := """housing-classification-example-scala"""

version := "1.0-SNAPSHOT"

lazy val root = project in file(".")

scalaVersion := "2.12.3"

scalacOptions ++= Seq(
  "-language:implicitConversions",
  // turns all warnings into errors ;-)
  "-target:jvm-1.8",
  "-language:reflectiveCalls",
  "-Xfatal-warnings",
  // possibly old/deprecated linter options
  "-unchecked",
  "-deprecation",
  "-feature",
  "-Ywarn-adapted-args",
  "-Ywarn-dead-code",
  "-Ywarn-inaccessible",
  "-Ywarn-nullary-override",
  "-Ywarn-nullary-unit",
  "-Xlog-free-terms",
  // enables linter options
  "-Xlint:adapted-args", // warn if an argument list is modified to match the receiver
  "-Xlint:nullary-unit", // warn when nullary methods return Unit
  "-Xlint:inaccessible", // warn about inaccessible types in method signatures
  "-Xlint:nullary-override", // warn when non-nullary `def f()' overrides nullary `def f'
  "-Xlint:infer-any", // warn when a type argument is inferred to be `Any`
  "-Xlint:-missing-interpolator", // disables missing interpolator warning
  "-Xlint:doc-detached", // a ScalaDoc comment appears to be detached from its element
  "-Xlint:private-shadow", // a private field (or class parameter) shadows a superclass field
  "-Xlint:type-parameter-shadow", // a local type parameter shadows a type already in scope
  "-Xlint:poly-implicit-overload", // parameterized overloaded implicit methods are not visible as view bounds
  "-Xlint:option-implicit", // Option.apply used implicit view
  "-Xlint:delayedinit-select", // Selecting member of DelayedInit
  "-Xlint:by-name-right-associative", // By-name parameter of right associative operator
  "-Xlint:package-object-classes", // Class or object defined in package object
  "-Xlint:unsound-match" // Pattern match may not be typesafe
)

scalacOptions in Test ++= Seq("-Yrangepos")

javacOptions ++= Seq(
  "-Xlint:unchecked",
  "-Xlint:deprecation"
)

logLevel := Level.Info

// use logback.xml when running unit tests
javaOptions in Test += "-Dlogger.file=conf/logback-test.xml"

resolvers += "sonatype-releases" at "https://oss.sonatype.org/content/repositories/public/"

// set the main class for 'sbt run'
mainClass in (Compile, run) := Some("com.inland24.housingml.Main")

libraryDependencies ++= Seq(
  "com.typesafe" % "config" % "1.3.1",
  "com.github.pathikrit" %% "better-files" % "3.6.0",
  "org.apache.commons" % "commons-compress" % "1.14",
  "commons-io" % "commons-io" % "2.6",
  // Test dependencies
  "org.scalatest" %% "scalatest" % "3.0.1" % Test,
  "org.awaitility" % "awaitility" % "3.0.0" % Test
)