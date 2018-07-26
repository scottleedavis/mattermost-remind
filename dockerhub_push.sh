#!/usr/bin/env bash

./mvnw install dockerfile:build
docker tag scottleedavis/mattermost-remind:latest scottleedavis/mattermost-remind
docker push scottleedavis/mattermost-remind
