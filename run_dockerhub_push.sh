#!/usr/bin/env bash

#export REMIND_SLASH_TOKEN="7q61n4m5mfbebpme4ga5nu1gdh"
#export REMIND_WEBHOOK_URL="http://127.0.0.1:8065/hooks/jqg3bkxbip8hzgsrsnzep5jqyc"

docker tag scottleedavis/mattermost-remind-app:latest scottleedavis/mattermost-remind-app
docker push scottleedavis/mattermost-remind-app
