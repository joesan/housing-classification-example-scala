package com.inland24.housingml

import better.files._
import com.typesafe.config.ConfigFactory

class Main {

  def main(args: Array[String]): Unit = {
    Option(System.getProperty("env", "")) match {
      case Some(envName) =>
        val config = ConfigFactory.load(s"application.${envName.toLowerCase}.conf")
        // 1. Download the file and store it locally
        val csvFile = downloadAndUnzip(File(config.getString("from")), config.getString("targetFilePath"))
      case None =>
        // TODO: log to the console and exit with a failure code
        System.exit(-1)
    }
  }

  def downloadAndUnzip(from: File, targetFileName: String) =
    from.unGzipTo(File(targetFileName))

  def splitTestSet(csvFile: File) = ???
}