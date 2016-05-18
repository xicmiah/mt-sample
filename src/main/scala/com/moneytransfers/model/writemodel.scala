package com.moneytransfers.model

import com.moneytransfers.service.TransferService.TransferRequest

object UserTransferRequest {
  implicit val pkl = upickle.default.macroRW[UserTransferRequest]
}
case class UserTransferRequest(to: AccountId, amount: Amount, currency: Currency) {
  def mkTransferRequest(from: AccountId) = TransferRequest(from, to, Money(amount, currency))
}
