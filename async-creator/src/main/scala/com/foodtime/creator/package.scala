package com.foodtime

import java.util.UUID

package object creator {
  final case class Order()
  def productToMap(cc: Product): Map[String, String] =
    cc.productElementNames.zip(cc.productIterator).toMap.view.mapValues(_.toString).toMap

}
