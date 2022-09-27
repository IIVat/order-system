package com.foodtime.creator

import cats.effect.Async
import cats.implicits.toFunctorOps
import com.foodtime.creator.dao.EventDao
import com.foodtime.creator.model.{AddCourier, AddOrder, Event}

import java.util.UUID

trait EventHandler[F[_], T] {
  def handle(e: T): F[Boolean]
}

object EventHandler {
  def apply[F[_]: Async, T <: Event](eventDao: EventDao[F, T]): EventHandler[F, T] = new EventHandler[F, T] {
    override def handle(e: T): F[Boolean] =  eventDao.create(e).map(_ > 0)
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



