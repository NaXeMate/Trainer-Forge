#!/usr/bin/env bash
set -euo pipefail

if [[ $# -ne 0 ]]; then
  echo "Usage: bash scripts/flyway-repair.sh" >&2
  exit 2
fi

repo_root=$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)
properties="$repo_root/.env.properties"

read_property() {
  if [[ -r "$properties" ]]; then
    awk -v key="$1" 'index($0, key "=") == 1 { sub(/^[^=]*=/, ""); sub(/\r$/, ""); value = $0 }
      END { print value }' "$properties"
  fi
}

export FLYWAY_URL="${DB_URL:-$(read_property DB_URL)}"
export FLYWAY_USER="${DB_USERNAME:-$(read_property DB_USERNAME)}"
export FLYWAY_PASSWORD="${DB_PASSWORD-$(read_property DB_PASSWORD)}"

if [[ -z "$FLYWAY_URL" || -z "$FLYWAY_USER" ]]; then
  echo "Set DB_URL and DB_USERNAME in .env.properties or in the environment." >&2
  exit 1
fi

cd "$repo_root/backend"
flyway_version=$(./mvnw -q help:evaluate -Dexpression=flyway.version -DforceStdout -Dstyle.color=never)
flyway_plugin="org.flywaydb:flyway-maven-plugin:$flyway_version"

# Read the SQL sources directly, even before rebuilding the backend.
./mvnw -Dflyway.locations=filesystem:src/main/resources/db/migration \
  "$flyway_plugin:repair" "$flyway_plugin:validate"
