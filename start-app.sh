#!/bin/bash

./gradlew build

docker build .

docker-compose up
