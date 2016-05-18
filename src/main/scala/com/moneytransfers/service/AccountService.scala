package com.moneytransfers.service

import scala.concurrent.Future
import com.moneytransfers.model._

trait AccountService {
  def queryAccount(id: AccountId): Future[AccountInfo]

  def createAccount(currency: Currency): Future[AccountInfo]
}
