#!/usr/bin/env bash

DOMAIN="ec2-18-216-218-171.us-east-2.compute.amazonaws.com"

mvn clean install
mvn -f ./chat/pom.xml dockerfile:build
mvn -f ./web-customers/pom.xml dockerfile:build
mvn -f ./web-moderators/pom.xml dockerfile:build

docker run -d -e VIRTUAL_HOST=${DOMAIN} ffriends/web-customers:latest
docker run -d -e VIRTUAL_HOST="admin."${DOMAIN} ffriends/web-moderators:latest
docker run -d -e VIRTUAL_HOST="chat."${DOMAIN} ffriends/chat:latest

docker run -d -p 80:80 -v /var/run/docker.sock:/tmp/docker.sock:ro jwilder/nginx-proxy