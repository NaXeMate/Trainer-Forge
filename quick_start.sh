#!/usr/bin/env bash

set -Eeuo pipefail

readonly SCRIPT_DIR="$(cd -- "$(dirname -- "${BASH_SOURCE[0]}")" && pwd -P)"
readonly BACKEND_DIR="${SCRIPT_DIR}/backend"
readonly ENV_FILE="${SCRIPT_DIR}/.env"
readonly COMPOSE_FILE="${SCRIPT_DIR}/docker-compose.yml"
readonly BACKEND_PORT=8080
readonly DATABASE_WAIT_SECONDS=90

info() {
    printf '[TrainerForge] %s\n' "$1"
}

fail() {
    printf '[TrainerForge] Error: %s\n' "$1" >&2
    exit 1
}

require_command() {
    command -v "$1" >/dev/null 2>&1 || fail "'$1' was not found in PATH."
}

select_java() {
    # macOS registers both system-wide and per-user JDK installations.
    if [[ -z "${JAVA_HOME:-}" && "$(uname -s)" == "Darwin" ]]; then
        JAVA_HOME="$(/usr/libexec/java_home -v 27 2>/dev/null)" ||
            fail "JDK 27 was not found. Install it or set JAVA_HOME to its home directory."
        export JAVA_HOME
    fi

    if [[ -n "${JAVA_HOME:-}" ]]; then
        [[ -x "${JAVA_HOME}/bin/java" && -x "${JAVA_HOME}/bin/javac" ]] ||
            fail "JAVA_HOME must point to a JDK 27 installation with java and javac."
        export PATH="${JAVA_HOME}/bin:${PATH}"
    fi

    require_command java
    require_command javac
    local compiler_version
    compiler_version="$(javac -version 2>&1)" || fail "Could not read the JDK version."
    [[ "$compiler_version" =~ ^javac[[:space:]]27([.+-]|$) ]] ||
        fail "TrainerForge requires JDK 27; found ${compiler_version}. Select JDK 27 with JAVA_HOME."
    info "Using ${compiler_version}${JAVA_HOME:+ (${JAVA_HOME})}."
}

trim() {
    local value="$1"
    value="${value#"${value%%[![:space:]]*}"}"
    value="${value%"${value##*[![:space:]]}"}"
    printf '%s' "$value"
}

load_backend_environment() {
    [[ -f "$ENV_FILE" ]] || fail "${ENV_FILE} is missing. Create it from .env.example."

    # Restrict access before reading credentials. Never print their values.
    chmod 600 "$ENV_FILE" || fail "Could not secure the permissions of ${ENV_FILE}."

    local line key value first_char last_char
    while IFS= read -r line || [[ -n "$line" ]]; do
        line="${line%$'\r'}"
        [[ "$line" =~ ^[[:space:]]*$ ]] && continue
        [[ "$line" =~ ^[[:space:]]*# ]] && continue

        if [[ "$line" =~ ^[[:space:]]*export[[:space:]]+ ]]; then
            line="${line#*export}"
        fi

        [[ "$line" == *"="* ]] || continue
        key="$(trim "${line%%=*}")"
        value="$(trim "${line#*=}")"

        case "$key" in
            DB_URL|DB_USERNAME|DB_PASSWORD|JWT_SECRET|JWT_EXPIRATION_MS)
                if (( ${#value} >= 2 )); then
                    first_char="${value:0:1}"
                    last_char="${value: -1}"
                    if [[ "$first_char" == '"' && "$last_char" == '"' ]] ||
                       [[ "$first_char" == "'" && "$last_char" == "'" ]]; then
                        value="${value:1:${#value}-2}"
                    fi
                fi

                printf -v "$key" '%s' "$value"
                export "$key"
                ;;
        esac
    done < "$ENV_FILE"

    local required_variable
    for required_variable in DB_URL DB_USERNAME DB_PASSWORD JWT_SECRET; do
        [[ -n "${!required_variable:-}" ]] || fail "${required_variable} is missing or empty in .env."
    done
}

validate_jwt_secret() {
    require_command openssl

    local decoded_length
    decoded_length="$(printf '%s' "$JWT_SECRET" | openssl base64 -d -A 2>/dev/null | wc -c | tr -d '[:space:]')" ||
        fail "JWT_SECRET does not contain valid Base64."

    [[ "$decoded_length" =~ ^[0-9]+$ ]] || fail "Could not validate JWT_SECRET."
    (( decoded_length >= 32 )) || fail "JWT_SECRET must encode at least 32 bytes for secure JWT signing."
}

ensure_docker_daemon() {
    if docker info >/dev/null 2>&1; then
        return
    fi

    if [[ "$(uname -s)" == "Darwin" ]] && [[ -d /Applications/Docker.app ]]; then
        info "Docker is not running; starting Docker Desktop..."
        open -gja Docker

        local attempt
        for (( attempt = 1; attempt <= 60; attempt++ )); do
            docker info >/dev/null 2>&1 && return
            sleep 2
        done
    fi

    fail "Docker is unavailable. Start the Docker service and run this script again."
}

start_database() {
    info "Starting PostgreSQL..."
    docker compose --project-directory "$SCRIPT_DIR" -f "$COMPOSE_FILE" up -d db

    local container_id health_state attempt
    container_id="$(docker compose --project-directory "$SCRIPT_DIR" -f "$COMPOSE_FILE" ps -q db)"
    [[ -n "$container_id" ]] || fail "Docker Compose did not create the PostgreSQL container."

    for (( attempt = 1; attempt <= DATABASE_WAIT_SECONDS; attempt++ )); do
        health_state="$(docker inspect --format '{{if .State.Health}}{{.State.Health.Status}}{{else}}{{.State.Status}}{{end}}' "$container_id" 2>/dev/null || true)"
        case "$health_state" in
            healthy)
                info "PostgreSQL is ready."
                return
                ;;
            unhealthy|exited|dead)
                docker compose --project-directory "$SCRIPT_DIR" -f "$COMPOSE_FILE" logs --tail=30 db >&2
                fail "PostgreSQL stopped with status '${health_state}'."
                ;;
        esac
        sleep 1
    done

    fail "PostgreSQL was not ready after ${DATABASE_WAIT_SECONDS} seconds."
}

check_backend_port() {
    if command -v curl >/dev/null 2>&1 &&
       curl --silent --fail --max-time 2 "http://localhost:${BACKEND_PORT}/actuator/health" >/dev/null 2>&1; then
        info "The backend is already available at http://localhost:${BACKEND_PORT}."
        exit 0
    fi

    if command -v lsof >/dev/null 2>&1 &&
       lsof -nP -iTCP:"${BACKEND_PORT}" -sTCP:LISTEN >/dev/null 2>&1; then
        fail "Port ${BACKEND_PORT} is already occupied by another process."
    fi
}

main() {
    require_command docker
    select_java
    [[ -x "${BACKEND_DIR}/mvnw" ]] || fail "The executable Maven Wrapper was not found at backend/mvnw."
    [[ -f "$COMPOSE_FILE" ]] || fail "docker-compose.yml was not found."

    load_backend_environment
    validate_jwt_secret
    ensure_docker_daemon
    start_database
    check_backend_port

    export SPRING_PROFILES_ACTIVE=dev

    info "Starting the backend at http://localhost:${BACKEND_PORT}..."
    info "Press Ctrl+C to stop Spring Boot; PostgreSQL will remain available."

    cd "$BACKEND_DIR"
    exec ./mvnw clean spring-boot:run
}

main "$@"
