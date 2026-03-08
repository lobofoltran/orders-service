#/bin/bash

go run load_orders.go \
  -rps 500 \
  -duration 60s \
  -workers 100

