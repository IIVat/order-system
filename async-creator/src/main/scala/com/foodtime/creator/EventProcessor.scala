package com.foodtime.creator

import cats.effect.Async
import cats.implicits.{catsSyntaxFlatMapOps, toFunctorOps}
import com.foodtime.creator.model.AddCourier
import fs2.Pipe
import fs2.aws.sqs.SqsConfig
import io.circe.Decoder
import io.laserdisc.pure.sqs.tagless.SqsAsyncClientOp
import software.amazon.awssdk.services.sqs.model.Message

trait EventProcessor[F[_], T] {
  def process(callback: T => F[Boolean]): F[Unit]
}

object EventProcessor {
  def apply[F[_] : Async, T: Decoder](sqsConfig: SqsConfig,
                                      client: SqsAsyncClientOp[F]): EventProcessor[F, T] =
    new EventProcessor[F, T] {
      override def process(callback: T => F[Boolean]): F[Unit] =
        fs2.aws.sqs.SQS.create(sqsConfig, client) >>= { sqsF =>
          sqsF.sqsStream
            .through(decoder)
            .through(handler(callback))
            .through(sqsF.deleteMessagePipe)
            .compile.drain
        }

      def decoder: Pipe[F, Message, (Message, T)] = {
        msq =>
          msq.map { msg =>
            io.circe.parser.decode[T](msg.body())
              .fold(
                err => throw new RuntimeException(err.getMessage),
                event => (msg, event)
              )
          }
      }

      def handler(callback: T => F[Boolean]): Pipe[F, (Message, T), Message] = src =>
        src.evalMap { case (m, e) => callback(e).map((_, m)) }
          .collect { case (is, m) if is => m }
    }
}
