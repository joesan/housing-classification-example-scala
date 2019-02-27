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
    println(s"Before Split :: total size of CSV data is ${csvFile.lines.size}")
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
    val ttt = cleanTrainingData(csvFile.lines.toSeq)
    println(s"Total size of cleaned data is ${ttt.size}")
    ttt
  }

  def encodeDataSet(lines: Seq[String]): Seq[String] = {
    // We know that the Ocean Proximity is the last record in the given CSV and that needs to be encoded
    @tailrec
    def splitRecursively(acc: Seq[(String, String)], elems: List[String]): Seq[(String, String)] = elems match {
      case x :: xs =>
        val (first, last) = (x.split(",").toSeq.init.mkString(","), x.split(",").toSeq.last)
        splitRecursively(acc ++ Seq((first, last)), xs)
      case Nil => acc
    }

    val (data, oceanProximities) = splitRecursively(Seq.empty, lines.tail.toList).unzip
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

  def encodeDataSet(csvFile: File): Seq[String] = {
    val encoded = encodeDataSet(csvFile.lines.toSeq)
    println(s"Total size of encoded training data is ${encoded.size}")
    encoded
  }
}

object DataPreparation {

  def apply(testDataConfig: TestDataConfig) =
    new DataPreparation(testDataConfig)
}