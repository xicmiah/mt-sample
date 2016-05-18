package com.moneytransfers.model

object UserTransferRequest {
  implicit val pkl = upickle.default.macroRW[UserTransferRequest]
}
case class UserTransferRequest(to: AccountId, amount: Amount, currency: Currency) {
  def mkTransferRequest(from: AccountId) = TransferRequest(from, to, Money(amount, currency))
}
