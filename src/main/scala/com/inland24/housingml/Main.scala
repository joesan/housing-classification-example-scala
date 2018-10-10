package com.inland24.housingml

import java.nio.file.Path

import com.typesafe.config.ConfigFactory

class Main {

  def main(args: Array[String]): Unit = {
    Option(System.getProperty("env", "")) match {
      case Some(envName) =>
        val config = ConfigFactory.load(s"application.${envName.toLowerCase}.conf")
      case None =>
        // TODO: log to the console and exit with a failure code
        System.exit(-1)
    }
  }

  def downloadFile(from: Path, to: Path) = ???

  def unzipFile(from: Path, to: Path) = ???

  def splitTestSet(from: Path, to: Path) = ???
}