package com.inland24.housingml

import better.files.File

import scala.annotation.tailrec
import scala.util.Random

/**
  * DataPreparation - Can do the following:
  *
  * 1. Split the dataset into test and training
  * 2. Cleanse the data
  *
  * @param testDataConfig Configuration that we need to apply for the dataset
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
    // We know that the Ocean Proximity is the last record in the given CSV and that needs to be encoded
    val (data, oceanProximities) = lines.tail.map(elem => {
      val strs = elem.split(",")
      (strs.init.mkString(","), strs.last)
    }).unzip
    val oceanProximityHeaders = oceanProximities.distinct
    val newHeader = s"${lines.head.split(",").toSeq.init.mkString(",")},${oceanProximityHeaders.mkString(",")}"

    def toBinary(elem: String, compare: String) = {
      if (elem == compare) 1 else 0
    }

    @tailrec
    def recurse(acc: String, actual: String, elems: Seq[String]): String = elems match {
      case x :: xs => recurse(s"$acc,${toBinary(actual, x)}", actual, xs)
      case Nil => acc
    }

    val encoded = oceanProximities.map(ocean =>
      recurse("", ocean, oceanProximityHeaders))

    Seq(newHeader) ++ data.zip(encoded).map {
      case (datas, oceans) => datas + oceans
    }
  }

  def encodeTrainingData(csvFile: File): Seq[String] = {
    encodeTrainingData(csvFile.lines.toSeq)
  }
}

object DataPreparation {

  def apply(testDataConfig: TestDataConfig) =
    new DataPreparation(testDataConfig)
}