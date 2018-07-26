#!/usr/bin/env bash

./mvnw -DREMIND_WEBHOOK=http://localhost:12345 -DREMIND_SLASH=123467890 install dockerfile:build
docker tag scottleedavis/mattermost-remind:latest scottleedavis/mattermost-remind
docker push scottleedavis/mattermost-remind
