#!/bin/bash

set -x -e

#
# Build production docker image
#
docker build -t cfpio/callforpapers:1.0.${BUILD_NUMBER}  -t cfpio/callforpapers:latest --label "org.label-schema.vcs-ref-commit=$GIT_COMMIT" .

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
