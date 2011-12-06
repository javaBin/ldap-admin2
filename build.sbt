organization := "no.bouvet"

name := "ldap-admin"

version := "0.1.0-SNAPSHOT"

scalaVersion := "2.9.1"

seq(webSettings :_*)

libraryDependencies ++= Seq(
  "org.scalatra" %% "scalatra" % "2.0.1",
  "org.scalatra" %% "scalatra-scalate" % "2.0.1",
  "org.squeryl" %% "squeryl" % "0.9.4",
  "org.apache.derby" % "derby" % "10.7.1.1",
  "commons-codec" % "commons-codec" % "1.5",
  "org.eclipse.jetty" % "jetty-webapp" % "7.4.5.v20110725" % "container",
  "javax.servlet" % "servlet-api" % "2.5" % "provided",
  "javax.mail" % "mail" % "1.4.4",
  "org.scalatest" %% "scalatest" % "1.6.1" % "test"
)

resolvers += "Sonatype OSS Snapshots" at "http://oss.sonatype.org/content/repositories/snapshots/"

resolvers += "Local Maven Repository" at "file://"+Path.userHome+"/.m2/repository"