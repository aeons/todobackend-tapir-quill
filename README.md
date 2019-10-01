# Todo-Backend implementation in Scala

This is an implementation of the [Todo-Backend](https://todobackend.com/) in Scala, using [`tapir`](https://tapir-scala.readthedocs.io/en/latest/), [`http4s`](https://http4s.org) and [`doobie`](https://github.com/tpolecat/doobie) with [`quill`](https://getquill.io/) queries.

## Running

Run `docker-compose up` to start a postgres server with port 5432 exposed, then in `sbt` run `reStart` to start the server using [`sbt-revolver`](https://github.com/spray/sbt-revolver).


## Testing

Visit the [Todo-Backend reference specs](http://todobackend.com/specs/index.html?http://localhost:9000/) with the server running locally.
