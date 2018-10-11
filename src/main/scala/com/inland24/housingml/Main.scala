package com.inland24.housingml

import java.io.{File => JFile}
import java.net.URL

import better.files._

import scala.language.postfixOps
import com.typesafe.config.{ConfigFactory, ConfigParseOptions, ConfigResolveOptions}
import org.apache.commons.io.FileUtils


object Main {

  def main(args: Array[String]): Unit = {
    Option(System.getProperty("env", "")) match {
      case Some(envName) =>
        val config = ConfigFactory.load(
          s"application.${envName.toLowerCase}.conf",
          ConfigParseOptions.defaults().setAllowMissing(false),
          ConfigResolveOptions.defaults()
        ).resolve()
        val fileName = config.getString("file.name")

        // Check if we have the target dir's created, if not create them
        val localDir = File(config.getString("file.to.path")).createDirectories()

        // 1. Download the file and store it locally
        download(new URL(config.getString("file.from.url")), new JFile(localDir.toJava, fileName))

        // 2. Unzip the contents
        unzip(File(s"${localDir.path}/$fileName"), File(s"${localDir.path}/housing.csv"))

        println()
      case None =>
        println("No environment variable setting found! So exiting run...")
        println("Usage: sbt -Denv=test run")
        System.exit(-1)
    }
  }

  def download(from: URL, to: JFile) = {
    FileUtils.copyURLToFile(from, to, 20000, 20000)
  }

  def unzip(from: File, to: File) =
    from.unGzipTo(to)

  def splitTestSet(csvFile: JFile) = ???
}