import sbtcrossproject.{CrossProject, CrossType, Platform}
import sbtghactions.JavaSpec.Distribution.Adopt

/// variables

val groupId = "org.scala-steward"
val projectName = "sbt-plugin"
val rootPkg = groupId.replace("-", "")
val gitHubOwner = "scala-steward-org"
val gitHubUrl = s"https://github.com/$gitHubOwner/$projectName"

val moduleCrossPlatformMatrix: Map[String, List[Platform]] = Map(
  "sbt-plugin-2_0_0" -> List(JVMPlatform),
  "sbt-plugin-1_3_11" -> List(JVMPlatform),
  "sbt-plugin-1_0_0" -> List(JVMPlatform)
)

val Scala212 = "2.12.20"
val Scala3 = "3.7.3"

/// sbt-github-actions configuration

ThisBuild / crossScalaVersions := Seq(Scala212, Scala3)
ThisBuild / githubWorkflowPublishTargetBranches := Seq()
ThisBuild / githubWorkflowJavaVersions := Seq(JavaSpec(Adopt, "17"))
ThisBuild / githubWorkflowBuild := Seq(
  WorkflowStep.Sbt(List("validate"), name = Some("Build project"))
)

/// global build settings

ThisBuild / evictionErrorLevel := Level.Info

/// projects

lazy val root = project
  .in(file("."))
  .aggregate(
    `sbt-plugin-2_0_0`.jvm,
    `sbt-plugin-1_3_11`.jvm,
    `sbt-plugin-1_0_0`.jvm
  )
  .settings(commonSettings)
  .settings(noPublishSettings)

lazy val `sbt-plugin-2_0_0` = myCrossProject("sbt-plugin-2_0_0")
  .settings(noPublishSettings)
  .settings(
    scalaVersion := Scala3,
    sbtPlugin := true,
    // scala-steward:off
    pluginCrossBuild / sbtVersion := "2.0.0-RC3"
    // scala-steward:on
  )

lazy val `sbt-plugin-1_3_11` = myCrossProject("sbt-plugin-1_3_11")
  .settings(noPublishSettings)
  .settings(
    scalaVersion := Scala212,
    sbtPlugin := true,
    // scala-steward:off
    pluginCrossBuild / sbtVersion := "1.3.11"
    // scala-steward:on
  )

lazy val `sbt-plugin-1_0_0` = myCrossProject("sbt-plugin-1_0_0")
  .settings(noPublishSettings)
  .settings(
    scalaVersion := Scala212,
    sbtPlugin := true,
    // scala-steward:off
    pluginCrossBuild / sbtVersion := "1.0.0"
    // scala-steward:on
  )

/// settings

def myCrossProject(name: String): CrossProject =
  CrossProject(name, file(name))(moduleCrossPlatformMatrix(name): _*)
    .crossType(CrossType.Pure)
    .withoutSuffixFor(JVMPlatform)
    .in(file(s"modules/$name"))
    .settings(
      moduleName := s"$projectName-$name",
      moduleRootPkg := s"$rootPkg.${name.replace('-', '.')}"
    )
    .settings(commonSettings)

lazy val commonSettings = Def.settings(
  compileSettings,
  metadataSettings
)

lazy val compileSettings = Def.settings(
  scalaVersion := Scala212
)

lazy val metadataSettings = Def.settings(
  name := projectName,
  organization := groupId,
  homepage := Some(url(gitHubUrl)),
  startYear := Some(2018),
  licenses := List("Apache-2.0" -> url("http://www.apache.org/licenses/LICENSE-2.0")),
  scmInfo := Some(ScmInfo(homepage.value.get, s"scm:git:$gitHubUrl.git")),
  headerLicense := Some(HeaderLicense.ALv2("2018-2022", "Scala Steward contributors")),
  developers := List(
    Developer(
      id = "alejandrohdezma",
      name = "Alejandro Hernández",
      email = "",
      url = url("https://github.com/alejandrohdezma")
    ),
    Developer(
      id = "exoego",
      name = "TATSUNO Yasuhiro",
      email = "",
      url = url("https://github.com/exoego")
    ),
    Developer(
      id = "fthomas",
      name = "Frank S. Thomas",
      email = "",
      url = url("https://github.com/fthomas")
    ),
    Developer(
      id = "mzuehlke",
      name = "Marco Zühlke",
      email = "",
      url = url("https://github.com/mzuehlke")
    )
  )
)

lazy val noPublishSettings = Def.settings(
  publish / skip := true
)

/// setting keys

lazy val moduleRootPkg = settingKey[String]("").withRank(KeyRanks.Invisible)
moduleRootPkg := rootPkg

/// commands

def addCommandsAlias(name: String, cmds: Seq[String]) =
  addCommandAlias(name, cmds.mkString(";", ";", ""))

addCommandsAlias(
  "validate",
  Seq(
    "clean",
    "headerCheck",
    "scalafmtCheckAll",
    "scalafmtSbtCheck",
    "test"
  )
)

addCommandsAlias(
  "fmt",
  Seq(
    "headerCreate",
    "scalafmtAll",
    "scalafmtSbt"
  )
)
