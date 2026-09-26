---
name: api-check
description: Smoke-test the running DigCurrency REST API with curl and check that frontend types match the backend OpenAPI contract. Use when the user asks to test, check, call or verify the API, or after adding an endpoint.
---

# API check

Requires the backend on http://localhost:8080 (start it with the `dev-server` skill if needed).

## 1. Smoke test

Run these and check status codes and bodies:

```bash
BASE=http://localhost:8080/api/v1
curl -s -w '\n%{http_code}\n' $BASE/currencies                              # 200, array
curl -s -w '\n%{http_code}\n' "$BASE/currencies?type=CRYPTO"                # 200, only CRYPTO
curl -s -w '\n%{http_code}\n' $BASE/currencies/XXX                          # 404 problem+json
curl -s -w '\n%{http_code}\n' "$BASE/conversions?from=BTC&to=EUR&amount=1"  # 200
curl -s -w '\n%{http_code}\n' -X POST $BASE/currencies \
  -H 'Content-Type: application/json' \
  -d '{"code":"sol","name":"","type":"CRYPTO","usdRate":-1}'                # 400 validation
curl -s -w '\n%{http_code}\n' -X POST $BASE/currencies \
  -H 'Content-Type: application/json' \
  -d '{"code":"SOL","name":"Solana","type":"CRYPTO","usdRate":150}'         # 201
curl -s -w '\n%{http_code}\n' -X DELETE $BASE/currencies/SOL                # 204
```

Every error response must be `application/problem+json` with `title`, `status` and `detail`.

## 2. Contract check

1. Fetch the spec: `curl -s http://localhost:8080/v3/api-docs`.
2. For each schema in `components.schemas`, compare field names and types against `frontend/src/api/types.ts`.
3. For each path, confirm there is a matching function in `frontend/src/api/client.ts`.

## 3. Report

A table of endpoint → expected status → actual status → pass/fail, followed by any contract mismatches (missing field, wrong type, endpoint without a client function).
