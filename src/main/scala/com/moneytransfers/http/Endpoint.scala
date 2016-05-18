package com.moneytransfers.http

import scala.concurrent.ExecutionContext
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.ExceptionHandler
import com.moneytransfers.model._
import com.moneytransfers.service.{AccountService, TransferService}
import de.heikoseeberger.akkahttpupickle.UpickleSupport
import upickle.default._

class Endpoint(accountService: AccountService, transferService: TransferService)(implicit ec: ExecutionContext) extends AnyRef with UpickleSupport {
  val validationErrorHandler = ExceptionHandler {
    case validationError: IllegalArgumentException => complete(StatusCodes.BadRequest, validationError.getMessage)
  }

  val route = handleExceptions(validationErrorHandler) {
    pathPrefix("accounts" / Segment) { accountId: AccountId =>
      pathEndOrSingleSlash {
        get {
          onSuccess(accountService.queryAccount(accountId)) {
            case Some(result) => complete(result)
            case None => complete(StatusCodes.NotFound, s"Account not found: $accountId")
          }
        }
      } ~
      path("transfers") {
        post {
          entity(as[UserTransferRequest]) { userPartial =>
            complete {
              transferService.transfer(userPartial.mkTransferRequest(accountId))
            }
          }
        }
      }
    }
  }
}
