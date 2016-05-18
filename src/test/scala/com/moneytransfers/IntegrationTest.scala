package com.moneytransfers

import akka.http.scaladsl.server.Route
import akka.http.scaladsl.testkit.ScalatestRouteTest
import com.moneytransfers.http.Endpoint
import com.moneytransfers.model.{AccountInfo, UserTransferRequest}
import com.moneytransfers.service.impl.StubService
import com.moneytransfers.service.impl.StubService.{Account, Transaction}
import de.heikoseeberger.akkahttpupickle.UpickleSupport
import org.scalatest.{BeforeAndAfter, FunSuite, OneInstancePerTest}

class IntegrationTest extends FunSuite with OneInstancePerTest with ScalatestRouteTest with BeforeAndAfter with UpickleSupport {
  val service = new StubService()
  val route = Route.seal(new Endpoint(service, service).route)

  before {
    service.createAccount(Account("External", "USD"))
    service.createAccount(Account("A", "USD"))
    service.createAccount(Account("B", "USD"))

    service.addTransaction(Transaction(from = "External", to = "A", amount = 100))
    service.addTransaction(Transaction(from = "External", to = "B", amount = 200))
  }

  test("Perform a transfer and check balances") {
    Post("/accounts/A/transfers", UserTransferRequest("B", 10, "USD")) ~> route ~> check {
      assert(status.isSuccess())
    }

    Get("/accounts/A") ~> route ~> check {
      val account = responseAs[AccountInfo]

      assert(account.balance === 90)

      val lastOperation = account.operations.head
      assert(lastOperation.counterparty === "B")
      assert(lastOperation.amount === -10)
    }

    Get("/accounts/B") ~> route ~> check {
      val account = responseAs[AccountInfo]

      assert(account.balance === 210)
    }
  }

  test("Try to exceed balance, receive error, no changes in balance") {
    Post("/accounts/A/transfers", UserTransferRequest("B", 250, "USD")) ~> route ~> check {
      assert(status.isFailure())
    }

    Get("/accounts/A") ~> route ~> check {
      assert(responseAs[AccountInfo].balance === 100)
    }
  }

  test("Try to transfer to non-existing account, receive error") {
    Post("/accounts/A/transfers", UserTransferRequest("bogus", 10, "USD")) ~> route ~> check {
      assert(status.isFailure())
    }
  }
}
