#!/usr/bin/env bash
# Run this script to initialize the petcare DB using the mysql client.
# Usage: ./scripts/init_db.sh <mysql_user> (it will prompt for password)

set -euo pipefail
if ! command -v mysql >/dev/null 2>&1; then
  echo "mysql client not found. Install MySQL client or ensure 'mysql' is on PATH." >&2
  exit 1
fi

SQL_FILE="$(dirname "$0")/init_db.sql"
if [ ! -f "$SQL_FILE" ]; then
  echo "SQL file not found: $SQL_FILE" >&2
  exit 1
fi

MYSQL_USER=${1:-root}
# run mysql and let it ask for password
mysql -u "$MYSQL_USER" -p < "$SQL_FILE"

echo "Database initialized (petcare)."
