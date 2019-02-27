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

    // Load the configuration & process
    AppConfig.load(ConfigUtil.loadFromEnv()) match {
      case Success(appCfg) =>
        // Delete all the directories first - Of course only if it exists
        val fileDir = File(appCfg.targetFilePath)
        if (fileDir.exists())
          fileDir.delete()

        // 0. We first create the directories if they do not exist
        val localDir = fileDir.createDirectoryIfNotExists(createParents = true)
        val dataPrep = DataPreparation(appCfg.testDataConfig)

        val result = for {
          // 1. Download the file and store it locally
          _ <- download(new URL(appCfg.sourceFileUrl), new JFile(localDir.toJava, appCfg.sourceFileName))

          // 2. Unzip the contents
          _ <- unzip(File(s"${localDir.path}/${appCfg.sourceFileName}"), File(s"${localDir.path}/housing.csv"))

          // 3. Split the training data and test data
          (training, test) = dataPrep.splitData(File(s"${localDir.path}/housing.csv"), appCfg.testDataConfig)

          // 4. Write the split data set to File system
          _ <- writeFile(File(s"${localDir.path}/training.csv"), training)
          _ <- writeFile(File(s"${localDir.path}/test.csv"), test)

          // 5. Clean the training data and write it to the File
          cleansedData = dataPrep.cleanTrainingData(File(s"${localDir.path}/training.csv"))
          _ <- writeFile(File(s"${localDir.path}/cleansedTraining.csv"), cleansedData)

          // 6. Encode the string features into binary
          encodedTestData = dataPrep.encodeDataSet(File(s"${localDir.path}/cleansedTraining.csv"))
          _ <- writeFile(File(s"${localDir.path}/encodedTraining.csv"), encodedTestData)

          // 6.1 We also need to Encode the string features into binary for the test (validation) data set
          // TODO: Write unit tests for this...
          encodedValidationData = dataPrep.encodeDataSet(File(s"${localDir.path}/test.csv"))
          _ <- writeFile(File(s"${localDir.path}/encodedTraining.csv"), encodedValidationData)
        } yield {
          println("Successfully ran the file pre-processing")
          println(s"Printing all the files that were produced ****** ")
          localDir.list.foreach(println)
          println(s"************************************************ ")
        }

        result.recover {
          case NonFatal(ex) =>
            ex.printStackTrace()
            //println(s"Some stupidity happened ${ex.getMessage} and the cause is ${ex.getCause}")
            System.exit(-1)
        }
      case Failure(ex) =>
        println(s"No environment variable setting found! So exiting run...." +
          s"failed with the error message ${ex.getMessage}")
        println("To run against a test environment configuration, use the following command:")
        println("sbt -Denv=test run")
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
}