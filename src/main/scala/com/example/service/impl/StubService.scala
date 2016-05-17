package com.example.service.impl

import scala.concurrent.{ExecutionContext, Future}
import com.example.model.exceptions.AccountNotFound
import com.example.model.{AccountId, AccountInfo, TransferRequest}
import com.example.service.{AccountService, TransferService}

class StubService(implicit ec: ExecutionContext) extends AccountService with TransferService {
  private val accounts: Map[AccountId, AccountInfo] = Seq(
    AccountInfo("A", Seq()), AccountInfo("B", Seq())
  ).map(full => full.id -> full)(collection.breakOut)


  override def queryAccount(id: AccountId) = Future(accounts.getOrElse(id, throw new AccountNotFound(id)))

  override def transfer(request: TransferRequest) = Future { ??? }
}
