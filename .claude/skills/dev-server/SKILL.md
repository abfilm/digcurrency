---
name: dev-server
description: Start the DigCurrency backend and frontend dev servers locally and confirm they are up. Use when the user asks to run, start, launch or preview the app.
---

# Start the dev environment

1. Check prerequisites: `java -version` (needs 21+), `mvn -v`, `node -v` (needs 20.19+ or 22.12+). If one is missing, tell the user and stop.
2. Start the backend in the background:
   ```bash
   cd backend && mvn spring-boot:run
   ```
   Wait until `curl -sf http://localhost:8080/actuator/health` returns `{"status":"UP"}`.
3. Install frontend deps if `frontend/node_modules` is missing (`npm install`), then start Vite in the background:
   ```bash
   cd frontend && npm run dev
   ```
   Wait until `curl -sf http://localhost:5173` responds.
4. Verify the proxy works: `curl -sf http://localhost:5173/api/v1/currencies` should return the seeded currencies.
5. Report the URLs:
   - App: http://localhost:5173
   - Swagger UI: http://localhost:8080/swagger-ui.html
   - H2 console: http://localhost:8080/h2-console (JDBC URL `jdbc:h2:mem:digcurrency`, user `sa`)

If port 8080 or 5173 is already in use, report which process holds it (`lsof -i :8080`) instead of killing it.
