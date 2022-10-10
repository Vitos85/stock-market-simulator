# stock-market-simulator
Stock market simulator challenge project

## CLI
Example: **add -d BUY -p 1000 -q 25 -s AAPL -u Vitos**
where:
- "-d" - Direction BUY/SELL
- "-p" - Order price
- "-q" - stock quantity
- "-s" - symbol name
- "-u" - user ID

## REST
Example for adding order:
```
curl --location --request POST 'http://localhost:8080/api/v1/orders/add' ^
--header 'Content-Type: application/json' ^
--data-raw '{
    "userID": "Warren Buffett",
    "dir": "BUY",
    "price": "30000",
    "quantity": "50",
    "symbol": "AAPL"
}'
```
Example for canceling order:
```
curl --location --request POST 'http://localhost:8080/api/v1/orders/8d903381-64a2-4433-a681-7c15b4ff0cb4/cancel'
```

## WebSocket
For initiate WebSocket connection do request:
```
ws://localhost:8080/user
```
In request must be header like "userId = Warren Buffett" with name equal with userId in order request
