# Agents

## Structure
Monorepo with three top-level entries:
- `openapi.yaml` — single source of truth for the API contract
- `backend/` — Spring Boot 4 / Java 25 / PostgreSQL
- `frontend/` — React 19 / TypeScript 6 / Vite

## Key commands

### Backend
```bash
# from backend/
./gradlew openApiGenerate   # generate Spring interfaces from openapi.yaml
./gradlew spotlessApply     # format with Google Java Format
./gradlew bootRun           # run (requires env vars below)
```

### Frontend
```bash
# from frontend/
npm run generate   # generate TypeScript types from openapi.yaml → src/api/schema.d.ts
npm run dev        # dev server at http://localhost:5173
```

## Environment variables
Required at runtime — no defaults, app refuses to start if missing. See `.env.example` for the full list.

Run `docker compose up -d` for the local DB.

## Architecture notes
- **OpenAPI-first**: never edit generated code under `build/generated/`. Re-run `openApiGenerate` after changing `openapi.yaml`.
- **Errors**: RFC 9457 — all error responses use `Content-Type: application/problem+json`.
- **Auth**: stateless JWT Bearer. `SecurityConfig` permits `/auth/**`, requires auth on everything else.
- **Naming**: use full variable names (`request`, `response`, `user`) not abbreviations (`req`, `res`, `usr`). Abbreviated names are harder to read for non-native English speakers and hurt code clarity.
