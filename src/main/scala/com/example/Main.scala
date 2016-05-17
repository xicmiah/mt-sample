package com.example

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.stream.ActorMaterializer
import com.example.http.Endpoint
import com.example.service.impl.StubService

object Main extends App {
  implicit val system = ActorSystem("revolut-sample")
  implicit val mat = ActorMaterializer()
  implicit val ec = system.dispatcher

  val serviceStub = new StubService

  val endpoint = new Endpoint(serviceStub, serviceStub)

  val binding = Http().bindAndHandle(endpoint.route, "localhost", 8080)

  println("Http server online on localhost:8080")

}