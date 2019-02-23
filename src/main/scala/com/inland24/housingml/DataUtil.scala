package com.inland24.housingml

object DataUtil {

  import Numeric.Implicits._

  def mean[A: Numeric](xs: Seq[A]): Double = xs.sum.toDouble / xs.size

  // TODO: Check if the formula is correct! Write unit tests!
  def covariance[A: Numeric](xs: Seq[A], ys: Seq[A]): Double = {
    (meanDiff(xs) zip meanDiff(ys)).map {
      case (x, y) => x.toDouble() * y.toDouble()
    }.sum / xs.length - 1
  }

  def meanDiff[A: Numeric](elems: Seq[A]): Seq[Double] = {
    val avg = mean(elems)
    elems.map(_.toDouble()).map(elem => elem - avg)
  }

  def correlation[A: Numeric](xs: Seq[A], ys: Seq[A]): Double = {
    covariance(xs, ys) / standardDeviation(xs) * standardDeviation(ys)
  }

  def standardDeviation[A: Numeric](xs: Seq[A]): Double = {
    math.sqrt(meanDiff(xs).map(elem => math.pow(elem, 2)).sum)
  }
}