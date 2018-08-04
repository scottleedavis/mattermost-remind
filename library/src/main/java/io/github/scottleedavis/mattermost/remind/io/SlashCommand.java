package io.github.scottleedavis.mattermost.remind.io;

import io.github.scottleedavis.mattermost.remind.exceptions.TokenException;
import io.github.scottleedavis.mattermost.remind.messages.Response;
import io.github.scottleedavis.mattermost.remind.reminders.Options;
import io.github.scottleedavis.mattermost.remind.reminders.Scheduler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController(value = "RemindSlashCommand")
public class SlashCommand {

    public static Logger logger = LoggerFactory.getLogger(SlashCommand.class);

    private String slashCommandToken;

    @Autowired
    private Options options;

    @Autowired
    private Scheduler scheduler;

    @Autowired
    public SlashCommand(@Value("${remind.SlashCommandToken}") String slashCommandToken) {
        if (slashCommandToken == null)
            slashCommandToken = "none";
        this.slashCommandToken = slashCommandToken;
        logger.info("remind.SlashCommandToken = {}", slashCommandToken);
    }

    @RequestMapping(value = "/remind", produces = "application/json")
    public Response remind(
            @ModelAttribute("token") String token,
            @ModelAttribute("team_id") String teamId,
            @ModelAttribute("team_domain") String teamDomain,
            @ModelAttribute("channel_id") String channelId,
            @ModelAttribute("channel_name") String channelName,
            @ModelAttribute("user_id") String userId,
            @ModelAttribute("user_name") String userName,
            @ModelAttribute("command") String command,
            @ModelAttribute("text") String text,
            @ModelAttribute("response_url") String responseUrl,
            @RequestHeader(value = "X-Forwarded-Proto", required = false) String scheme,
            @RequestHeader("Host") String host,
            @ModelAttribute("stage") String stage) throws Exception {

        if (!slashCommandToken.equals(token)) {
            throw new TokenException("token doesn't match");
        }

        options.setAppUrl((scheme == null ? "http" : scheme) + "://" + host + "/" + stage);
        return scheduler.setReminder(userName, text, userId, channelName);
    }

}
