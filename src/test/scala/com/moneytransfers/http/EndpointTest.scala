package com.moneytransfers.http

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.testkit.ScalatestRouteTest
import com.moneytransfers.service.impl.StubService
import de.heikoseeberger.akkahttpupickle.UpickleSupport
import org.scalatest.WordSpec

class EndpointTest extends WordSpec with ScalatestRouteTest with UpickleSupport {
  val stub = new StubService()
  val route = new Endpoint(stub, stub).route

  "GET /users/:id" should {
    "return 404 on unknown ids" in {
      Get("/accounts/bogus") ~> route ~> check {
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
        val deserialzedJson = responseAs[upickle.Js.Value].obj

        assert(deserialzedJson.keySet contains "id")
        assert(deserialzedJson.keySet contains "balance")
      }
    }
  }
}
