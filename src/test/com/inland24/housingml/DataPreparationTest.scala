package com.inland24.housingml

import org.scalatest.FlatSpec

import scala.util.Random


class DataPreparationTest extends FlatSpec {

  val testDataConfig = TestDataConfig(
    trainingSetRatio = 0.4 // corresponds that 40% is training dataset
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

  "splitTestData" should "split dataset into test and training" in {
    val dataSet = Seq(header) ++ testData
    val (training, test) = dataPreparation.splitData(dataSet.toList, testDataConfig)

    // Expect 40 records as training and the remaining 60 for test
    training.size === 40
    test.size === 60
  }
}