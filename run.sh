#!/bin/sh
java -Xms2048m -Xmx8192m -XX:+HeapDumpOnOutOfMemoryError -jar build/libs/multiverse-randomizer-0.1.jar
