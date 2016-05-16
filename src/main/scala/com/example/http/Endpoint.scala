package com.example.http

import akka.http.scaladsl.server.Directive1
import akka.http.scaladsl.server.Directives._

class Endpoint {
  type Transaction = String

  val decodeTransfer: Directive1[Transaction] = entity(as[Transaction])

  val route = {
    pathPrefix("users" / Segment) { userId =>
      (pathEndOrSingleSlash & get) {
        complete(s"User $userId")
      } ~
      path("transfers") {
        post {
          decodeTransfer { transfer =>
            complete(s"Started transfer $transfer")
          }
        }
      }
    }
  }
}
