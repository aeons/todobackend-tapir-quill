package todo
import cats.effect._
import cats.implicits._
import doobie._
import doobie.implicits._
import doobie.quill.DoobieContext
import io.getquill.{idiom => _, _}

trait TodoAlgebra[F[_]] {
  def list: F[List[Todo]]
  def get(id: Long): F[Option[Todo]]
  def create(createTodo: CreateTodo): F[Todo]
  def delete(id: Long): F[Unit]
  def deleteAll: F[Unit]
  def update(id: Long, updateTodo: UpdateTodo): F[Option[Todo]]
}

object TodoAlgebra {
  val ctx = new DoobieContext.Postgres(CompositeNamingStrategy2(SnakeCase, Escape))

  def default[F[_]](xa: Transactor[F])(implicit F: Bracket[F, Throwable]): TodoAlgebra[F] =
    new TodoAlgebra[F] {
      import ctx._

      def list: F[List[Todo]] =
        run(queries.all).transact(xa)

      def get(id: Long): F[Option[Todo]] =
        run(queries.get(id)).map(_.headOption).transact(xa)

      def create(createTodo: CreateTodo): F[Todo] =
        run(queries.create(createTodo)).transact(xa)

      def delete(id: Long): F[Unit] =
        run(queries.delete(id)).transact(xa).void

      def deleteAll: F[Unit] =
        run(queries.deleteAll).transact(xa).void

      def update(id: Long, updateTodo: UpdateTodo): F[Option[Todo]] =
        run(queries.update(id, updateTodo))
          .map(_ == 1) // if a row has been updated
          .ifM(
            ifTrue = run(queries.get(id)).map(_.headOption),
            ifFalse = none[Todo].pure[ConnectionIO],
          )
          .transact(xa)
    }

  object queries {
    import ctx._

    implicit val todoInsertMeta: InsertMeta[Todo] = insertMeta[Todo](_.id)

    val all = quote {
      query[Todo]
    }

    def get(id: Long) = quote {
      query[Todo].filter(_.id == lift(id))
    }

    def create(createTodo: CreateTodo) = quote {
      val ct = lift(createTodo)
      query[Todo]
        .insert(
          _.title -> ct.title,
          _.order -> ct.order.getOrElse(0),
        )
        .returning(todo => todo)
    }

    def update(id: Long, updateTodo: UpdateTodo) = quote {
      val ut = lift(updateTodo)
      get(id)
        .update(
          todo => todo.title     -> ut.title.getOrElse(todo.title),
          todo => todo.completed -> ut.completed.getOrElse(todo.completed),
          todo => todo.order     -> ut.order.getOrElse(todo.order),
        )
    }

    def delete(id: Long) = quote {
      get(id).delete
    }

    val deleteAll = quote {
      query[Todo].delete
    }
  }
}
