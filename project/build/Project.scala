import sbt._
class Project(info: ProjectInfo) extends DefaultProject(info) {
  val jbossRepo = "JBoss Repo" at "https://repository.jboss.org/nexus/content/groups/public/"

  val uf = "net.databinder" %% "unfiltered-server" % "0.1.4"
  val redis = "com.redis" %% "redisclient" % "1.5-SNAPSHOT"
  val specs = "org.scala-tools.testing" %% "specs" % "1.6.5"
  val mockito = "org.mockito" % "mockito-core" % "1.8.5" % "test->default"
  val netty = "org.jboss.netty" % "netty" % "3.2.1.Final" withSources()
}
