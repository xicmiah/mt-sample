package com.moneytransfers.model

case class AccountInfo(id: AccountId, transfers: Seq[Transfer])

case class Transfer(from: Account, to: Account, amount: Money)

case class Account(id: AccountId)

case class Money(amount: Double, currency: String)