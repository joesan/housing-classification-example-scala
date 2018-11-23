package com.inland24.housingml

import java.io.{File => JFile}
import java.net.URL

import better.files._

import scala.language.postfixOps
import org.apache.commons.io.FileUtils

import scala.util.{Failure, Random, Success, Try}
import scala.util.control.NonFatal


object Main {

  def main(args: Array[String]): Unit = {

    // Load the configuration & process
    AppConfig.load(ConfigUtil.loadFromEnv()) match {
      case Success(appCfg) =>
        // 0. We first delete old data and create the directories fresh
        val fileDir = File(appCfg.targetFilePath)
        fileDir.delete()
        val localDir = fileDir.createDirectoryIfNotExists(createParents = true)

        val result = for {
          // 1. Download the file and store it locally
          _ <- download(new URL(appCfg.sourceFileUrl), new JFile(localDir.toJava, appCfg.sourceFileName))

          // 2. Unzip the contents
          _ <- unzip(File(s"${localDir.path}/${appCfg.sourceFileName}"), File(s"${localDir.path}/housing.csv"))

          // 3. Split the training data and test data
          (training, test) = splitData(File(s"${localDir.path}/housing.csv"))

          // 4. Write the split data set to File system
          _ <- writeFile(File(s"${localDir.path}/training.csv"), training)
          _ <- writeFile(File(s"${localDir.path}/test.csv"), test)

          // 5. Clean the training data and write it to the File
          cleansedData = cleanTrainingData(File(s"${localDir.path}/training.csv"))
          _ <- writeFile(File(s"${localDir.path}/cleansedTraining.csv"), cleansedData)
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

  def writeFile(file: File, elems: Seq[String]): Try[Unit] = Try {
    val lines = elems.map(_.mkString(",")).toString
    file.appendLines(lines)
  }
  def splitData(csvFile: File): (Seq[String], Seq[String])  = {
    val data = csvFile.lines.toSeq
    val trainingData = data.map(x => (Random.nextFloat(), x))
      .sortBy(_._1)
      .map(_._2)
      .take(3)
    (trainingData, data.filterNot(trainingData.toSet))
  }

  def cleanTrainingData(csvFile: File): Seq[String] = {
    csvFile.lines.toSeq.collect {
      case line if!line.split(",").toSeq.contains("") => line
    }
  }
}