package com.inland24.housingml

import java.io.{File => JFile}
import java.net.URL

import better.files._

import scala.language.postfixOps
import org.apache.commons.io.FileUtils

import scala.util.{Failure, Success, Try}
import scala.util.control.NonFatal


object Main {

  def main(args: Array[String]): Unit = {

    // 1. Load the configuration
    AppConfig.load(ConfigUtil.loadFromEnv()) match {
      case Success(appCfg) =>
        // 0. Check if we have the target dir's created, if not create them
        val localDir = File(appCfg.targetFilePath).createDirectories()

        println(s"Created the directories under ${localDir.path.toString}")

        val result = for {
          // 1. Download the file and store it locally
          _ <- download(new URL(appCfg.sourceFileUrl), new JFile(localDir.toJava, appCfg.sourceFileName))
          // 2. Unzip the contents
          _ <- unzip(File(s"${localDir.path}/${appCfg.sourceFileName}"), File(s"${localDir.path}/housing.csv"))
        } yield {
          println("Successfully ran the file pre-processing")
        }

        localDir.list.foreach(println)

        result.recover {
          case NonFatal(ex) =>
            println(s"Some stupidity happened ${ex.getMessage}")
            System.exit(-1)
        }
      case Failure(ex) =>
        println(s"No environment variable setting found! So exiting run...." +
          s"failed with the error message ${ex.getMessage}")
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

  def splitTestSet(csvFile: File) = {
    val lines = csvFile.lines
  }
}