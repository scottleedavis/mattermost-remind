# todos 

### tasks

1. finishing up 'at' when handling (with unit tests) (see comments for remaining todo patterns in https://github.com/scottleedavis/mattermost-remind/blob/master/src/test/java/scottleedavis/mattermost/remind/reminders/OccurrenceTests.java)
1. 'on' when patterns.  (my approach is to try all the patterns in slack and then build the logic for them).  So for 'on', it looks like 'day of week', or 'date', or... (still need to check')
1. I haven't fully fleshed out the idea for recurring reminders in the db & function.   My passing thoughts were : 1) store the 'when' pattern with a reminder if it is recurring (db column).  2) when reminder triggers, set another reminder immediately using the 'when' pattern.   maybe as easy as that?   

### features
* create configuation for postgresql
  * 9.4.15-0+deb8u1  (test locally with docker)
* snooze choices 
  * (currently only 20 minutes)
  * awaiting [Interactive message button dropdown](https://forum.mattermost.org/t/interactive-message-button-dropdown/5219)    
* remind list 
  * awaiting [flat buttons/links * delete/complete interactions](https://forum.mattermost.org/t/interactive-flat-message-button-links/5220)
  * past & complete view
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