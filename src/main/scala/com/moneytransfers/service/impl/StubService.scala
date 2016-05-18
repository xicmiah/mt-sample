package com.moneytransfers.service.impl

import java.time.Instant
import java.util.UUID
import scala.concurrent.ExecutionContext
import akka.agent.Agent
import com.moneytransfers.model._
import com.moneytransfers.service.TransferService.{TransferRequest, TransferResponse}
import com.moneytransfers.service.{AccountService, TransferService}

object StubService {
  case class Account(id: AccountId, currency: Currency, operations: Seq[Operation] = Seq()) {
    def balance = operations.map(_.amount).sum

    def toReadModel = AccountInfo(id, currency, balance, operations)

    def addOperation(operation: Operation) = copy(operations = operation +: operations)
  }

  case class Transaction(id: TransactionId = UUID.randomUUID(), from: AccountId, to: AccountId, amount: Amount, timestamp: Instant = Instant.now()) {
    def accountOperations: Seq[(AccountId, Operation)] = Seq(
      from -> Operation(id, to, amount, timestamp),
      to -> Operation(id, from, -amount, timestamp))
  }

  private type State = Map[AccountId, Account]
  private val initialState: State = Map.empty

  private def validateRequest(currentState: State, request: TransferRequest, transactionId: TransactionId): Transaction = {
    val TransferRequest(from, to, Money(amount, currency)) = request
    val fromAccount = currentState.getOrElse(from, throw new IllegalArgumentException(s"Account not found: $from"))
    val toAccount = currentState.getOrElse(from, throw new IllegalArgumentException(s"Account not found: $to"))

    require(toAccount.currency == fromAccount.currency, "Accounts must have matching currencies")
    require(fromAccount.currency == currency, "Transfer currency must match account currency")
    require(fromAccount.balance >= amount, "Not enough funds")

    Transaction(transactionId, from, to, amount)
  }

  private def applyTransaction(currentState: State, transaction: Transaction): State = {
    require(currentState.contains(transaction.from) && currentState.contains(transaction.to))

    transaction.accountOperations.foldLeft(currentState) {
      case (state, (accountId, operation)) => state.updated(accountId, state(accountId).addOperation(operation))
    }
  }
}
class StubService(implicit ec: ExecutionContext) extends AccountService with TransferService {
  import StubService._

  private val state = Agent[State](initialState)

  override def queryAccount(id: AccountId) = state.future().map(_.get(id).map(_.toReadModel))
  override def transfer(request: TransferRequest) = {
    val transactionId: TransactionId = UUID.randomUUID()

    state.alter { currentState =>
      val transaction = validateRequest(currentState, request, transactionId)
      applyTransaction(currentState, transaction)
    }.map(_ => TransferResponse(transactionId))
  }

  def createAccount(account: Account) = state.alter(_.updated(account.id, account)).map(_ => s"Created account $account")
  def addTransaction(transaction: Transaction) = state.alter(applyTransaction(_, transaction)).map(_ => s"Applied transaction $transaction")
}
