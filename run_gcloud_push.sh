#!/usr/bin/env bash

# gcloud docker -- push scottleedavis/mattermost-remind-app:latest
docker tag scottleedavis/mattermost-remind-app gcr.io/mattermost-remind/scottleedavis/mattermost-remind-app:latest
docker push gcr.io/mattermost-remind/scottleedavis/mattermost-remind-app:latest
