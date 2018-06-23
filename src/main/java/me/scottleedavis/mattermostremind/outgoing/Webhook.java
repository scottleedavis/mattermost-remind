package me.scottleedavis.mattermostremind.outgoing;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import com.github.seratch.jslack.*;
import com.github.seratch.jslack.api.webhook.*;

import javax.annotation.PostConstruct;

@Component
public class Webhook {

    private static final Logger logger = LoggerFactory.getLogger(Webhook.class);

    @Value("${mattermostIncomingWebhookUrl}")
    private String mattermostIncomingWebhookUrl;

    @PostConstruct
    public void invokeSlackWebhook() throws Exception {

        String url = mattermostIncomingWebhookUrl;

        Payload payload = Payload.builder()
                .channel("@scottleedavi")
                .username("jSlack Bot")
                .iconEmoji(":smile_cat:")
                .text("Hello World!")
                .build();

        Slack slack = Slack.getInstance();
        WebhookResponse response = slack.send(url, payload);

//        RestTemplate restTemplate = new RestTemplate();
//        RichMessage richMessage = new RichMessage("Just to test Slack's incoming webhooks.");
//        // set attachments
//        Attachment[] attachments = new Attachment[1];
//        attachments[0] = new Attachment();
//        attachments[0].setText("Some data relevant to your users.");
//        richMessage.setAttachments(attachments);2Q2w                          #33333333333333 3
//
//        // For debugging purpose only
//        try {
//            logger.debug("Reply (RichMessage): {}", new ObjectMapper().writeValueAsString(richMessage));
//        } catch (JsonProcessingException e) {
//            logger.debug("Error parsing RichMessage: ", e);
//        }
//
//        // Always remember to send the encoded message to Slack
//        try {
//            restTemplate.postForEntity(mattermostIncomingWebhookUrl, richMessage.encodedMessage(), String.class);
//        } catch (RestClientException e) {
//            logger.error("Error posting to Slack Incoming Webhook: ", e);
//        }
    }

}
