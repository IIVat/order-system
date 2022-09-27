package com.foodtime.couriers

import java.util.UUID

trait CourierService[F[_]] {
  def markAvailability(isAvailable: Boolean): F[Boolean]
  def getCourier(orderId: UUID): F[Option[Courier]]
  def getOrders(courierId: UUID): F[List[Order]]
}

object CourierService {
  def impl[F[_]] = new CourierService[F] {
    override def markAvailability(isAvailable: Boolean): F[Boolean] = ???

    override def getCourier(orderId: UUID): F[Option[Courier]] = ???

    override def getOrders(courierId: UUID): F[List[Order]] = ???
  }
}

/**Tables:
 *  - Assignment table:
 *    - courierId
 *    - orderId
 *
 *  -Courier table:
 *   - id
 *   - data
 *
 *  -order table
 *   - id
 *   -data
 */



