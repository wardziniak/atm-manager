import sbt.Keys._
import sbt.{Developer, ScmInfo, URL, url}

/**
  * Created by wardziniak on 28.09.2020.
  */
object Common {

  lazy val Settings = Seq(
    name                  := "atm-manager",
    organization          := "com.wardziniak",
    version               := Versions.Atm,
    organizationName      := "com.wardziniak",
    scalaVersion          := Versions.Scala,
    organizationHomepage  := Some(url("http://wardziniak.com")),
    scmInfo               := Some(ScmInfo(url("https://github.com/wardziniak/atm-manager"), "scm:git@github.com:wardziniak/atm-manager.git")),
    developers            := List(
      Developer(
        id    = "wardziniak",
        name  = "Bartosz Wardziński",
        email = "bwardziniak@yahoo.pl",
        url   = url("http://wardziniak.com")
      )
    ),
    description           := "Some description of project",
    licenses              := List("Apache 2" -> new URL("http://www.apache.org/licenses/LICENSE-2.0.txt")),
    homepage              := Some(url("https://github.com/wardziniak/atm-manager")),
    libraryDependencies   ++= Dependencies.AtmDependencies
  )
}
