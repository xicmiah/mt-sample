package com.moneytransfers.model


case class TransferRequest(
  from: AccountId,
  to: AccountId,
  sum: Money
)


