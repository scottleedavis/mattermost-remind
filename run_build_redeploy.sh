#!/usr/bin/env bash

# build docker image
./mvnw install dockerfile:build

# tag image and push to docker cloud
#docker tag scottleedavis/mattermost-remind-app:latest scottleedavis/mattermost-remind-app
#docker push scottleedavis/mattermost-remind-app
docker tag scottleedavis/mattermost-remind-app:latest scottleedavis/mattermost-remind
docker push scottleedavis/mattermost-remind

# tag image and push to google cloud
# gcloud docker -- push scottleedavis/mattermost-remind-app:latest
docker tag scottleedavis/mattermost-remind-app gcr.io/mattermost-remind/scottleedavis/mattermost-remind-app:latest
docker push gcr.io/mattermost-remind/scottleedavis/mattermost-remind-app:latest

# deploy latest image to kubernetes
kubectl set image deployment mattermost-remind  mattermost-remind=gcr.io/mattermost-remind/scottleedavis/mattermost-remind-app:latest
kubectl rollout status deployment mattermost-remind


