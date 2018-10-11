package com.inland24.housingml

import java.io.{File => JFile}
import java.net.URL

import better.files._

import scala.language.postfixOps
import com.typesafe.config.{ConfigFactory, ConfigParseOptions, ConfigResolveOptions}
import org.apache.commons.io.FileUtils

import scala.util.Try
import scala.util.control.NonFatal


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
        val downloadURL = config.getString("file.from.url")
        val localDirPath = config.getString("file.to.path")

        // 0. Check if we have the target dir's created, if not create them
        val localDir = File(localDirPath).createDirectories()

        val result = for {
          // 1. Download the file and store it locally
          _ <- download(new URL(downloadURL), new JFile(localDir.toJava, fileName))
          // 2. Unzip the contents
          _ <- unzip(File(s"${localDir.path}/$fileName"), File(s"${localDir.path}/housing.csv"))
        } yield {
          println("Successfully ran the file pre-processing")
        }

        result.recover {
          case NonFatal(ex) =>
            println(s"Some stupidity happened ${ex.getMessage}")
            System.exit(-1)
        }
      case None =>
        println("No environment variable setting found! So exiting run...")
        println("Usage: sbt -Denv=test run")
        System.exit(-1)
    }
  }

  def download(from: URL, to: JFile): Try[Unit] = {
    Try {
      if (!to.exists())
        FileUtils.copyURLToFile(from, to, 20000, 20000)
    }
  }

  def unzip(from: File, to: File): Try[Unit] = {
    Try { from.unGzipTo(to) }
  }

  def splitTestSet(csvFile: JFile) = ???
}