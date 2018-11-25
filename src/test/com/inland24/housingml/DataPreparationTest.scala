package com.inland24.housingml

import org.scalatest.FlatSpec

import scala.util.Random


class DataPreparationTest extends FlatSpec {

  val header = "longitude,latitude,housing_median_age,total_rooms,total_bedrooms," +
    "population,households,median_income,median_house_value,ocean_proximity"

  def random(min: Int, max: Int): Int =
    min + (max - min) * Random.nextInt()

  def random(min: Double, max: Double): Double =
    min + (max - min) * Random.nextDouble()

  def random = ??? // Gives a random String

  val testData = (1 to 100) map {
    case _ => s"${random(-180.0, 180.0)}," +
      s"${random(-90.0, 90.0)}," +
      s"${random(1, 50)}," +
      s"${random(1000, 10000)}," +
      s"${random(800, 2000)}," +
      s"${random(600, 2000)}," +
      s"${random(200, 1000)}," +
      s"${random(2.0, 12.0)}," +
      s"${random(100000, 400000)}," +
      s"${random()}"
  }

  "" should "" in {

  }
}