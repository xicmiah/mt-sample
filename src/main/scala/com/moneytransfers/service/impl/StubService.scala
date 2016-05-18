package com.moneytransfers.service.impl

import scala.concurrent.{ExecutionContext, Future}
import com.moneytransfers.model.{AccountId, AccountInfo, Currency}
import com.moneytransfers.service.TransferService.TransferRequest
import com.moneytransfers.service.{AccountService, TransferService}

class StubService(implicit ec: ExecutionContext) extends AccountService with TransferService {
  private val accounts: Map[AccountId, AccountInfo] = Seq(
    AccountInfo("A", "USD", 100, Seq()),
    AccountInfo("B", "EUR", 50, Seq())
  ).map(full => full.id -> full)(collection.breakOut)


  override def queryAccount(id: AccountId) = Future(accounts.get(id))

  override def createAccount(currency: Currency) = Future(???)

  override def transfer(request: TransferRequest) = Future { ??? }
}
