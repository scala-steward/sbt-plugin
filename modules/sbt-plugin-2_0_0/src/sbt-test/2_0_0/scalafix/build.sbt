scalaVersion := "3.8.4"
scalafixDependencies += "com.github.liancheng" %% "organize-imports" % "0.6.0"

val expected =
  """|--- snip ---
     |{ "groupId": "org.scala-lang", "artifactId": { "name": "scala3-library", "maybeCrossName": "scala3-library_3" }, "version": "3.8.4", "sbtVersion": null, "scalaVersion": null, "configurations": null }
     |{ "groupId": "com.github.liancheng", "artifactId": { "name": "organize-imports", "maybeCrossName": "organize-imports_2.13" }, "version": "0.6.0", "sbtVersion": null, "scalaVersion": null, "configurations": "scalafix-rule" }
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
