package com.moneytransfers

import java.time.Instant
import java.util.UUID
import upickle.Js
import upickle.default._

package object model {
  type AccountId = String
  type TransactionId = UUID
  type Currency = String
  type Amount = BigDecimal


  implicit val InstantRW = ReadWriter[Instant](i => Js.Str(i.toString), {
    case Js.Str(value) => Instant.parse(value)
  })

  implicit val AmountRW = ReadWriter[BigDecimal](bd => Js.Num(bd.doubleValue()), {
    case Js.Str(value) => BigDecimal(value)
    case Js.Num(value) => BigDecimal(value)
  })
}
