package com.moneytransfers.http

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.testkit.ScalatestRouteTest
import com.moneytransfers.service.impl.StubService
import de.heikoseeberger.akkahttpupickle.UpickleSupport
import org.scalatest.WordSpec
import upickle.Js

class EndpointTest extends WordSpec with ScalatestRouteTest with UpickleSupport {
  val stub = new StubService()
  val route = new Endpoint(stub, stub).route
  val sealedRoute = Route.seal(route)

  "GET /users/:id" should {
    "return 404 on unknown ids" in {
      Get("/accounts/bogus") ~> sealedRoute ~> check {
        assert(status === StatusCodes.NotFound)
      }
    }

    "return 200 for known accounts" in {
      Get("/accounts/A") ~> route ~> check {
        assert(status === StatusCodes.OK)
      }
    }

    "return json with id and balance for known accounts" in {
      Get("/accounts/A") ~> route ~> check {
        val json = responseAs[upickle.Js.Value].obj

        assert(json.keySet contains "id")
        assert(json.keySet contains "balance")
      }
    }
  }

  "POST /accounts/:id/transfers" should {
    val goodPayload = Js.Obj(
      "to" -> Js.Str("B"),
      "amount" -> Js.Num(10),
      "currency" -> Js.Str("USD"))

    "accept valid payloads" in {
      Post("/accounts/A/transfers", goodPayload) ~> route ~> check {
        assert(status.isSuccess())
      }
    }

    "return transaction id in successful response" in {
      Post("/accounts/A/transfers", goodPayload) ~> route ~> check {
        val json = responseAs[upickle.Js.Value].obj

        assert(json.keySet contains "transactionId")
      }
    }

    "return 400 on malformed requests" in {
      Post("/accounts/A/transfers", Js.Obj()) ~> sealedRoute ~> check {
        assert(status === StatusCodes.BadRequest)
      }
    }
  }
}
