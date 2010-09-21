import sbt._
class Project(info: ProjectInfo) extends DefaultProject(info) {
  val jbossRepo = "JBoss Repo" at "https://repository.jboss.org/nexus/content/groups/public/"

  val redis = "com.redis" %% "redisclient" % "1.5-SNAPSHOT"
  val netty = "org.jboss.netty" % "netty" % "3.2.1.Final" withSources()
  val jetty = "org.eclipse.jetty" % "jetty-server" % "7.0.2.v20100331" withSources()

  val unfilteredVersion = "0.2.0-SNAPSHOT"

  val unfiltered = "net.databinder" %% "unfiltered" % unfilteredVersion withSources()

  val unfiltered_netty = "net.databinder" %% "unfiltered-netty" % unfilteredVersion withSources()

  val unfiltered_jetty = "net.databinder" %% "unfiltered-jetty" % unfilteredVersion withSources()
  val unfiltered_filter = "net.databinder" %% "unfiltered-filter" % unfilteredVersion withSources()

  val specs = "org.scala-tools.testing" %% "specs" % "1.6.5" % "test->default"
  val mockito = "org.mockito" % "mockito-core" % "1.8.5" % "test->default"
}
