package com.example.service

import scala.concurrent.Future
import com.example.model.TransferRequest

trait TransferService {
  def transfer(request: TransferRequest): Future[Unit]
}
