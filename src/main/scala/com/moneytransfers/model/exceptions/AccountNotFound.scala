package com.moneytransfers.model.exceptions

import com.moneytransfers.model.AccountId

class AccountNotFound(id: AccountId) extends RuntimeException(s"Account not found: $id")
