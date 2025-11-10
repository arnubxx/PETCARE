#!/usr/bin/env bash
# Helper to start Tomcat with PETCARE_DB_* env vars exported in this shell.
# Edit TOMCAT_HOME below if your Tomcat is elsewhere. Run from project root.

set -euo pipefail
# --- edit these values if needed ---
TOMCAT_HOME="/opt/homebrew/opt/tomcat/libexec"
# load .env if present (optional)
if [ -f .env ]; then
  # shellcheck disable=SC1090
  source .env
fi

: "Ensure required env vars are set (you can also set them in .env or export them manually)"
: "PETCARE_DB_URL=${PETCARE_DB_URL:-}", : "PETCARE_DB_USER=${PETCARE_DB_USER:-}", : "PETCARE_DB_PASSWORD=${PETCARE_DB_PASSWORD:-}"

if [ -z "${PETCARE_DB_URL:-}" ] || [ -z "${PETCARE_DB_USER:-}" ]; then
  echo "Please set PETCARE_DB_URL and PETCARE_DB_USER (either in .env or export them)" >&2
  echo "You can copy .env.example -> .env and edit it." >&2
  exit 1
fi

if [ ! -d "$TOMCAT_HOME" ]; then
  echo "Tomcat not found at $TOMCAT_HOME. Update TOMCAT_HOME in this script." >&2
  exit 1
fi

echo "Using TOMCAT_HOME=$TOMCAT_HOME"

# Export vars for Tomcat process
export PETCARE_DB_URL
export PETCARE_DB_USER
export PETCARE_DB_PASSWORD

# Stop Tomcat if running
if [ -f "$TOMCAT_HOME/bin/shutdown.sh" ]; then
  echo "Stopping Tomcat (if running)..."
  "$TOMCAT_HOME/bin/shutdown.sh" || true
  sleep 1
fi

# Start Tomcat in this shell (Tomcat will inherit env vars)
echo "Starting Tomcat..."
"$TOMCAT_HOME/bin/startup.sh"

echo "Tomcat started. Tail logs with: tail -f $TOMCAT_HOME/logs/catalina.out"
