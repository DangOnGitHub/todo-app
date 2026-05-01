# Docker Pipeline

## Overview

Three GitHub Actions workflows manage the full image lifecycle:

| Workflow | Trigger | Outcome |
|---|---|---|
| `ci.yaml` | Pull request → `main` | Build + scan (no push) |
| `cd-main.yaml` | Push to `main` | Build + scan + push `edge` tags |
| `cd-release.yaml` | Push of `v*.*.*` tag | Build + scan + push semver tags |

Both `backend` and `frontend` images are built and pushed in parallel to:
```
ghcr.io/dangongithub/todo-app/backend
ghcr.io/dangongithub/todo-app/frontend
```

## Versioning

The git tag is the single source of truth. `build.gradle.kts` and `package.json` version fields are not used by the pipeline — do not update them for releases.

| Event | Image tags |
|---|---|
| Pull request | `pr-<number>` (local only, not pushed) |
| Push to `main` | `edge`, `main-<short-sha>` |
| Push of `v1.2.3` | `1.2.3`, `1.2`, `1`, `latest` |

`latest` is only updated on versioned tag pushes, so it always points to the last stable release.

## Cutting a Release

```bash
git tag -a v1.0.0 -m "Release 1.0.0"
git push origin v1.0.0
```

`cd-release.yaml` triggers automatically and pushes all semver tags if the vulnerability scan passes.

## Vulnerability Scanning

Trivy scans each image locally before it is pushed to the registry. A vulnerable image never enters GHCR.

| Trigger | Blocking severity |
|---|---|
| PR | None (informational — results appear in Security tab) |
| Push to `main` | `CRITICAL` |
| Version tag push | `HIGH`, `CRITICAL` |

CVEs with no available fix are ignored (`ignore-unfixed: true`) to reduce noise from Alpine base images. Scan results are uploaded to **Security > Code Scanning** on every run.

## One-time GitHub Setup

1. **Workflow permissions** — Settings > Actions > General → set to "Read and write permissions". This grants `GITHUB_TOKEN` the `write:packages` scope needed to push to GHCR.

2. **Frontend API URL** — Settings > Secrets and Variables > Actions > Variables → add `VITE_API_URL` (e.g. `https://api.yourdomain.com`). This is baked into the frontend image at build time.

3. **Make packages public** — after the first push, go to `github.com/users/DangOnGitHub/packages (note: GHCR normalises the owner to lowercase — `dangongithub`)`, open each package, and set visibility to Public. This allows PR builds to read the build cache without registry credentials.

## Build Cache

Layer cache is stored in GHCR alongside the images (`:buildcache` tag) using BuildKit registry cache with `mode=max`. This caches all intermediate layers — including the expensive Gradle dependency resolution and `npm ci` steps — across workflow runs.
