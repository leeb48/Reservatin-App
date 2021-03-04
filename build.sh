#!/bin/bash

mvn -f server/pom.xml package -DskipTests
echo "maven build complete"
docker-compose build --no-cache backend nginx
echo "docker-compose build compelte"
docker-compose up --force-recreat