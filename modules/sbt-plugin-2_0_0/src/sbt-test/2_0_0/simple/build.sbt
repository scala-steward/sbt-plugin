name := "simple"
scalaVersion := "3.8.4"
libraryDependencies += "org.apache.commons" % "commons-lang3" % "3.20.0"

@transient lazy val check = taskKey[Unit]("")
check := {
  val s = state.value
  val e = Project.extract(s)

  val charset = "UTF-8"
  val out = new java.io.ByteArrayOutputStream()
  val printStream = new java.io.PrintStream(out, true, charset)

  scala.Console.withOut(printStream) {
    e.runTask(stewardDependencies, s)
  }

  val obtained = out.toString(charset).trim
  val expected =
    """|--- snip ---
       |{ "groupId": "org.scala-lang", "artifactId": { "name": "scala3-library", "maybeCrossName": "scala3-library_3" }, "version": "3.8.4", "sbtVersion": null, "scalaVersion": null, "configurations": null }
       |{ "groupId": "org.apache.commons", "artifactId": { "name": "commons-lang3", "maybeCrossName": null }, "version": "3.20.0", "sbtVersion": null, "scalaVersion": null, "configurations": null }
       |{ "MavenRepository": { "name": "public", "location": "https://repo1.maven.org/maven2/", "headers": [ ] } }
       |""".stripMargin.trim

  if (obtained != expected) {
    val msg = s"""|Output mismatch!
                  |Expected:\n$expected
                  |Obtained:\n$obtained""".stripMargin
    s.log.error(msg)
    throw new Throwable(msg)
  }
}
