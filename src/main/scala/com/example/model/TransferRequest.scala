package com.example.model


case class TransferRequest(
  from: AccountId,
  to: AccountId,
  sum: Money
)


