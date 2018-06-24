package me.scottleedavis.mattermostremind.io;

import org.springframework.beans.factory.annotation.Value;
import com.github.seratch.jslack.*;
import com.github.seratch.jslack.api.webhook.*;
import org.springframework.stereotype.Service;


@Service
public class Webhook {

    @Value("${webhookUrl}")
    private String webhookUrl;

    public void invoke(String target, String message) throws Exception {

        Payload payload = Payload.builder()
                .channel(target)
                .username("mattermost remind")
                .text(message)
                .build();

        Slack slack = Slack.getInstance();
        WebhookResponse response = slack.send(webhookUrl, payload);
    }

}
