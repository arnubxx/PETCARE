#!/usr/bin/env bash
set -euo pipefail

# Edit these paths if your Tomcat or MySQL connector are in different locations
TOMCAT_HOME="/opt/homebrew/opt/tomcat/libexec"

if [ ! -d "$TOMCAT_HOME" ]; then
  echo "Tomcat not found at $TOMCAT_HOME. Update TOMCAT_HOME in this script." >&2
  exit 1
fi

if command -v mvn >/dev/null 2>&1; then
  echo "Building WAR with Maven..."
  mvn clean package
  WAR_FILE="$(pwd)/target/PETCARE-1.0.0.war"
  if [ ! -f "$WAR_FILE" ]; then
    echo "WAR not found at $WAR_FILE" >&2
    exit 1
  fi
  echo "Deploying $WAR_FILE to Tomcat webapps..."
  cp "$WAR_FILE" "$TOMCAT_HOME/webapps/PETCARE.war"
  echo "Deployed. Restart Tomcat to pick up the new WAR or Tomcat may auto-deploy." 
else
  echo "Maven not found. Falling back to manual javac deployment (ensure MYSQL connector is present)." >&2
  echo "Edit this script to set MYSQL_CONNECTOR_JAR and re-run." >&2
  exit 1
fi
