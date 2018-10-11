package com.inland24.housingml

import java.io.{File => JFile}
import java.net.URL

import com.typesafe.config.{ConfigFactory, ConfigParseOptions, ConfigResolveOptions}

object Main {

  def main(args: Array[String]): Unit = {
    Option(System.getProperty("env", "")) match {
      case Some(envName) =>
        val config = ConfigFactory.load(
          s"application.${envName.toLowerCase}.conf",
          ConfigParseOptions.defaults().setAllowMissing(false),
          ConfigResolveOptions.defaults()
        ).resolve()
        // 1. Download the file and store it locally
        //val csvFile = downloadAndUnzip(File(config.getString("file.from.url")), config.getString("file.to.path"))
        // Check if we have the target dir's created, if not create them
        val fileDir = new JFile(config.getString("file.to.path"))
        if (!fileDir.exists()) fileDir.mkdirs()
        val fileTargetPath = new JFile(fileDir, config.getString("file.name"))
        val csvFile = download(new URL(config.getString("file.from.url")), fileTargetPath)
        println(s"CSV file downloaded is ${csvFile.!!}")

        // 2. Unzip the contents
        unzip(fileTargetPath, fileDir)
        System.exit(0)
      case None =>
        // TODO: log to the console and exit with a failure code
        System.exit(-1)
    }
  }

  def download(from: URL, to: JFile) = {
    import sys.process._
    from #> to
  }

  def unzip(from: JFile, to: JFile) = {
    FileUtils.extractTGZ(from.getAbsolutePath, to.getPath)
  }

  //def downloadAndUnzip(from: File, targetFileName: String) =
   // from.unGzipTo(File(targetFileName))

  def splitTestSet(csvFile: JFile) = ???
}