package me.scottleedavis.mattermostremind.incoming;

import me.scottleedavis.mattermostremind.messages.Response;
import me.scottleedavis.mattermostremind.reminders.Scheduler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.ModelAttribute;


@RestController
public class SlashCommand {

    @Value("${slashCommandToken}")
    String slashCommandToken;

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
            @ModelAttribute("response_url") String responseUrl) throws Exception {

        if (!slashCommandToken.equals(token)) {
            throw new Exception("forbidden");
        }

        return scheduler.setReminder(userName, text, userId, channelName);
    }

}
