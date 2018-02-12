#!/bin/sh

./gradlew wrapper --gradle-version 4.5.1 --distribution-type all

./gradlew cleanEclipse
./gradlew clean

./gradlew eclipse

./gradlew build -x test
