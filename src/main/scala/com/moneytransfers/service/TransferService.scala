package com.moneytransfers.service

import scala.concurrent.Future
import com.moneytransfers.model.{TransferRequest, TransferResponse}

trait TransferService {
  def transfer(request: TransferRequest): Future[TransferResponse]
}
