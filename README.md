# mattermost-remind [![Build Status](https://travis-ci.org/scottleedavis/mattermost-remind.svg?branch=master)](https://travis-ci.org/scottleedavis/mattermost-remind)
A /remind slash command for [Mattermost](https://mattermost.com/) built with [Spring Boot](https://spring.io/projects/spring-boot)

`/remind [@someone or #channel] [what] [when]`

![set_reminder](set_reminder.png)
![reminded](reminded.png)

### requirements
* Build: [Maven](https://maven.apache.org/download.cgi) & [Java8](http://openjdk.java.net/install/)
    * `mvn package`
* Run: [Java8 JRE](http://openjdk.java.net/install/)
* Use: [Mattermost](https://mattermost.com/) 

### setup 
##### Datasource
* Default database is [h2](http://www.h2database.com/html/main.html) (an in-memory database)
* [SQL Server](https://www.microsoft.com/en-us/sql-server/default.aspx) can be used by changing [application.properties](src/main/resources/application.properties) 
  ```$xslt
    spring.datasource.url=jdbc:sqlserver://YOUR_DATABASE_SERVER;databaseName=YOUR_DATABASE_NAME
    spring.datasource.username=YOUR_DATABASE_USER
    spring.datasource.password=YOUR_DATABASE_PASSWORD
    spring.datasource.driverClassName=com.microsoft.sqlserver.jdbc.SQLServerDriver
    spring.jpa.hibernate.dialect=org.hibernate.dialect.SQLServer2012Dialect
  ```
  _[reminders.example.sql](reminders.example.sql)_

##### Mattermost Integration
_Requires slash command and webhook integrations_
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
  * set mattermost system icon
  * put token in [application.properties](src/main/resources/application.properties) `slashCommandToken=<YOUR_TOKEN>`
* create incoming webhook
  * Title & Description: `Set a reminder`
  * Channel: `Town Square`  <= (won't be used)
  * Username: `mattermost-remind`
  * set mattermost system icon
  * put webhook URL in [application.properties](src/main/resources/application.properties) `webhookUrl=<YOUR_WEBHOOK>`

### execution
* Running via java
  * `java -jar target/mattermost-remind-0.0.1-SNAPSHOT.jar`
* Running via tomcat
  * [Packaging as a .war](https://docs.spring.io/spring-boot/docs/current/reference/htmlsingle/#build-tool-plugins-maven-packaging)
* Running via docker
  * _todo_

### usage

##### supported features
* **/remind help**
* **/remind list**
* **/remind [who] [what] in [# (seconds|minutes|hours|days|weeks|months|years)]**
  * `/remind me Do the dishes in 1 day`
  * `/remind @jessica about the interview in three hours`
* _(in progress)_ **/remind [who] [what] at [(noon|midnight|one..twelve|00:00am/pm|0000)]**  
  * `/remind @lima Lunch time! at noon`
  * `/remind #test-team New tests at two`
  * `/remind me Pickup kids at 4:30pm`
  
### todos 
* button interactions
* past & incomplete reminders
* ensure cannot set recurring reminders for other users
* ensure channel reminders can’t be snoozed
* future planned features
  * Days of the week
    * `/remind #design Join the meeting Monday`
    * `/remind me Go to movies on Friday`
  * Dates
    * `/remind @lima Submit report May 30`
    * `/remind #management Send annual salary review report on December 15`
  * Recurring reminders
    * `/remind me Drink water Everyday`
    * `/remind me to schedule annual reviews every January 25`
    * `/remind #design Design critique meeting every Thursday`
    * `/remind me Physiotherapy after work every other Wednesday`
    * `/remind me to update the team meeting agenda on Mondays`
    * `/remind me to attend the team meeting at 11:00 every Tuesday`
    * `/remind me to drink water at 3pm every day`
    * `/remind #team-alpha to update the project status every Monday at 9am`
  * Alternate ordering `/remind [who] [when] [what]`
    * `/remind me on June 1st to wish Linda happy birthday`
    * `/remind @peter tomorrow "Please review the office seating plan"`

### notes
* You can’t set recurring reminders for other members.
* Channel reminders can’t be snoozed.
* Message buttons don’t show up for `/remind` in channels other than with yourself
  * [This is because Ephemeral messages do not have a state, and therefore do not support interactive message buttons at this time.](https://docs.mattermost.com/developer/interactive-message-buttons.html#troubleshooting)
