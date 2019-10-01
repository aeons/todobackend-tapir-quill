package todo
import cats.effect._
import cats.implicits._
import doobie.util.transactor.Transactor
import io.chrisdavenport.log4cats.Logger
import org.http4s._
import org.http4s.implicits._
import org.http4s.server.HttpMiddleware
import org.http4s.server.blaze.BlazeServerBuilder
import org.http4s.server.middleware.{CORS, RequestLogger}

object server {
  def resource[F[_]: ConcurrentEffect: ContextShift: Timer: Logger](xa: Transactor[F]) =
    BlazeServerBuilder[F]
      .bindHttp(host = "0.0.0.0", port = 9000)
      .withHttpApp(todoApp[F](xa))
      .resource

  def middlewares[F[_]: Concurrent: Logger]: HttpMiddleware[F] = {
    val cors: HttpMiddleware[F]      = CORS(_)
    val logAction: String => F[Unit] = Logger[F].info(_)
    val logger: HttpMiddleware[F] =
      RequestLogger.httpRoutes(
        logHeaders = false,
        logBody = true,
        logAction = logAction.some,
      )
    cors.andThen(logger)
  }

  def todoApp[F[_]: Concurrent: ContextShift: Logger](xa: Transactor[F]): HttpApp[F] =
    middlewares[F].apply(routes.todoRoutes[F](TodoAlgebra.default[F](xa))).orNotFound
}
