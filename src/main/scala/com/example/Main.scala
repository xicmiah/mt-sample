package com.example

import scala.io.StdIn
import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.stream.ActorMaterializer
import com.example.http.Endpoint

object Main extends App {
  implicit val system = ActorSystem("revolut-sample")
  implicit val mat = ActorMaterializer()
  implicit val ec = system.dispatcher

  val endpoint = new Endpoint

  val binding = Http().bindAndHandle(endpoint.route, "localhost", 8080)

  println("Http server online on localhost:8080")
  StdIn.readLine()

  binding.flatMap(_.unbind()).onComplete(_ => system.terminate())

}