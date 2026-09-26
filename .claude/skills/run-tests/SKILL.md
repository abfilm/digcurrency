---
name: run-tests
description: Run the backend (Maven/JUnit) and/or frontend (Vitest) test suites and report results. Use when the user asks to run tests, check the build, or verify a change.
---

# Run tests

Decide the scope from the request or the changed files (`git status`):
- Only `backend/` changed → backend only.
- Only `frontend/` changed → frontend only.
- Both, or unclear → both.

## Backend

```bash
cd backend && mvn -q test
```

- Single class: `mvn -q test -Dtest=CurrencyControllerTest`
- Single method: `mvn -q test -Dtest=CurrencyControllerTest#createReturns201WithLocation`
- Failure details are in `backend/target/surefire-reports/*.txt`.

## Frontend

```bash
cd frontend && npm run typecheck && npm test
```

- If `node_modules/` is missing, run `npm install` first.
- Single file: `npx vitest run src/api/client.test.ts`

## Reporting

- Report pass/fail counts per suite.
- For each failure: test name, the assertion message, and your diagnosis (test bug vs. production bug).
- Do not change assertions just to make a test pass. If behaviour changed on purpose, say so and ask before updating the expectation.
- If a toolchain is missing (`java`, `mvn`, `node`), say which one and stop. Don't claim the tests pass.
