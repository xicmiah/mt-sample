package com.moneytransfers.model

case class UserTransferRequest(to: AccountId, amount: Money)
