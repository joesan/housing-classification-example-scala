package com.inland24.housingml

object DataUtil {

  def centroid[T](xs: Seq[T])(implicit T: Fractional[T]): T =
    T.div(xs.sum, T.fromInt(xs.size))
}