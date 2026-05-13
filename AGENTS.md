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

## Kubernetes conventions

### Naming
- All resource names are lowercase kebab-case (e.g. `todo-app`, `backend-config`, `strip-api-prefix`).
- Component resources (Deployment, Service, StatefulSet, Ingress) are named after the component: `frontend`, `backend`, `postgres`, `mailhog`.
- Compound resources append a descriptive suffix: `backend-config` (ConfigMap), `backend-secret` / `postgres-secret` / `ghcr-pull-secret` (Secret).
- Middleware names describe the function: `strip-api-prefix`.
- The namespace and ArgoCD Application share the project name: `todo-app`.

### File names
- **Inside a component directory** (`backend/`, `frontend/`, `postgres/`): name the file after the kind alone — `deployment.yaml`, `service.yaml`, `configmap.yaml`, `statefulset.yaml`. The directory provides component scope.
- **In a shared directory** (`ingress/`, `patches/`, `secrets/`): use `<component>-<kind>.yaml` — e.g. `backend-ingress.yaml`, `backend-configmap.yaml`, `postgres-secret.yaml`. This groups files by component when sorted alphabetically.
- **SealedSecrets** add a `sealed-` prefix — `sealed-postgres-secret.yaml`, `sealed-backend-secret.yaml`.
- **Singleton resources** with no ambiguity are named after the kind alone — `namespace.yaml`, `middleware.yaml`, `cluster-issuer.yaml`, `application.yaml`.

### Labels
Every resource carries these two labels:
```yaml
labels:
  app.kubernetes.io/name: <component>   # frontend | backend | postgres | mailhog
  app.kubernetes.io/part-of: todo-app
```
- `app.kubernetes.io/name` is the single source of truth for selectors (`matchLabels` and Service `selector`).
- `app.kubernetes.io/part-of` groups all resources under the project umbrella.
- Do not introduce custom label keys or shorthand values — follow the `app.kubernetes.io/*` recommended label scheme.
