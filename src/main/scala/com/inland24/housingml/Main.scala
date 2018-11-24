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
        // 0. We first create the directories if they do not exist
        val localDir = File(appCfg.targetFilePath).createDirectoryIfNotExists(createParents = true)

        val result = for {
          // 1. Download the file and store it locally
          _ <- download(new URL(appCfg.sourceFileUrl), new JFile(localDir.toJava, appCfg.sourceFileName))

          // 2. Unzip the contents
          _ <- unzip(File(s"${localDir.path}/${appCfg.sourceFileName}"), File(s"${localDir.path}/housing.csv"))

          // 3. Split the training data and test data
          (training, test) = splitData(File(s"${localDir.path}/housing.csv"), appCfg.testDataConfig)

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
    elems.foreach(elem => file.appendLine(elem))
  }

  def splitData(csvFile: File, testDataCfg: TestDataConfig): (Seq[String], Seq[String])  = {
    val lines = csvFile.lines.toList
    // We need to clean the header of this file
    val data = Seq(lines.head.substring(lines.head.indexOf("longitude"), lines.head.length).trim) ++ lines.drop(1)
    val tail = data.tail

    // Use seed such that we get the same data set always for training
    Random.setSeed(42)
    Random.shuffle(0 to tail.length - 1)
    // Using the ration, we can get the percentage of data that we will use for training
    val trainingSetSize = (tail.length * testDataCfg.trainingSetRatio).toInt

    val (trainingData, testData) = tail.splitAt(trainingSetSize)
    println(s">> Training Dataset Size is ${trainingData.length} >> Test Dataset size is ${testData.length}")
    (Seq(data.head) ++ trainingData, Seq(data.head) ++ testData)
  }

  def cleanTrainingData(csvFile: File): Seq[String] = {
    csvFile.lines.toSeq.collect {
      case line if!line.split(",").toSeq.contains("") => line
    }
  }
}