package com.inland24.housingml

object SimpleLinearRegression {

  import Numeric.Implicits._

  def simpleRegression(slope: Double, intercept: Double, x: Double): Double =
    slope * x + intercept

  def mean[A: Numeric](xs: Seq[A]): Double =
    xs.sum.toDouble / xs.size

  // TODO: Check if the formula is correct! Write unit tests!
  def covariance[A: Numeric](xs: Seq[A], ys: Seq[A]): Double = {ä
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

  def standardDeviation[A: Numeric](elems: Seq[A]): Double = {
    math.sqrt(meanDiff(elems).map(elem => math.pow(elem, 2)).sum)
  }

  def pearsonsCoefficient[A: Numeric](xs: Seq[A], ys: Seq[A]): Double = {
    val (meanDiffXs, meanDiffYs) = (meanDiff(xs), meanDiff(ys))
    val numerator = (meanDiffXs zip meanDiffYs).map {
      case (x, y) => x.toDouble() * y.toDouble()
    }.sum
    val denominator = math.sqrt(
      meanDiffXs.map(elem => math.pow(elem, 2)).sum * meanDiffYs.map(elem => math.pow(elem, 2)).sum
    )
    numerator / denominator
  }

  def slope[A: Numeric](xs: Seq[A], ys: Seq[A]): Double = {
    pearsonsCoefficient(xs, ys) * (standardDeviation(xs) / standardDeviation(ys))
  }

  def intercept(meanX: Double, meanY: Double, slope: Double): Double = meanY - meanX * slope

  def residualSumOfSquares[A:Numeric](actual: Seq[A], predicted: Seq[A]): Double = {
    math.pow((actual zip predicted).map {
      case (x, y) => x.toDouble() - y.toDouble()
    }.sum, 2)
  }

  def totalSumOfSquares[A: Numeric](dependantVariable: Seq[A]): Double =
    meanDiff(dependantVariable).sum
}