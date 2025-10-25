#!/usr/bin/env sh
# Minimal gradlew script
APP_BASE_DIR=$(cd "$(dirname "$0")"; pwd)
CLASSPATH=$APP_BASE_DIR/gradle/wrapper/gradle-wrapper.jar
JAVA_EXE=java
# Download wrapper jar if missing
if [ ! -f "$CLASSPATH" ]; then
  echo "Missing gradle-wrapper.jar (Android Studio will regenerate it on sync)."
fi
exec "$JAVA_EXE" -classpath "$CLASSPATH" org.gradle.wrapper.GradleWrapperMain "$@"
