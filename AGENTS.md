# Agents

## Structure
Monorepo with three top-level entries:
- `openapi.yaml` — single source of truth for the API contract
- `backend/` — Spring Boot 4 / Java 25 / PostgreSQL
- `frontend/` — React 19 / TypeScript 6 / Vite

## Key commands

Run build and generate commands only from the owning component directory. Do not invoke backend or frontend build tools from the repository root.

### Backend
```bash
# from backend/
./gradlew openApiGenerate # generate Spring interfaces from openapi.yaml
./gradlew build -x test # build
```

### Frontend
```bash
# from frontend/
npm run generate # generate TypeScript types from openapi.yaml → src/api/schema.d.ts
npm run build # build
```

## Environment variables
Required at runtime — no defaults, app refuses to start if missing. See `.env.example` for the full list.

## Architecture notes
- **OpenAPI-first**: never edit generated code under `build/generated/`. Re-run `openApiGenerate` after changing `openapi.yaml`.
- **Errors**: RFC 9457 — all error responses use `Content-Type: application/problem+json`.
- **Auth**: stateless JWT Bearer. `SecurityConfig` permits `/auth/**`, requires auth on everything else.
- **Naming**: use full variable names (`request`, `response`, `user`) not abbreviations (`req`, `res`, `usr`). Abbreviated names are harder to read for non-native English speakers and hurt code clarity.

## Kubernetes conventions

### File names
- **Use kebab-case for resource kind names** when they appear in file names — e.g. `pod-disruption-budget.yaml`, `cluster-issuer.yaml`, `sealed-backend-secret.yaml`.
- **Inside a component directory** (`backend/`, `frontend/`, `postgres/`): name the file after the kind alone — `deployment.yaml`, `service.yaml`, `configmap.yaml`, `statefulset.yaml`. The directory provides component scope.
- **In a shared directory** (`ingress/`, `patches/`, `secrets/`): use `<component>-<kind>.yaml` — e.g. `backend-ingress.yaml`, `backend-configmap.yaml`, `backend-pod-disruption-budget.yaml`, `postgres-secret.yaml`. This groups files by component when sorted alphabetically.
- **SealedSecrets** add a `sealed-` prefix — `sealed-postgres-secret.yaml`, `sealed-backend-secret.yaml`.
- **Singleton resources** with no ambiguity are named after the kind alone — `namespace.yaml`, `middleware.yaml`, `cluster-issuer.yaml`, `application.yaml`.
