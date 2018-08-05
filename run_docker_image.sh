#!/usr/bin/env bash

docker rmi scottleedavis/mattermost-remind-app
docker pull scottleedavis/mattermost-remind-app
docker run -p 8080:8080 -e "REMIND_USER=5okns4uk9ffauy16q8rkhe9zjc" -e "REMIND_SLASH_TOKEN=7q61n4m5mfbebpme4ga5nu1gdh" -e "REMIND_WEBHOOK_URL=http://127.0.0.1:8065/hooks/jqg3bkxbip8hzgsrsnzep5jqyc" scottleedavis/mattermost-remind-app:latest
