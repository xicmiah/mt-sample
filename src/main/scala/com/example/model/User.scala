package com.example.model

import java.util.UUID

case class User(id: UUID)

case class AccountInfo(id: UUID, transactions: Seq[Transaction])

sealed trait Transaction {
  def balance: Money
}

sealed trait Source
case class Account(id: UUID) extends Source
case object Initial extends Source

case class Money(amount: BigDecimal, currency: String)