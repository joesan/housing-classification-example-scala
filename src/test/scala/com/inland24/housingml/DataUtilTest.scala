package com.inland24.housingml

import org.scalatest.FlatSpec

class DataUtilTest extends FlatSpec {

  val xs = Seq(1,2,3,4,5,6,7,8,9,10)
  val ys = Seq(10,12,15,9,13,11,15,11,12,14)

  "DataUtil.mean" should "calculate the mean for a sequence" in {
    val avg = DataUtil.mean(xs)
    assert(
      avg === 5.5,
      s"Expected the mean to be 5.5, but was $avg"
    )
  }

  "DataUtil.meanDiff" should "calculate the mean difference for a sequence" in {
    val diff = DataUtil.meanDiff(xs)
    assert(
      diff === Seq(-4.5, -3.5, -2.5, -1.5, -0.5, 0.5, 1.5, 2.5, 3.5, 4.5),
      s"Expected the mean difference to be Seq(-4.5, -3.5, -2.5, -1.5, -0.5, 0.5, 1.5, 2.5, 3.5, 4.5), but was $diff"
    )
  }
}