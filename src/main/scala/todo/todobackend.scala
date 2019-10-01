package todo
import cats.effect._
import io.chrisdavenport.log4cats.Logger
import io.chrisdavenport.log4cats.slf4j.Slf4jLogger
import org.http4s.server.Server

object TodoBackend {
  def application[F[_]: ConcurrentEffect: Timer: ContextShift]: Resource[F, Server[F]] =
    for {
      implicit0(logger: Logger[F]) <- Resource.liftF(Slf4jLogger.create[F])
      blocker                         <- Blocker[F]
      _                               <- Resource.liftF(db.migrate(config.db))
      xa                              <- db.transactor(config.db, blocker)
      server                          <- todo.server.resource(xa)
    } yield server
}
