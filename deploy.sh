#!/bin/bash

git pull && \
docker-compose kill && \
docker ps -a | grep -E 'backend|webapp' | awk '{print $1}' | xargs docker rm -f && \
docker-compose pull && \
docker-compose build && \
docker-compose up -d && \
docker images -qf "dangling=true" | xargs docker rmi -f