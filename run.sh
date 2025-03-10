#!/bin/sh

set -e # Exit early if any commands fail

# Define the directory for the built .jar file in the Maven 'target' directory
TARGET_DIR="$(dirname "$0")/target"
JAR_FILE="$TARGET_DIR/dmsrosa-dns-server.jar"

# Ensure compile steps are run within the repository directory
(
  cd "$(dirname "$0")" # Change to the directory where the script is located
  mvn -B clean package -DskipTests # Run Maven build
)

# Check if the .jar file exists after the build
if [ ! -f "$JAR_FILE" ]; then
  echo "Error: $JAR_FILE not found. Maven build may have failed or the file is missing."
  exit 1
fi

clear

echo "Starting server..."

# Run the Java application if the .jar file exists
exec java -jar "$JAR_FILE" "$@"
