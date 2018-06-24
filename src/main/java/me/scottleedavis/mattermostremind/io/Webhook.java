package me.scottleedavis.mattermostremind.io;

import me.scottleedavis.mattermostremind.messages.Attachment;
import me.scottleedavis.mattermostremind.messages.Response;
import me.scottleedavis.mattermostremind.reminders.ReminderOptions;
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

    @Value("${webhookUrl}")
    private String webhookUrl;

    @Autowired
    ReminderOptions reminderOptions;

    public void invoke(String target, String message) throws Exception {

        Response response = new Response();
        response.setChannel(target);
        response.setUsername("mattermost-remind");
        response.setResponseType(Response.ResponseType.EPHEMERAL);
        Attachment attachment = new Attachment();
        attachment.setActions(reminderOptions.finishedActions());
        attachment.setText("You asked me to remind you \""+message+"\".");
        response.setAttachments(Arrays.asList(attachment));

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity entity = new HttpEntity(response,headers);
        ResponseEntity<String> out = restTemplate.exchange(webhookUrl, HttpMethod.POST, entity, String.class);
        logger.info(out.toString());

    }

}
