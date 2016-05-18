package com.moneytransfers.service

import scala.concurrent.Future
import com.moneytransfers.model._
import com.moneytransfers.service.TransferService.{TransferRequest, TransferResponse}

object TransferService {
  case class TransferRequest(from: AccountId, to: AccountId, sum: Money) {
    require(sum.amount > 0, "Transfer amount must be positive")
  }

  case class TransferResponse(transactionId: TransactionId)
}

trait TransferService {
  def transfer(request: TransferRequest): Future[TransferResponse]
}
