#!/bin/sh

#
# Copyright © 2015-2021 the original authors.
#
# Licensed under the Apache License, Version 2.0 (the "License");
# ...
#

# Attempt to set APP_HOME
APP_HOME=$(cd "$(dirname "$0")" && pwd -P)
APP_BASE_NAME=$(basename "$0")

# Determine the Java command to use to start the JVM.
if [ -n "$JAVA_HOME" ] ; then
    if [ -x "$JAVA_HOME/jre/sh/java" ] ; then
        JAVACMD="$JAVA_HOME/jre/sh/java"
    else
        JAVACMD="$JAVA_HOME/bin/java"
    fi
else
    JAVACMD="java"
fi

# Set default wrapper properties file location
WRAPPER_PROP="$APP_HOME/gradle/wrapper/gradle-wrapper.properties"
WRAPPER_JAR="$APP_HOME/gradle/wrapper/gradle-wrapper.jar"

if [ ! -f "$WRAPPER_JAR" ]; then
    echo "Downloading Gradle Wrapper JAR..."
    # Download wrapper jar if missing
    curl -sLo "$WRAPPER_JAR" "https://raw.githubusercontent.com/gradle/gradle/v8.10.2/gradle/wrapper/gradle-wrapper.jar" 2>/dev/null || \
    wget -qO "$WRAPPER_JAR" "https://raw.githubusercontent.com/gradle/gradle/v8.10.2/gradle/wrapper/gradle-wrapper.jar" 2>/dev/null
fi

exec "$JAVACMD" "-Dorg.gradle.appname=$APP_BASE_NAME" -classpath "$WRAPPER_JAR" org.gradle.wrapper.GradleWrapperMain "$@"
