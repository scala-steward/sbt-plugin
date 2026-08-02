name := "credentials"
scalaVersion := "2.13.18"
credentials += Credentials("Some Realm", "artifacts.example.com", "user", "secret")
externalResolvers += "Some Realm".at("artifacts.example.com")

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
       |{ "groupId": "org.scala-lang", "artifactId": { "name": "scala-library", "maybeCrossName": null }, "version": "2.13.18", "sbtVersion": null, "scalaVersion": null, "configurations": null }
       |{ "MavenRepository": { "name": "public", "location": "https://repo1.maven.org/maven2/", "headers": [ ] } }
       |{ "MavenRepository": { "name": "Some Realm", "location": "artifacts.example.com", "headers": [ ], "credentials": { "user": "user", "pass": "secret" } } }
       |""".stripMargin.trim

  if (obtained != expected) {
    val msg = s"""|Output mismatch!
                  |Expected:\n$expected
                  |Obtained:\n$obtained""".stripMargin
    s.log.error(msg)
    throw new Throwable(msg)
  }
}
