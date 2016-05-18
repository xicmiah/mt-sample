package com.moneytransfers.service

import scala.concurrent.Future
import com.moneytransfers.model._
import com.moneytransfers.service.TransferService.{TransferRequest, TransferResponse}

object TransferService {
  case class TransferRequest(
    from: AccountId,
    to: AccountId,
    sum: Money)

  case class TransferResponse(transactionId: TransactionId)
}

trait TransferService {
  def transfer(request: TransferRequest): Future[TransferResponse]
}
