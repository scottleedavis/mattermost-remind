# mattermost-remind
A /remind slash command for mattermost

`/remind [@someone or #channel] [what] [when]`

### installation
* By default an in memory database is used (h2).  To use another database edit [application.properties](src/main/resources/application.properties) with datasource details
_todo_
* Running
  * standalone _todo_
  * tomcat _todo_


### setup 
Integration into Mattermost requires a slash command and webhook
* create /remind as slash command
  * Description: Set a reminder
  * Usage hint: [@someone or #channel] [what] [when]
  * put token in [application.properties](src/main/resources/application.properties) `slashCommandToken=<YOUR_TOKEN>`
* create incoming webhook
  * put token in [application.properties](src/main/resources/application.properties) `webhookUrl=<YOUR_TOKEN>`

### todos
* button/link interactions on reminders
* manage past & incomplete reminders
* complete planned supported features

### supported features
* /remind help
* /remind list
* /remind [who] [what] in # (seconds|minutes|hours|days|weeks|months|years)
  * example: `/remind me Do the dishes in 2 days`

### planned supported features
* /remind me Drink water Everyday
* /remind @lima	Submit report May 30
* /remind #design Join the meeting Monday
* /remind me to drink water at 3pm every day
* /remind me on June 1st to wish Linda happy birthday
* /remind #team-alpha to update the project status every Monday at 9am
* /remind @jessica about the interview in 3 hours
* /remind @peter tomorrow "Please review the office seating plan"
* /remind @lima Lunch time! at 12:30pm
* /remind #management Send annual salary review report on December 15
* /remind #design Design critique meeting every Thursday
* /remind me Physiotherapy after work every other Wednesday
* /remind me to update the team meeting agenda on Mondays
* /remind me to attend the team meeting at 11:00 every Tuesday
* /remind me to schedule annual reviews every January 25

### notes
* Note: You can’t set recurring reminders for other members.
* Note: Channel reminders can’t be snoozed.
* Delete any of the reminders you no longer need or created by mistake.
* Mark reminders as Complete and simply click View completed reminders to see a full list.
