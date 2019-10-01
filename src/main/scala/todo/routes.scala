package todo
import cats.data.NonEmptyList
import cats.effect._
import cats.implicits._
import io.chrisdavenport.log4cats._
import org.http4s.HttpRoutes
import tapir._
import tapir.json.circe._
import tapir.model.StatusCodes
import tapir.server.http4s._

object routes {
  val index = endpoint.get.in("").out(jsonBody[List[Todo]])

  val create = endpoint.post.in("").in(jsonBody[CreateTodo]).out(jsonBody[Todo])

  val deleteAll = endpoint.delete.in("")

  val baseTodos = endpoint.in("todos").in(path[Long])

  val get = baseTodos.get
    .out(jsonBodyOrNotFound[Todo])

  val update = baseTodos.patch
    .in(jsonBody[UpdateTodo])
    .out(jsonBodyOrNotFound[Todo])

  val delete = baseTodos.delete

  def todoRoutes[F[_]: Sync: ContextShift: Logger](alg: TodoAlgebra[F]): HttpRoutes[F] =
    NonEmptyList
      .of(
        index.toRoutes(_ => alg.list.logErrors),
        create.toRoutes(body => alg.create(body).logErrors),
        deleteAll.toRoutes(_ => alg.deleteAll.logErrors),
        get.toRoutes(id => alg.get(id).logErrors),
        update.toRoutes { case (id, updateTodo) => alg.update(id, updateTodo).logErrors },
        delete.toRoutes(id => alg.delete(id).logErrors),
      )
      .reduceK

  def todoUrl(id: Long): String =
    s"${config.baseUrl}/todos/${id}"

  def jsonBodyOrNotFound[A](implicit codec: Codec[Some[A], MediaType.Json, _]) =
    oneOf(
      statusMapping(StatusCodes.NotFound, plainBody[String].map(_ => None)(_ => "not found")),
      statusMapping(StatusCodes.Ok, jsonBody[Some[A]]),
    )

  private implicit class ErrorLoggingOps[F[_], A](self: F[A]) {
    def logErrors(implicit F: Sync[F], logger: Logger[F]) =
      self
        .handleErrorWith(err => logger.error(err)("Error in request") *> self)
        .attempt
        .map(_.leftMap(_ => ()))
  }
}
