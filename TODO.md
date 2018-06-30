# todos 

* known bugs
  * updates needs logging
  * ensure day/date select automatically selects 9AM 
  * the word 'to' needs to be removed from start of a message
  * day/date reminders need to be at 9am us with at
  * error response needs to have 'show some examples button'
  * Quote blocks need to be respected " food"
  * 'in ##s' need to work  (only uses space currently)

* upcoming features
  * create configuration for postgresql
    * 9.4.15-0+deb8u1  (test locally with docker)
  * snooze choices 
    * (currently only 20 minutes)
    * try reappearing buttons?  or initial row of buttons...
    * awaiting [Interactive message button dropdown](https://forum.mattermost.org/t/interactive-message-button-dropdown/5219)   
  * remind list 
    * awaiting [flat buttons/links * delete/complete interactions](https://forum.mattermost.org/t/interactive-flat-message-button-links/5220)
    * try list of attachments in meantime...
    * past & complete view
  * ensure cannot set recurring reminders for other users
  * ensure channel reminders can’t be snoozed
  * /remind help should display command patterns... with button on bottom saying show examples
  
  
* future planned commands
  * Times recurring (at <time> every <recurrent>)
    * `/remind me to attend the team meeting at 11:00 every Tuesday`
    * `/remind me to drink water at 3pm every day`
  * Days of the week (without 'on' <day>)
    * `/remind #design Join the meeting Monday`
    * `/remind me Smile tomorrow`
    * `/remind me Drink water Everyday`
  * Days of the week recurring (by plural day name)
    * `/remind me to update the team meeting agenda on Mondays`
  * Dates (without 'on' <date>)
    * `/remind @lima Submit report May 30`
  * Dates (with 'on' <date> 'at' <time>)
    * `/remind me foo on Friday at 12pm`
  * Alternate ordering `/remind [who] [when] [what]`
    * `/remind me on June 1st to wish Linda happy birthday`
    * `/remind @peter tomorrow "Please review the office seating plan"`
