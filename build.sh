#!/bin/sh

./gradlew wrapper --gradle-version 4.3-rc-2 --distribution-type all

./gradlew cleanEclipse
./gradlew clean

./gradlew eclipse

./gradlew build -x test
