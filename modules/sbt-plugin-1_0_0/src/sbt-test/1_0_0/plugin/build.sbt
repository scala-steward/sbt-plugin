scalaVersion := "2.12.21"
addSbtPlugin("com.eed3si9n" % "sbt-buildinfo" % "0.11.0")

val expected =
  """|--- snip ---
     |{ "groupId": "org.scala-lang", "artifactId": { "name": "scala-library", "maybeCrossName": null }, "version": "2.12.21", "sbtVersion": null, "scalaVersion": null, "configurations": null }
     |{ "groupId": "com.eed3si9n", "artifactId": { "name": "sbt-buildinfo", "maybeCrossName": null }, "version": "0.11.0", "sbtVersion": "1.0", "scalaVersion": "2.12", "configurations": null }
     |{ "MavenRepository": { "name": "public", "location": "https://repo1.maven.org/maven2/", "headers": [ ] } }
     |""".stripMargin.trim

@transient lazy val check = taskKey[Unit]("")
check := {
  val s = state.value
  val e = Project.extract(s)

  val charset = "UTF-8"
  val out = new java.io.ByteArrayOutputStream()
  val printStream = new java.io.PrintStream(out, true, charset)

  scala.Console.withOut(printStream)(e.runTask(stewardDependencies, s))

  val obtained = out.toString(charset).trim
  if (obtained != expected) {
    val msg = s"""|Output mismatch!
                  |Expected:\n$expected
                  |Obtained:\n$obtained""".stripMargin
    s.log.error(msg)
    throw new Throwable(msg)
  }
}
