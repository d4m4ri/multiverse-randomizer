#!/bin/sh
./gradlew jar
java -Xmx4096m -Xms4096m -XX:+CMSClassUnloadingEnabled -XX:+HeapDumpOnOutOfMemoryError -jar build/libs/*.jar
