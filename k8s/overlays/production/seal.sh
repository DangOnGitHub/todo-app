#!/usr/bin/env bash
set -euo pipefail

CERT="$(dirname "$0")/pub-key.pem"
SECRETS="$(dirname "$0")/secrets"

kubeseal --format yaml --cert "$CERT" < "$SECRETS/backend-secret.yaml"  > "$SECRETS/sealed-backend-secret.yaml"
kubeseal --format yaml --cert "$CERT" < "$SECRETS/postgres-secret.yaml" > "$SECRETS/sealed-postgres-secret.yaml"

echo "Re-sealed: backend, postgres"
