# mattermost-remind
A /remind slash command for mattermost built with [Spring Boot](https://spring.io/projects/spring-boot)

`/remind [@someone or #channel] [what] [when]`

### requirements
* Building with [Maven](https://maven.apache.org/download.cgi) & [Java8](http://openjdk.java.net/install/)
    * `mvn package`
* Running [Java8 JRE](http://openjdk.java.net/install/)
* Using [Mattermost](https://mattermost.com/) 
  * Tested agaist  Version: 5.0.0-rc1  
  * (other versions could likely work great too)

### setup 
##### Datasource
* Default database is [h2](http://www.h2database.com/html/main.html) (an in-memory database)
* To use another database edit [application.properties](src/main/resources/application.properties) with datasource details

##### Integration into Mattermost 
(requires a slash command and webhook)
* Ensure Custom Integrations (in System Console) has the following enabled
  * `Enable Incoming Webhooks`
  * `Enable Custom Slash Commands`
  * `Enable integrations to override usernames`
    * OR... create a `mattermost-remind` user to setup the slash command and webhook
* create `remind` as slash command
  * Title & Autocomplete: `Set a reminder`
  * Response Username: `mattermost-remind`
  * Autocomplete hint: `[@someone or #channel] [what] [when]`
  * Request URL: `<path_to_mattermost-remind>/remind`
  * put token in [application.properties](src/main/resources/application.properties) `slashCommandToken=<YOUR_TOKEN>`
* create incoming webhook
  * Title & Description: `Set a reminder`
  * Channel: `Town Square`  <= (won't be used)
  * Username: `mattermost-remind`
  * put webhook URL in [application.properties](src/main/resources/application.properties) `webhookUrl=<YOUR_WEBHOOK>`

### execution
* Running via java
  * `java -jar target/mattermost-remind-0.0.1-SNAPSHOT.jar`
* Running via tomcat
  * _todo_
* Running via docker
  * _todo_

### usage

##### supported features
* /remind help
* /remind list
* /remind [who] [what] [in # (seconds|minutes|hours|days|weeks|months|years)]
  * example: `/remind me Do the dishes in 2 days`

##### todos
* setup appropriate icons
* button/link interactions on reminders
* manage past & incomplete reminders
* complete planned supported features
* planned supported features
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
* You can’t set recurring reminders for other members.
* Channel reminders can’t be snoozed.

### troubleshooting
* Message buttons don’t show up for `/remind` in channels others than with yourself
  * [This is because Ephemeral messages do not have a state, and therefore do not support interactive message buttons at this time.](https://docs.mattermost.com/developer/interactive-message-buttons.html#troubleshooting)
