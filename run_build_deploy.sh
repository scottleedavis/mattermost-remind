#!/usr/bin/env bash

./run_docker_build.sh

./run_dockerhub_push.sh

docker rmi scottleedavis/mattermost-remind-app
docker pull scottleedavis/mattermost-remind-app

./run_gcloud_push.sh

./run_kubernetes_deploy.sh

