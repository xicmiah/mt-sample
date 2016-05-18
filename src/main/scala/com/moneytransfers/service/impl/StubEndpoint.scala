package com.moneytransfers.service.impl

import scala.concurrent.ExecutionContext
import akka.http.scaladsl.server.Directives._
import com.moneytransfers.model._
import de.heikoseeberger.akkahttpupickle.UpickleSupport

class StubEndpoint(service: StubService)(implicit ec: ExecutionContext) extends UpickleSupport {
  val route = {
    (path("accounts") & post) { handleWith(service.createAccount) } ~
    (path("transactions") & post) { handleWith(service.addTransaction) }
  }
}
