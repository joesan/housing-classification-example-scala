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
  val header
    : String = "housing.csv 000. XXX #### longitude,latitude,housing_median_age,total_rooms,total_bedrooms," +
    "population,households,median_income,median_house_value,ocean_proximity"

  def random(min: Int, max: Int): Int =
    min + (max - min) * Random.nextInt()

  def random(min: Double, max: Double): Double =
    min + (max - min) * Random.nextDouble()

  val testData: Seq[String] = (1 to 100) map { _ =>
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
            s",,${random(200, 1000)},,,NEAR OCEAN"
        }
        newElem
    }
    val cleansed =
      dataPreparation.cleanTrainingData(alteredFirstSet ++ remaining)
    cleansed.size === remaining.size // Expecting half of the elements to be removed
  }

  // TODO: under implementation currently!!!!
  "encodeData" should "convert the Ocean Proximities into 0 or 1 values with new headers" in {
    // First get the training dataset
    val (training, _) = splitDataSet()

    // Let us set our expectations (We collect indexes for comparison)
    val expectedOceanProximities = training.tail
      .map(elem => elem.split(",").last)
      .zipWithIndex // We want to know the positions so that we can compare against
      .groupBy(_._1)
      .map {
        case (key, value) => key -> value.unzip._2
      }

    // Now let's encode for the Ocean Proximity fields
    val encoded = dataPreparation.encodeDataSet(training)

    // We have 4 Ocean Proximity values, let us extract the encoded result (obviously the last 4 elements)
    val oceanProximitiesWithHeaders =
      encoded.map(elem => elem.split(",").takeRight(4).mkString(","))
    val actualOceanProximityHeaders = oceanProximitiesWithHeaders.head.split(",")
    val actualOceanProximities = oceanProximitiesWithHeaders.tail

    def isMatchFound(elems: Seq[(String, Int)], indexPosition: Int): Boolean = {
      elems.exists {
        case (elem, index) => elem == "1" && indexPosition == index
      }
    }

    // Stupid logic, but shit works!
    val collectedElems = actualOceanProximityHeaders.zipWithIndex.flatMap {
      headerElem =>
        val (oceanHeader, indexPosition) = headerElem
        actualOceanProximities.zipWithIndex.map(elem => {
          val (oceanVal, oceanValIndex) = elem
          if (isMatchFound(oceanVal.split(",").zipWithIndex.toSeq, indexPosition))
            oceanHeader -> oceanValIndex
          else
            "" -> -1
        })
    }.toSeq.filter(_._2 != -1).groupBy(_._1).map {
      case (key, value) => key -> value.unzip._2
    }

    // Finally check our expectations!
    collectedElems === expectedOceanProximities
  }
}
