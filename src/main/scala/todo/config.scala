package todo
import db.DatabaseConfig

object config {
  val baseUrl = "http://localhost:9000"

  val db = DatabaseConfig(
    driver = "org.postgresql.Driver",
    url = "jdbc:postgresql:postgres",
    user = "postgres",
    pass = "postgres",
  )
}
