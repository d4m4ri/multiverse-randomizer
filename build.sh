#!/bin/sh

gradle cleanEclipse
gradle clean

gradle eclipse

gradle build -x test
