# Todo App

A full-stack todo application with JWT authentication.

- **Backend** — Spring Boot 4 / Java 25 / PostgreSQL
- **Frontend** — React 19 / TypeScript 6 / Vite + Nginx
- **API contract** — `openapi.yaml` (OpenAPI 3.1, single source of truth)

## Quick start

```bash
# Start the database and MailHog (email capture UI at http://localhost:8025)
docker compose up -d db mailhog

# Backend (from backend/)
./gradlew openApiGenerate
./gradlew bootRun

# Frontend (from frontend/)
npm install
npm run generate
npm run dev        # http://localhost:5173
```

## Environment variables

Copy `.env.example` and fill in the values. Required at runtime — the app refuses to start if any are missing.

## Run with Docker

```bash
docker compose up -d
```

Starts the database, backend, and frontend together. See `.env.example` for all required variables.

## Docker pipeline

See [docs/docker-pipeline.md](docs/docker-pipeline.md) for the CI/CD workflow, image tagging strategy, vulnerability scanning, and release process.
