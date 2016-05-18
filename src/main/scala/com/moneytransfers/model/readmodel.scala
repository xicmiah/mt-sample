package com.moneytransfers.model

import java.time.Instant

object AccountInfo {
  implicit val pkl = upickle.default.macroRW[AccountInfo]
}
case class AccountInfo(id: AccountId, currency: String, balance: Amount, operations: Seq[Operation])

object Operation {
  implicit val pkl = upickle.default.macroRW[Operation]
}
case class Operation(transactionId: TransactionId, counterparty: AccountId, amount: Amount, timestamp: Instant)
