package io.github.scottleedavis.mattermost.remind.io;

import io.github.scottleedavis.mattermost.remind.db.ReminderOccurrence;
import io.github.scottleedavis.mattermost.remind.messages.Attachment;
import io.github.scottleedavis.mattermost.remind.messages.Response;
import io.github.scottleedavis.mattermost.remind.reminders.Options;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;


@Service
public class Webhook {

    private static Logger logger = LoggerFactory.getLogger(Webhook.class);

    @Value("${remind.webhookUrl}")
    private String webhookUrl;

    @Autowired
    private Options options;

    public void invoke(ReminderOccurrence reminderOccurrence) throws Exception {

        Response response = new Response();
        response.setChannel(reminderOccurrence.getReminder().getTarget());
        response.setUsername("mattermost-remind");
        response.setResponseType(Response.ResponseType.EPHEMERAL);
        Attachment attachment = new Attachment();
        attachment.setActions(options.finishedActions(reminderOccurrence.getId(), reminderOccurrence.getRepeat() != null));
        attachment.setText("You asked me to remind you \"" + reminderOccurrence.getReminder().getMessage() + "\".");
        response.setAttachments(Arrays.asList(attachment));

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity entity = new HttpEntity(response, headers);
        ResponseEntity<String> out = restTemplate.exchange(webhookUrl, HttpMethod.POST, entity, String.class);
        logger.info(out.toString());

    }

}
