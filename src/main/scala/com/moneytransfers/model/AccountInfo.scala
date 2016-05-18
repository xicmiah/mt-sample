package com.moneytransfers.model

import java.time.Instant

case class AccountInfo(id: AccountId, currency: String, balance: Amount, operations: Seq[Operation])

case class Operation(transactionId: TransactionId, counterparty: AccountId, amount: Amount, timestamp: Instant)
