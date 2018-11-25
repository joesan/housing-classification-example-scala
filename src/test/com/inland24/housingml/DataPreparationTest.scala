package com.inland24.housingml

import org.scalatest.FlatSpec

import scala.util.Random


class DataPreparationTest extends FlatSpec {

  val testDataConfig = TestDataConfig(
    trainingSetRatio = 0.4, // corresponds that 40% is training dataset
    dataCleansingStrategy = RemoveEmptyRows
  )
  val dataPreparation = DataPreparation(testDataConfig)
  val oceanProximity = Seq("<1H OCEAN", "NEAR OCEAN", "INLAND", "NEAR BAY")
  val header: String = "housing.csv 000. XXX #### longitude,latitude,housing_median_age,total_rooms,total_bedrooms," +
    "population,households,median_income,median_house_value,ocean_proximity"

  def random(min: Int, max: Int): Int =
    min + (max - min) * Random.nextInt()

  def random(min: Double, max: Double): Double =
    min + (max - min) * Random.nextDouble()

  val testData: Seq[String] = (1 to 100) map {
    _ =>
      s"${random(-180.0, 180.0)}," +
        s"${random(-90.0, 90.0)}," +
        s"${random(1, 50)}," +
        s"${random(1000, 10000)}," +
        s"${random(800, 2000)}," +
        s"${random(600, 2000)}," +
        s"${random(200, 1000)}," +
        s"${random(2.0, 12.0)}," +
        s"${random(100000, 400000)}," +
        s"${oceanProximity(Random.nextInt(oceanProximity.length))}"
  }

  def splitDataSet(): (Seq[String], Seq[String]) = {
    dataPreparation.splitData((Seq(header) ++ testData).toList, testDataConfig)
  }

  "splitTestData" should "split dataset into test and training" in {
    val (training, test) = splitDataSet()

    // Expect 40 records as training and the remaining 60 for test
    training.size === 40
    test.size === 60

    // TODO: Since we use a seed to split the dataset, we should be consistent
  }

  "cleanseData" should "clean training dataset using clean strategy" in {
    // First get the training dataset
    val (training, _) = splitDataSet()

    // Now let's set some fields in the testData to empty for the first 20 records
    val (first, remaining) = training.splitAt(20)
    val alteredFirstSet = first.zipWithIndex.map {
      case (_, index) =>
        val newElem = if (index % 2 == 0) {
          s"${random(-180.0, 180.0)},${random(-90.0, 90.0)},,${random(1000, 10000)},${random(800, 2000)}," +
            s",${random(600, 2000)},${random(200, 1000)},,,NEAR BAY"
        } else {
          s"${random(-180.0, 180.0)},${random(-90.0, 90.0)},${random(1000, 10000)},,${random(800, 2000)}," +
            s",,${random(200, 1000)},,,NEAR BAY"
        }
        newElem
    }
    val cleansed = dataPreparation.cleanTrainingData(alteredFirstSet ++ remaining)
    cleansed.size === remaining.size // Expecting half of the elements to be removed
  }
}