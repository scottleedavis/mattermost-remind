package scottleedavis.mattermost.remind.io;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import scottleedavis.mattermost.remind.exceptions.TokenException;
import scottleedavis.mattermost.remind.messages.Response;
import scottleedavis.mattermost.remind.reminders.Options;
import scottleedavis.mattermost.remind.reminders.Scheduler;


@RestController
public class SlashCommand {

    @Value("${slashCommandToken}")
    String slashCommandToken;

    @Autowired
    Options options;

    @Autowired
    Scheduler scheduler;

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
