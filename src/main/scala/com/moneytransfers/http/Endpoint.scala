package com.moneytransfers.http

import scala.concurrent.ExecutionContext
import akka.http.scaladsl.server.Directives._
import com.moneytransfers.model._
import com.moneytransfers.service.{AccountService, TransferService}
import de.heikoseeberger.akkahttpupickle.UpickleSupport
import upickle.default._

class Endpoint(accountService: AccountService, transferService: TransferService)(implicit ec: ExecutionContext) extends AnyRef with UpickleSupport {

  val route = {
    pathPrefix("accounts" / Segment) { accountId: AccountId =>
      pathEndOrSingleSlash {
        get {
          complete(accountService.queryAccount(accountId))
        }
      } ~
      path("transfers") {
        post {
          entity(as[UserTransferRequest]) { transferRequest =>
            complete(s"Started transfer from $accountId, request: $transferRequest")
          }
        }
      }
    } ~
    (path("accounts") & post) {
      handleWith(accountService.createAccount)
    }
  }
}
