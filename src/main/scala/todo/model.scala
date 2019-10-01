package todo
import io.circe._
import io.circe.derivation._
import io.circe.syntax._

case class Todo(
    id: Long,
    title: String,
    completed: Boolean,
    order: Int,
)
object Todo {
  implicit val encoder: Encoder[Todo] = {
    Encoder.instance { todo =>
      deriveEncoder[Todo]
        .encodeObject(todo)
        .add("url", Json.fromString(routes.todoUrl(todo.id)))
        .asJson
    }
  }
  implicit val decoder: Decoder[Todo] = deriveDecoder
}

case class CreateTodo(title: String, order: Option[Int])
object CreateTodo {
  implicit val codec: Codec[CreateTodo] = deriveCodec
}

case class UpdateTodo(
    title: Option[String],
    completed: Option[Boolean],
    order: Option[Int],
)
object UpdateTodo {
  implicit val codec: Codec[UpdateTodo] = deriveCodec
}
