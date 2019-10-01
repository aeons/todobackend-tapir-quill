package todo
import cats.effect._
import cats.implicits._
import doobie.hikari.HikariTransactor
import doobie.util.ExecutionContexts
import doobie.util.transactor.Transactor
import org.flywaydb.core.Flyway

object db {
  case class DatabaseConfig(
      driver: String,
      url: String,
      user: String,
      pass: String,
  )

  def transactor[F[_]: Async: ContextShift](
      config: DatabaseConfig,
      blocker: Blocker,
  ): Resource[F, Transactor[F]] =
    for {
      ce <- ExecutionContexts.fixedThreadPool[F](32)
      xa <- HikariTransactor.newHikariTransactor[F](
        config.driver,
        config.url,
        config.user,
        config.pass,
        ce,
        blocker,
      )
    } yield xa

  def migrate[F[_]](config: DatabaseConfig)(implicit F: Sync[F]): F[Unit] =
    F.delay {
      val flyway = Flyway
        .configure()
        .dataSource(config.url, config.user, config.pass)
        .load()
      flyway.clean()
      flyway.migrate()
    }.void
}
