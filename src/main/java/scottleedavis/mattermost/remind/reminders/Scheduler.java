package scottleedavis.mattermost.remind.reminders;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import scottleedavis.mattermost.remind.db.ReminderService;
import scottleedavis.mattermost.remind.io.Webhook;
import scottleedavis.mattermost.remind.db.Reminder;
import scottleedavis.mattermost.remind.db.ReminderRepository;
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

    @Value("${version}")
    private String version;

    @Autowired
    private Parser parser;

    @Autowired
    private Webhook webhook;

    @Autowired
    private Options options;

    @Autowired
    private Occurrence occurrence;

    @Autowired
    private Formatter formatter;

    @Autowired
    private ReminderService reminderService;

    @Resource
    private ReminderRepository reminderRepository;

    public Response setReminder(String userName, String payload, String userId, String channelName) {

        Response response = new Response();
        try {

            ParsedRequest parsedRequest = parser.extract(payload);
            switch (parsedRequest.getTarget()) {
                case "help":
                    response.setText(Options.helpMessage);
                    break;
                case "list":
                    response.setText(options.listReminders(userName));
                    break;
                case "version":
                    response.setText(version);
                    break;
                default:
                    Reminder reminder = reminderService.schedule(userName, parsedRequest);
                    String responseText = formatter.reminderResponse(parsedRequest);
                    if (channelName.contains(userId)) {
                        Attachment attachment = new Attachment();
                        attachment.setActions(options.setActions(reminder.getId()));
                        attachment.setText(responseText);
                        response.setAttachments(Arrays.asList(attachment));
                    } else {
                        response.setText(responseText);
                    }
            }

        } catch (Exception e) {
            response.setText(options.exceptionText);
        }

        response.setResponseType(channelName.contains(userId) ? Response.ResponseType.IN_CHANNEL : Response.ResponseType.EPHEMERAL);

        return response;
    }

    @Scheduled(fixedRate = 1000)
    public void runSchedule() {

        //TODO FIX THIS
//        List<Reminder> reminders = reminderRepository.findByOccurrence(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS));
//        reminders.forEach(reminder -> {
//            logger.info("Sending reminder {} to {} ", reminder.getId(), reminder.getTarget());
//            try {
//                webhook.invoke(reminder.getTarget(), reminder.getMessage(), reminder.getId());
//            } catch (Exception e) {
//                logger.error("Not able to send reminder {}", e);
//            }
//        });
    }

}
