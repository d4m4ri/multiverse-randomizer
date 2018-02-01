#!/bin/sh
java -Xmx4096m -Xms4096m -XX:+CMSClassUnloadingEnabled -XX:+HeapDumpOnOutOfMemoryError -jar build/libs/multiverse-randomizer-0.1.jar
