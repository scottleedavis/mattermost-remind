#!/usr/bin/env bash

./run_docker_build.sh

./run_dockerhub_push.sh

./run_gcloud_push.sh

./run_kubernetes_redeploy.sh

