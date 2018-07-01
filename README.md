# mattermost-remind [![Build Status](https://travis-ci.org/scottleedavis/mattermost-remind.svg?branch=master)](https://travis-ci.org/scottleedavis/mattermost-remind) [![codecov](https://codecov.io/gh/scottleedavis/mattermost-remind/branch/master/graph/badge.svg)](https://codecov.io/gh/scottleedavis/mattermost-remind)

A /remind slash command for [Mattermost](https://mattermost.com/) built with [Spring Boot](https://spring.io/projects/spring-boot)

`/remind [@someone or #channel] [what] [when]`

![set_reminder](set_reminder.png)
![reminded](reminded.png)

### usage

##### supported features
* **/remind help**
* **/remind list**
* **/remind version**
* **/remind [who] [what] in [# (seconds|minutes|hours|days|weeks|months|years)]**
  * `/remind me Do the dishes in 1 day`
  * `/remind @jessica about the interview in three hours`
* **/remind [who] [what] at [(noon|midnight|one..twelve|00:00am/pm|0000)]**  
  * `/remind @lima Lunch time! at noon`
  * `/remind #test-team New tests at two`
  * `/remind me Pickup kids at 4:30pm`
* **/remind [who] [what] on [(Monday-Sunday|Month&Day|MM/DD/YYYY|MM/DD)]**
  * `/remind me Go to movies on Friday`
  * `/remind #management Send annual salary review report on December 15`
  * `/remind me Mow the lawn on 7th`
* _(in progress)_ **/remind [who] [what] every (other) [Monday-Sunday|Month&Day|MM/DD] (at) [time]**
  * `/remind me to schedule annual reviews every January 25`
  * `/remind #design Design critique meeting every Thursday at 12:30`
  * `/remind me Physiotherapy after work every other Wednesday`
  * `/remind #team-alpha to update the project status every Monday at 9am`
  * `/remind me to do things every monday and wednesday at noon`
  
### notes
* You can’t set recurring reminders for other members.
* Channel reminders can’t be snoozed.
* Message buttons don’t show up for `/remind` in channels other than with yourself
  * [This is because Ephemeral messages do not have a state, and therefore do not support interactive message buttons at this time.](https://docs.mattermost.com/developer/interactive-message-buttons.html#troubleshooting)

### releases

* Currently targeting [Release 0.0.2](https://github.com/scottleedavis/mattermost-remind/projects/1)

### bugs & issues

* Request a feature or report a bug in [Github issues](https://github.com/scottleedavis/mattermost-remind/issues).
* Don't have a github account?  [Use this form](https://gitreports.com/issue/scottleedavis/mattermost-remind/)

### todos

* See the [TODO](TODO.md) page for planned coming features.
* Want to contribute?  Fork this repository and submit a [Pull Request](https://github.com/scottleedavis/mattermost-remind/pulls).

### setup 

#### requirements
* Run: [Java8 JRE](http://openjdk.java.net/install/)
* Run: [Docker](https://www.docker.com/) (Optional)
* Use: [Mattermost](https://mattermost.com/) 

##### Datasource
* Default database is [h2](http://www.h2database.com/html/main.html) (an auto-generated in-memory database)
* [SQL Server](https://www.microsoft.com/en-us/sql-server/default.aspx) can be used by changing [application.properties](src/main/resources/application.properties) and creating a reminders table  (_[reminders.example.sqlserver.sql](scripts/reminders.example.sqlserver.sql)_)
  ```$xslt
    spring.datasource.url=jdbc:sqlserver://YOUR_DATABASE_SERVER;databaseName=YOUR_DATABASE_NAME
    spring.datasource.username=YOUR_DATABASE_USER
    spring.datasource.password=YOUR_DATABASE_PASSWORD
    spring.datasource.driverClassName=com.microsoft.sqlserver.jdbc.SQLServerDriver
    spring.jpa.hibernate.dialect=org.hibernate.dialect.SQLServer2012Dialect
  ```
* [PostgreSQL](https://www.postgresql.org/) can be used by changing [application.properties](src/main/resources/application.properties) and creating a reminders table (_[reminders.example.postgresql.sql](scripts/reminders.example.postgresql.sql)_)
  ```$xslt
    spring.datasource.url=jdbc:postgresql://YOUR_DATABASE_SERVER:5432/YOUR_DATABASE_NAME 
    spring.datasource.username=YOUR_DATABASE_USER 
    spring.datasource.password=postgres@YOUR_DATABASE_PASSWORD   
  ```
##### Mattermost Integration
_Requires slash command and webhook integrations_
* Ensure Custom Integrations (in System Console) has the following enabled
  * `Enable Incoming Webhooks`
  * `Enable Custom Slash Commands`
  * `Enable integrations to override usernames` & `Enable integrations to override profile picture icons`
    * OR... create a `mattermost-remind` user to setup the slash command and webhook with system icon
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

### build
* Build: [Maven](https://maven.apache.org/download.cgi) & [Java8](http://openjdk.java.net/install/)
  * `./mvnw  package`
* Building for tomcat
  * [Packaging as a .war](https://docs.spring.io/spring-boot/docs/current/reference/htmlsingle/#build-tool-plugins-maven-packaging)
* Build [Docker](https://www.docker.com/) Image (Optional)
  * `./mvnw install dockerfile:build`

### execution
* Running via java
  * `java -jar target/mattermost-remind-#.#.#.jar`
* Running via docker (Optional)
  * Using a locally built version `docker run -p 8080:8080 -t scottleedavis/mattermost-remind`
  * Using [dockerhub latest image](https://hub.docker.com/r/scottleedavis/mattermost-remind/): `docker run scottleedavis/mattermost-remind`

