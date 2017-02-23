#!/bin/bash

set -x -e

#
# Build binaries using docker
#
docker build -t callforpapers-${BUILD_NUMBER} -f Dockerfile.build .

#
# Prepare distribution folder
#
rm -rf dist
mkdir -p dist/target
container=$(docker create callforpapers-${BUILD_NUMBER})
docker cp $container:/work/target/call-for-paper.jar dist/target/call-for-paper.jar
docker rm $container
docker rmi callforpapers-${BUILD_NUMBER}
cp Dockerfile dist/Dockerfile

#
# Build production docker image
#
docker build -t cfpio/callforpapers:1.0.${BUILD_NUMBER}  -t cfpio/callforpapers:latest --label "org.label-schema.vcs-ref-commit=$GIT_COMMIT" dist

#
# Push to Dockerhub
#
docker push cfpio/callforpapers:1.0.${BUILD_NUMBER}
docker push cfpio/callforpapers:latest

#
# Clean up built images
#
docker rmi cfpio/callforpapers
docker rmi cfpio/callforpapers:1.0.${BUILD_NUMBER}
