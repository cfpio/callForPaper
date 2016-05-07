#!/bin/bash

docker kill mysql-cfp-dev
docker rm -v mysql-cfp-dev

docker run --name mysql-cfp-dev -d -p 3306:3306 \
  -e MYSQL_ROOT_PASSWORD=cfpbreizhroot \
  -e MYSQL_DATABASE=cfpdev \
  -e MYSQL_USER=cfpdev \
  -e MYSQL_PASSWORD=galettesaucisse \
  mysql:5.7

