package com.moneytransfers.http

import java.util.UUID
import scala.concurrent.Future
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.testkit.ScalatestRouteTest
import com.moneytransfers.model.{AccountInfo, Money}
import com.moneytransfers.service.TransferService.{TransferRequest, TransferResponse}
import com.moneytransfers.service.{AccountService, TransferService}
import de.heikoseeberger.akkahttpupickle.UpickleSupport
import org.scalamock.scalatest.MockFactory
import org.scalatest.{OneInstancePerTest, WordSpec}
import upickle.Js

class EndpointTest extends WordSpec with OneInstancePerTest with ScalatestRouteTest with UpickleSupport with MockFactory {
  import scala.concurrent.Future.successful

  val mockAccounts = mock[AccountService]
  val mockTransfers = mock[TransferService]
  val route = new Endpoint(mockAccounts, mockTransfers).route
  val sealedRoute = Route.seal(route)

  object fixtures {
    val goodAccount = AccountInfo("A", "USD", 0, Seq())
    val transactionId = UUID.fromString("64bc2fba-1d2c-11e6-9812-14109fe5d08f")
  }

  "/users/:id" should {
    "return 404 on unknown ids" in {
      (mockAccounts.queryAccount _).expects("bogus").returning(successful(None))

      Get("/accounts/bogus") ~> sealedRoute ~> check {
        assert(status === StatusCodes.NotFound)
      }
    }

    "return 200 for known accounts" in {
      (mockAccounts.queryAccount _).expects(*).returning(successful(Some(fixtures.goodAccount)))

      Get("/accounts/A") ~> route ~> check {
        assert(status === StatusCodes.OK)
      }
    }

    "return json with id and balance for known accounts" in {
      (mockAccounts.queryAccount _).expects("A").returning(successful(Some(fixtures.goodAccount)))

      Get("/accounts/A") ~> route ~> check {
        val json = responseAs[upickle.Js.Value].obj

        assert(json.keySet contains "id")
        assert(json.keySet contains "balance")
      }
    }

    "return 405 on non-get methods" in {
      Post("/accounts/A") ~> sealedRoute ~> check {
        assert(status === StatusCodes.MethodNotAllowed)
      }
    }
  }

  "POST /accounts/:id/transfers" should {
    val goodPayload = Js.Obj(
      "to" -> Js.Str("B"),
      "amount" -> Js.Num(10),
      "currency" -> Js.Str("USD"))

    "accept valid payloads" in {
      (mockTransfers.transfer _)
        .expects(TransferRequest("A", "B", Money(10, "USD")))
        .returning(successful(TransferResponse(fixtures.transactionId)))

      Post("/accounts/A/transfers", goodPayload) ~> route ~> check {
        assert(status.isSuccess())
      }
    }

    "return transaction id in successful response" in {
      (mockTransfers.transfer _).expects(*)
        .returning(successful(TransferResponse(fixtures.transactionId)))

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

    "return 400 on validation errors" in {
      val message = "Insufficient funds"
      (mockTransfers.transfer _).expects(*).returning(Future.failed(new IllegalArgumentException(message)))

      Post("/accounts/A/transfers", goodPayload) ~> sealedRoute ~> check {
        assert(status === StatusCodes.BadRequest)
        assert(responseAs[String].contains(message))
      }
    }
  }
}
