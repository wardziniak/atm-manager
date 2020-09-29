
lazy val root = (project in file("."))
  .settings(Common.Settings: _*).enablePlugins(DockerPlugin).enablePlugins(JavaAppPackaging)

