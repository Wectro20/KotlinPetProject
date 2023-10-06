#!/bin/bash

./gradlew build -x test

docker build .

docker-compose up
