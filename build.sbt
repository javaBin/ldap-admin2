organization := "no.java"

name := "ldap-admin"

version := "0.1.0-SNAPSHOT"

scalaVersion := "2.9.1"

seq(webSettings :_*)

libraryDependencies ++= Seq(
    "javax.mail" % "mail" % "1.4.4",
    "commons-codec" % "commons-codec" % "1.5",
    "org.scalatra" % "scalatra" % "2.1.0",
    "org.scalatra" % "scalatra-scalate" % "2.1.0",
    "org.scalatra" % "scalatra-specs2" % "2.1.0" % "test",
    "ch.qos.logback" % "logback-classic" % "1.0.6" % "runtime",
    "org.eclipse.jetty" % "jetty-webapp" % "8.1.5.v20120716" % "container",
    "org.eclipse.jetty" % "test-jetty-servlet" % "8.1.5.v20120716" % "test",
    "org.eclipse.jetty.orbit" % "javax.servlet" % "3.0.0.v201112011016" % "container;provided;test" artifacts (Artifact("javax.servlet", "jar", "jar"))
)