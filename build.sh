#!/bin/sh

./gradlew wrapper --gradle-version 4.7 --distribution-type all

./gradlew cleanEclipse
./gradlew clean

./gradlew eclipse

./gradlew build -x test
