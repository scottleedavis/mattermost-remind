package me.scottleedavis.mattermostremind.reminders;

import me.scottleedavis.mattermostremind.jpa.Reminder;
import me.scottleedavis.mattermostremind.messages.*;
import me.scottleedavis.mattermostremind.io.Webhook;
import me.scottleedavis.mattermostremind.jpa.ReminderRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.List;

@Service
public class Scheduler {

    private static Logger logger = LoggerFactory.getLogger(Scheduler.class);

    private static String helpMessage = ":wave: Need some help with `/remind`?\n" +
            "Use `/remind` to set a reminder for yourself, someone else, or for a channel. Some examples include:\n" +
            "• `/remind me to drink water at 3pm every day`\n" +
            "• `/remind me on June 1st to wish Linda happy birthday`\n" +
            "• `/remind #team-alpha to update the project status every Monday at 9am`\n" +
            "• `/remind @jessica about the interview in 3 hours`\n" +
            "• `/remind @peter tomorrow \"Please review the office seating plan\"`\n" +
            "Or, use `/remind list` to see the list of all your reminders.";

    private static String exceptionText = "Sorry, I didn’t quite get that. I’m easily confused. " +
            "Perhaps try the words in a different order? This usually works: " +
            "`/remind [@someone or #channel] [what] [when]`.\n";

    @Autowired
    ReminderRequest reminderRequest;

    @Autowired
    Webhook webhook;

    @Resource
    ReminderRepository reminderRepository;

    public Response setReminder(String userName, String message, String userId, String channelName) {

        Response response = new Response();
        try {
            String target = reminderRequest.findTarget(message);
            if( target.equals("help") ) {
                response.setText(helpMessage);
            } else if( target.equals("list") ) {
                response.setText(listReminders(userName));
            } else {
                String when = reminderRequest.findWhen(message);
                String actualMessage = message.replace(target, "").replace(when, "").trim();

                if (channelName.contains(userId)) {
                    Context context = new Context();
                    context.setAction("delete");
                    Integration integration = new Integration();
                    integration.setContext(context);
                    integration.setUrl("http://fooo.com");
                    Action action = new Action();
                    action.setIntegration(integration);
                    action.setName("View Reminders");

                    Attachment attachment = new Attachment();
                    attachment.setActions(Arrays.asList(action));
                    attachment.setText(scheduleReminder(target, userName, when, actualMessage));
                    response.setAttachments(Arrays.asList(attachment));

                } else {
                    response.setText(scheduleReminder(target, userName, when, actualMessage));
                }
            }

        } catch( Exception e ) {
            response.setText(exceptionText);
        }


        response.setResponseType(channelName.contains(userId) ? Response.ResponseType.IN_CHANNEL : Response.ResponseType.EPHEMERAL);

        return response;
    }

    private String scheduleReminder(String target, String userName, String when, String message) throws Exception {

        Reminder reminder = new Reminder();
        reminder.setTarget(target.equals("me") ? "@"+userName : target);
        reminder.setUserName(userName);
        reminder.setMessage(message);
        reminder.setOccurrence(calculateOccurrence(when));
        reminderRepository.save(reminder);

        return ":thumbsup: I will remind " + (target.equals("me") ? "you" : target) + " \"" + message.trim() + "\" " + when;
    }

    private LocalDateTime calculateOccurrence(String when) throws Exception {

        if ( when.startsWith("in") ) {

            LocalDateTime date;
            String[] timeChunks = when.split(" ");
            if (timeChunks.length != 3)
                throw new Exception("unrecognized time mark.");

            Integer count = Integer.parseInt(timeChunks[1]);
            String chronoUnit = timeChunks[2].toLowerCase();
            switch(chronoUnit) {
                case "seconds":
                    date =  LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS).plusSeconds(count);
                    break;
                case "minutes":
                    date =  LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS).plusMinutes(count);
                    break;
                case "hours":
                    date =  LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS).plusHours(count);
                    break;
                case "days":
                    date =  LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS).plusDays(count);
                    break;
                case "weeks":
                    date =  LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS).plusWeeks(count);
                    break;
                case "months":
                    date =  LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS).plusMonths(count);
                    break;
                case "years":
                    date =  LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS).plusYears(count);
                    break;
                default:
                    throw new Exception("Unrecognized time specification");
            }
            return date;

        } else if ( when.startsWith("at") ) {
            throw new Exception("not yet supported");
        } else if ( when.startsWith("on") ) {
            throw new Exception("not yet supported");
        } else if ( when.startsWith("every") ) {
            throw new Exception("not yet supported");
        } else {
            throw new Exception("unrecognized time mark.");
        }

    }

    private String listReminders(String userName) {

        List<Reminder> reminders = reminderRepository.findByUserName(userName);
        return "*Upcoming*:\n"
                + reminders.stream()
                            .map(r -> "• \""+r.getMessage()+"\" at "
                                    + r.getOccurrence().getHour() + ":"
                                    + r.getOccurrence().getMinute() + " "
                                    + r.getOccurrence().getDayOfWeek().toString().substring(0,1)
                                    + r.getOccurrence().getDayOfWeek().toString().substring(1).toLowerCase() + ", "
                                    + r.getOccurrence().getMonth().toString().substring(0,1)
                                    + r.getOccurrence().getMonth().toString().substring(1).toLowerCase() + " "
                                    + r.getOccurrence().getDayOfMonth() + "\n")
                            .reduce("", String::concat);
    }

    @Scheduled(fixedRate = 1000)
    public void runSchedule() {

        List<Reminder> reminders = reminderRepository.findByOccurrence(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS));
        reminders.forEach( reminder -> {
            logger.info("Sending reminder to {} ", reminder.getTarget());
            try {
                webhook.invoke(reminder.getTarget(), reminder.getMessage());
            } catch (Exception  e) {
                logger.error("Not able to send reminder {}",e);
            }
        });
    }

}
