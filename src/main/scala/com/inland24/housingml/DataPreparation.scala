package com.inland24.housingml

import better.files.File
import breeze.linalg._

import scala.util.Random

/**
  * DataPreparation - Can do the following:
  *
  * 1. Split the dataset into test and training
  * 2. Cleanse the data
  *
  * @param testDataConfig
  */
final class DataPreparation(testDataConfig: TestDataConfig) {

  def splitData(lines: List[String], testDataCfg: TestDataConfig): (Seq[String], Seq[String]) = {
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

  def splitData(csvFile: File, testDataCfg: TestDataConfig): (Seq[String], Seq[String])  = {
    splitData(csvFile.lines.toList, testDataCfg)
  }

  // TODO: Implementation pending!!!!!
  def cleanTrainingData(lines: Seq[String]): Seq[String] = testDataConfig.dataCleansingStrategy match {
    case _ =>
      lines.collect {
        case line if!line.split(",").toSeq.contains("") => line
      }
  }

  def cleanTrainingData(csvFile: File): Seq[String] = {
    // TODO: Using breeze library to work with data cleaning
    // 0. First load the data as a dense matrix using the Breeze library
    // val matrix = csvread(csvFile.toJava, ',')
    cleanTrainingData(csvFile.lines.toSeq)
  }

  def encodeTrainingData(lines: Seq[String]): Seq[String] = {
    // TODO: We#ve got to implement a simplified OneHotEncoder damn it!
  }

  def encodeTrainingData(csvFile: File): Seq[String] = {
    encodeTrainingData(csvFile.lines.toSeq)
  }
}

object DataPreparation {

  def apply(testDataConfig: TestDataConfig) =
    new DataPreparation(testDataConfig)
}