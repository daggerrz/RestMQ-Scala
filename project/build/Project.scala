import sbt._
class Project(info: ProjectInfo) extends DefaultProject(info) {
  val uf = "net.databinder" %% "unfiltered-server" % "0.1.4"
  val redis = "com.redis" %% "redisclient" % "1.0.2"
  val specs = "org.scala-tools.testing" %% "specs" % "1.6.5"

  val mockito = "org.mockito" % "mockito-core" % "1.8.5" % "test->default"
  
}