package com.foodtime.creator.model

import java.util.UUID

sealed abstract class Event(val id: String) extends Product

final case class AddOrder(override val id: String) extends Event(id)
final case class AddCourier(override val id: String) extends Event(id)




