#!/usr/bin/env bash

# clean, pull, run the docker.io container
docker rmi scottleedavis/mattermost-remind
docker pull scottleedavis/mattermost-remind
docker run -p 8080:8080 -e "REMIND_USER=5okns4uk9ffauy16q8rkhe9zjc" -e "REMIND_SLASH_TOKEN=7q61n4m5mfbebpme4ga5nu1gdh" -e "REMIND_WEBHOOK_URL=http://127.0.0.1:8065/hooks/jqg3bkxbip8hzgsrsnzep5jqyc" scottleedavis/mattermost-remind:latest

## clean, pull, run the google docker container
#docker rmi gcr.io/mattermost-remind/scottleedavis/mattermost-remind-app
#docker pull gcr.io/mattermost-remind/scottleedavis/mattermost-remind-app
#docker run -p 8080:8080 -e "REMIND_USER=5okns4uk9ffauy16q8rkhe9zjc" -e "REMIND_SLASH_TOKEN=7q61n4m5mfbebpme4ga5nu1gdh" -e "REMIND_WEBHOOK_URL=http://127.0.0.1:8065/hooks/jqg3bkxbip8hzgsrsnzep5jqyc" gcr.io/mattermost-remind/scottleedavis/mattermost-remind-app:latest
