#!/bin/sh
GRADLE_OPTS="-Xmx4096m -Xms4096m -XX:+CMSClassUnloadingEnabled -XX:+HeapDumpOnOutOfMemoryError" gradle -i test
