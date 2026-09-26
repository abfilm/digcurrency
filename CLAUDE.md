# DigCurrency showcase

Monorepo with a Spring Boot REST API (`backend/`) and a React SPA (`frontend/`).
Domain: currencies (FIAT, CRYPTO, CBDC) with a USD rate, plus conversion between them.

## Commands

| Task | Command (run from repo root) |
|---|---|
| Backend: run | `cd backend && mvn spring-boot:run` → http://localhost:8080 |
| Backend: test | `cd backend && mvn test` |
| Frontend: install | `cd frontend && npm install` |
| Frontend: run | `cd frontend && npm run dev` → http://localhost:5173 (proxies `/api` to :8080) |
| Frontend: test | `cd frontend && npm test` |
| Frontend: typecheck | `cd frontend && npm run typecheck` |

API docs while the backend runs: http://localhost:8080/swagger-ui.html (OpenAPI JSON at `/v3/api-docs`).

## Backend conventions (Java 21, Spring Boot 3.5)

- Package by feature: `com.digcurrency.<feature>` holds its entity, repository, service, controller and DTOs.
- Controllers are thin; business rules live in services. Services are `@Transactional(readOnly = true)` at class level, with `@Transactional` on writes.
- Never expose JPA entities from controllers. Responses are `record` DTOs; request bodies are separate `record`s with Bean Validation annotations and `@Valid`.
- All endpoints live under `/api/v1/...`. Use plural nouns, `201 + Location` for creates, `204` for deletes.
- Errors are RFC 9457 `ProblemDetail`. Throw `NotFoundException` (404), `ConflictException` (409) or `IllegalArgumentException` (400); `common/GlobalExceptionHandler` maps them. Don't build error bodies in controllers.
- Money is `BigDecimal`, never `double`. Use explicit scale and `RoundingMode.HALF_EVEN`.
- Constructor injection only, no field `@Autowired` in production code.
- Demo data is loaded by `CurrencySeeder`, which is disabled in the `test` profile.

## Frontend conventions (React 19, TypeScript, Vite)

- All HTTP goes through `src/api/client.ts`; components never call `fetch` directly.
- `src/api/types.ts` mirrors the backend DTOs. When a DTO changes, update this file in the same change.
- Failed requests throw `ApiError` carrying the backend's `ProblemDetail`.
- Function components and hooks only. Styles in `src/index.css` using the CSS variables in `:root`.

## Testing

- Backend: `@WebMvcTest` + `@MockitoBean` for controllers, plain Mockito for services, `@SpringBootTest` + `@ActiveProfiles("test")` for end-to-end API flows.
- Frontend: Vitest + Testing Library; mock `fetch` with `vi.spyOn(globalThis, 'fetch')`.
- Every new endpoint needs a controller test covering success and at least one error status.

## Claude Code setup

- Skills (`.claude/skills/`): `add-endpoint`, `run-tests`, `dev-server`, `api-check`.
- Agents (`.claude/agents/`): `backend-developer`, `frontend-developer`, `test-engineer`, `api-reviewer`.
