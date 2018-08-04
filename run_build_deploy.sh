#!/usr/bin/env bash

# build docker image
./mvnw install dockerfile:build

# tag image and push to docker cloud
docker tag scottleedavis/mattermost-remind-app:latest scottleedavis/mattermost-remind-app
docker push scottleedavis/mattermost-remind-app

# tag image and push to google cloud
# gcloud docker -- push scottleedavis/mattermost-remind-app:latest
docker tag scottleedavis/mattermost-remind-app gcr.io/mattermost-remind/scottleedavis/mattermost-remind-app:latest
docker push gcr.io/mattermost-remind/scottleedavis/mattermost-remind-app:latest

# deploy image to kubernetes
kubectl run mattermost-remind --image=scottleedavis/mattermost-remind-app --port 8080
