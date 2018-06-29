# todos 

1. I haven't fully fleshed out the idea for recurring reminders in the db & function.   My passing thoughts were : 1) store the 'when' pattern with a reminder if it is recurring (db column).  2) when reminder triggers, set another reminder immediately using the 'when' pattern.   maybe as easy as that?   

* minor bugs
  * ensure day/date select automatically selects 9AM 
  * the word 'to' needs to be removed from start of a message
  * day/date reminders need to be at 9am us with at
  * error response needs to have 'show some examples button'
  * Quote blocks need to be respected " food"
  
* upcoming features
  * create configuation for postgresql
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
  * pattern match days of week better
    * `((mon|tues|wed(nes)?|thur(s)?|fri|sat(ur)?|sun)(day)?)`
      
  
* future planned commands
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
    * `/remind me to do things every monday and wednesday`
  * Alternate ordering `/remind [who] [when] [what]`
    * `/remind me on June 1st to wish Linda happy birthday`
    * `/remind @peter tomorrow "Please review the office seating plan"`
  * Misc
    * `/remind me Smile tomorrow`
    
