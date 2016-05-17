package com.example.service

import scala.concurrent.Future
import com.example.model._

trait AccountService {
  def queryAccount(id: AccountId): Future[AccountInfo]
}
