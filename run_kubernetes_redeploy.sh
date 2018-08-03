#!/bin/sh

kubectl set image deployment/mattermost-remind  mattermost-remind=scottleedavis/mattermost-remind-app:latest
kubectl rollout status deployment/mattermost-remind

