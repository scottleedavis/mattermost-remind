package scottleedavis.mattermost.remind.reminders;

import scottleedavis.mattermost.remind.jpa.Reminder;
import scottleedavis.mattermost.remind.io.Webhook;
import scottleedavis.mattermost.remind.jpa.ReminderRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import scottleedavis.mattermost.remind.messages.Attachment;
import scottleedavis.mattermost.remind.messages.ParsedRequest;
import scottleedavis.mattermost.remind.messages.Response;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.List;

@Service
public class Scheduler {

    private static Logger logger = LoggerFactory.getLogger(Scheduler.class);

    @Autowired
    Parser parser;

    @Autowired
    Webhook webhook;

    @Autowired
    Options options;

    @Autowired
    Occurrence occurrence;

    @Autowired
    Formatter formatter;

    @Resource
    ReminderRepository reminderRepository;

    public Response setReminder(String userName, String payload, String userId, String channelName) {

        Response response = new Response();
        try {

            ParsedRequest parsedRequest = parser.extract(payload);
            switch( parsedRequest.getTarget() ) {
                case "help":
                    response.setText(Options.helpMessage);
                    break;
                case "list":
                    response.setText(options.listReminders(userName));
                    break;
                default:
                    Reminder reminder = scheduleReminder(parsedRequest.getTarget(),
                            userName,
                            parsedRequest.getWhen(),
                            parsedRequest.getMessage());
                    String responseText = formatter.setReminder(parsedRequest.getTarget(),
                            parsedRequest.getMessage(),
                            parsedRequest.getWhen());

                    if (channelName.contains(userId)) {
                        Attachment attachment = new Attachment();
                        attachment.setActions(options.setActions(reminder.getId()));
                        attachment.setText(responseText);
                        response.setAttachments(Arrays.asList(attachment));
                    } else {
                        response.setText(responseText);
                    }
            }

        } catch( Exception e ) {
            response.setText(options.exceptionText);
        }

        response.setResponseType(channelName.contains(userId) ? Response.ResponseType.IN_CHANNEL : Response.ResponseType.EPHEMERAL);

        return response;
    }

    private Reminder scheduleReminder(String target, String userName, String when, String message) throws Exception {

        Reminder reminder = new Reminder();
        reminder.setTarget(target.equals("me") ? "@"+userName : target);
        reminder.setUserName(userName);
        reminder.setMessage(message);
        reminder.setOccurrence(occurrence.calculate(when));
        reminderRepository.save(reminder);
        return reminder;
    }

    @Scheduled(fixedRate = 1000)
    public void runSchedule() {

        List<Reminder> reminders = reminderRepository.findByOccurrence(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS));
        reminders.forEach( reminder -> {
            logger.info("Sending reminder {} to {} ", reminder.getId(), reminder.getTarget());
            try {
                webhook.invoke(reminder.getTarget(), reminder.getMessage(), reminder.getId());
            } catch (Exception  e) {
                logger.error("Not able to send reminder {}",e);
            }
        });
    }

}
