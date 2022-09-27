package com.foodtime.creator.dao

import cats.effect.kernel.Async
import cats.implicits.{catsSyntaxApplicativeId, catsSyntaxFlatMapOps}
import com.foodtime.creator.model._
import com.foodtime.creator.productToMap
import dev.profunktor.redis4cats.RedisCommands

trait EventDao[F[_], T] {
  def create(event: T): F[Long]
}

object EventDao {
  def apply[F[_] : Async, T <: Event](eventName: String, redis: RedisCommands[F, String, String]): EventDao[F, T] = new EventDao[F, T] {
    override def create(event: T): F[Long] = {
      redis.hGetAll(s"$eventName-${event.id}") >>= { set =>
        if (set.isEmpty)
          redis.hSet(event.id, productToMap(event))
        else set.size.toLong.pure[F]
      }
    }
  }
}
