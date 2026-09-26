---
name: add-endpoint
description: Add a new REST endpoint or resource end to end (Spring Boot controller/service/DTO/tests plus the React API client and types). Use when the user asks to add, expose or extend an API endpoint or resource.
---

# Add a REST endpoint end to end

Follow these steps in order. Read `CLAUDE.md` conventions first if you haven't this session.

## 1. Design the contract

Before writing code, state:
- Method + path under `/api/v1/` (plural nouns, kebab-case, no verbs except for actions like `/conversions`).
- Request body record (fields + validation) and response record.
- Status codes: 200 / 201 + `Location` / 204 on success; 400, 404, 409 on failure.

If the endpoint belongs to an existing feature, reuse its package. Otherwise create `backend/src/main/java/com/digcurrency/<feature>/`.

## 2. Backend

1. **DTOs** as Java `record`s. Request records carry Bean Validation annotations (`@NotBlank`, `@NotNull`, `@DecimalMin`, `@Pattern`...). Money fields are `BigDecimal`.
2. **Entity + repository** only if new persistence is needed (`JpaRepository`, derived query methods preferred over `@Query`).
3. **Service**: business logic, `@Transactional` on writes. Throw `NotFoundException` / `ConflictException` / `IllegalArgumentException` for error cases.
4. **Controller**: thin, `@Valid @RequestBody`, `@Operation(summary = ...)` and a `@Tag` for OpenAPI.

## 3. Backend tests

- `@WebMvcTest(<Controller>.class)` with `@MockitoBean` service: one success case and each error status.
- Service unit test with Mockito for non-trivial logic (calculations, rules).
- Extend `CurrencyApiIntegrationTest` or add a new `@SpringBootTest` + `@ActiveProfiles("test")` test if the flow crosses features.

Run `cd backend && mvn test` and fix failures before moving on.

## 4. Frontend

1. Add/extend the TypeScript interfaces in `frontend/src/api/types.ts` so they match the Java records field-for-field.
2. Add a function to the relevant object in `frontend/src/api/client.ts` using the shared `request<T>()` helper.
3. Add a client test in `client.test.ts` asserting the URL/method it calls.
4. Only then wire it into components.

Run `cd frontend && npm run typecheck && npm test`.

## 5. Finish

Summarize the new contract (method, path, request, response, error codes) for the user. Suggest running the `api-check` skill against a live backend.
