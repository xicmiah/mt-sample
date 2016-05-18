# Transfer API example

## Overview

This is a small attempt to implement api for money transfers.

## Running

Standalone jar is available on GitHub releases.

To build and run from source, install [sbt](http://www.scala-sbt.org) and execute
```
sbt run
```

When run, this sample starts http server bound to `localhost:8080`

## API
Current endpoints:

* `GET /accounts/:id` returns account information, including current balance
```
GET /accounts/B HTTP/1.1
Accept: */*
Accept-Encoding: gzip, deflate
Connection: keep-alive
Host: localhost:8080
User-Agent: HTTPie/0.9.3


HTTP/1.1 200 OK
Content-Length: 182
Content-Type: application/json
Date: Wed, 18 May 2016 21:07:10 GMT
Server: akka-http/2.4.4

{
    "balance": 10,
    "currency": "USD",
    "id": "B",
    "operations": [
        {
            "amount": 10,
            "counterparty": "A",
            "timestamp": "2016-05-18T20:47:20.879Z",
            "transactionId": "567dae84-81b8-41b8-b6f8-13cb1bfc09ce"
        }
    ]
}
```

* `POST /accounts/:id/transfers` initiates a money transfer, _returns_ on completion.
Requires destination account, sum and currency. Should return 400 on meaningful validation errors.
```
POST /accounts/A/transfers HTTP/1.1
Accept: application/json
Accept-Encoding: gzip, deflate
Connection: keep-alive
Content-Length: 46
Content-Type: application/json
Host: localhost:8080
User-Agent: HTTPie/0.9.3

{
    "amount": "10",
    "currency": "USD",
    "to": "B"
}

HTTP/1.1 200 OK
Content-Length: 56
Content-Type: application/json
Date: Wed, 18 May 2016 21:08:42 GMT
Server: akka-http/2.4.4

{
    "transactionId": "0d4e1681-7e99-4934-92c7-e4cb490d3546"
}
```

Unofficial endpoints, useful for manual testing:
* `POST /accounts` creates a new account
```
POST /accounts HTTP/1.1
Accept: application/json
Accept-Encoding: gzip, deflate
Connection: keep-alive
Content-Length: 30
Content-Type: application/json
Host: localhost:8080
User-Agent: HTTPie/0.9.3

{
    "currency": "USD",
    "id": "C"
}

HTTP/1.1 200 OK
Content-Length: 41
Content-Type: application/json
Date: Wed, 18 May 2016 21:14:45 GMT
Server: akka-http/2.4.4

"Created account Account(C,USD,Vector())"
```
* `POST /transactions` creates a new transaction between two accounts
omitting some validations (e.g. remaining funds).
This allows to actually insert money into system by creating an _`External`_ account
and transferring initial sums from there, running it into negatives.
Summary of all balance of all accounts should remain at zero.
```
POST /transactions HTTP/1.1
Accept: application/json
Accept-Encoding: gzip, deflate
Connection: keep-alive
Content-Length: 48
Content-Type: application/json
Host: localhost:8080
User-Agent: HTTPie/0.9.3

{
    "amount": "100",
    "from": "External",
    "to": "C"
}

HTTP/1.1 200 OK
Content-Length: 111
Content-Type: application/json
Date: Wed, 18 May 2016 21:23:08 GMT
Server: akka-http/2.4.4

"Applied transaction Transaction(a40324f5-d87e-4b17-bec9-a452a070d8c4,External,C,100,2016-05-18T21:23:08.132Z)"
```

## Tests

There are two test suites for now.

`com.moneytransfers.http.EndpointTest` checks http endpoint on mocked services,
i.e  that it correctly parses requests and routes them to appropriate service methods.

`com.moneytransfers.IntegrationTest` actually runs an integration-like test,
everything short of actually starting an http server.

## Sample session

Starting:
```
> java -jar transfers-http-api-assembly-0.1.0.jar
Http server online on localhost:8080
```

Create an account (unofficial api):
```
> http -v post :8080/accounts id=A currency=USD
POST /accounts HTTP/1.1
Accept: application/json
Accept-Encoding: gzip, deflate
Connection: keep-alive
Content-Length: 30
Content-Type: application/json
Host: localhost:8080
User-Agent: HTTPie/0.9.3

{
    "currency": "USD",
    "id": "A"
}

HTTP/1.1 200 OK
Content-Length: 41
Content-Type: application/json
Date: Wed, 18 May 2016 21:38:03 GMT
Server: akka-http/2.4.4

"Created account Account(A,USD,Vector())"
```

Create second account, add initial funds:
```
> http -pb post :8080/accounts id=B currency=USD
"Created account Account(B,USD,Vector())"
> http -pb post :8080/accounts id=External currency=USD
"Created account Account(External,USD,Vector())"
> http -v post :8080/transactions from=External to=A amount=100
  POST /transactions HTTP/1.1
  Accept: application/json
  Accept-Encoding: gzip, deflate
  Connection: keep-alive
  Content-Length: 48
  Content-Type: application/json
  Host: localhost:8080
  User-Agent: HTTPie/0.9.3

  {
      "amount": "100",
      "from": "External",
      "to": "A"
  }

  HTTP/1.1 200 OK
  Content-Length: 111
  Content-Type: application/json
  Date: Wed, 18 May 2016 21:42:22 GMT
  Server: akka-http/2.4.4

  "Applied transaction Transaction(297a55b7-5453-44cd-9ab3-8ccb2d165adf,External,A,100,2016-05-18T21:42:22.772Z)"
```

Transfer $10 from _A_ to _B_:
```
> http -v post :8080/accounts/A/transfers to=B amount=10 currency=USD
POST /accounts/A/transfers HTTP/1.1
Accept: application/json
Accept-Encoding: gzip, deflate
Connection: keep-alive
Content-Length: 46
Content-Type: application/json
Host: localhost:8080
User-Agent: HTTPie/0.9.3

{
    "amount": "10",
    "currency": "USD",
    "to": "B"
}

HTTP/1.1 200 OK
Content-Length: 56
Content-Type: application/json
Date: Wed, 18 May 2016 21:43:41 GMT
Server: akka-http/2.4.4

{
    "transactionId": "ff81a517-3e85-43ca-b528-689cfced3839"
}
```

Query account _B_'s state, previous transaction should be visible:
```
http -v get :8080/accounts/B
GET /accounts/B HTTP/1.1
Accept: */*
Accept-Encoding: gzip, deflate
Connection: keep-alive
Host: localhost:8080
User-Agent: HTTPie/0.9.3



HTTP/1.1 200 OK
Content-Length: 182
Content-Type: application/json
Date: Wed, 18 May 2016 21:44:26 GMT
Server: akka-http/2.4.4

{
    "balance": 10,
    "currency": "USD",
    "id": "B",
    "operations": [
        {
            "amount": 10,
            "counterparty": "A",
            "timestamp": "2016-05-18T21:43:41.112Z",
            "transactionId": "ff81a517-3e85-43ca-b528-689cfced3839"
        }
    ]
}
```

Try to transfer more funds than on account:
```
POST /accounts/B/transfers HTTP/1.1
Accept: application/json
Accept-Encoding: gzip, deflate
Connection: keep-alive
Content-Length: 49
Content-Type: application/json
Host: localhost:8080
User-Agent: HTTPie/0.9.3

{
    "amount": "10000",
    "currency": "USD",
    "to": "A"
}

HTTP/1.1 400 Bad Request
Content-Length: 38
Content-Type: application/json
Date: Wed, 18 May 2016 21:47:24 GMT
Server: akka-http/2.4.4

"requirement failed: Not enough funds"
```

