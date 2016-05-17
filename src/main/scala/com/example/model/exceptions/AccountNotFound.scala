package com.example.model.exceptions

import com.example.model.AccountId

class AccountNotFound(id: AccountId) extends RuntimeException(s"Account not found: $id")
