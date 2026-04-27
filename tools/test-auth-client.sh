#!/usr/bin/env bash
set -euo pipefail

HOST="${1:-127.0.0.1}"
PORT="${2:-7777}"
COMMAND="${3:-login}"
USERNAME="${4:-dev}"
PASSWORD="${5:-dev-password}"

case "$COMMAND" in
  login)
    printf 'LOGIN_REQUEST|%s|%s\n' "$USERNAME" "$PASSWORD" | nc "$HOST" "$PORT"
    ;;
  register)
    printf 'REGISTER_REQUEST|%s|%s\n' "$USERNAME" "$PASSWORD" | nc "$HOST" "$PORT"
    ;;
  *)
    echo "Usage: $0 [host] [port] [login|register] [username] [password]" >&2
    exit 1
    ;;
esac
