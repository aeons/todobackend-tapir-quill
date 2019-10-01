package todo
import cats.effect._
import cats.implicits._

object Main extends IOApp {
  def run(args: List[String]): IO[ExitCode] =
    TodoBackend.application[IO].use(_ => IO.never).as(ExitCode.Success)
}
