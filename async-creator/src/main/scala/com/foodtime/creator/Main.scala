package com.foodtime.creator

import cats.effect.{ExitCode, IO, IOApp}
import com.foodtime.creator.dao.EventDao
import com.foodtime.creator.model.{AddCourier, AddOrder}
import dev.profunktor.redis4cats.Redis
import dev.profunktor.redis4cats.effect.Log.NoOp.instance
import fs2.aws.sqs.SqsConfig
import io.laserdisc.pure.sqs.tagless.{Interpreter, SqsAsyncClientOp}
import io.circe.generic.auto._
import software.amazon.awssdk.services.sqs.SqsAsyncClient

import scala.concurrent.duration.DurationInt

object Main extends IOApp{
  override def run(args: List[String]): IO[ExitCode] = {
    Redis[IO].utf8("redis://localhost").use { redisCommands =>
      val sqsAsyncClientOp: SqsAsyncClientOp[IO] = Interpreter[IO].create(SqsAsyncClient.builder().build())
      val config = SqsConfig("sqs://couriers")
      val courierProcessor = EventProcessor[IO, AddCourier](config, sqsAsyncClientOp)
      val orderProcessor = EventProcessor[IO, AddOrder](config, sqsAsyncClientOp)
      val courierDao = EventDao[IO, AddCourier]("courier",redisCommands)
      val orderDao = EventDao[IO, AddOrder]("order",redisCommands)
      val courierEventHandler = EventHandler[IO, AddCourier](courierDao)
      val orderEventHandler = EventHandler[IO, AddOrder](orderDao)

      fs2.Stream.awakeEvery[IO](1.second)
        .evalTap(_ => courierProcessor.process(courierEventHandler.handle))
        .evalTap(_ => orderProcessor.process(orderEventHandler.handle))
        .compile.drain.as(ExitCode.Success)
    }
  }
}
