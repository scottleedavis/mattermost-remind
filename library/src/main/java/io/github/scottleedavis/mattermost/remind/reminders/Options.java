package io.github.scottleedavis.mattermost.remind.reminders;

import io.github.scottleedavis.mattermost.remind.db.Reminder;
import io.github.scottleedavis.mattermost.remind.db.ReminderService;
import io.github.scottleedavis.mattermost.remind.messages.Action;
import io.github.scottleedavis.mattermost.remind.messages.Context;
import io.github.scottleedavis.mattermost.remind.messages.Integration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component(value = "remind")
public class Options {

    public static String helpMessage = ":wave: Need some help with `/remind`?\n" +
            "Use `/remind` to set a reminder for yourself, someone else, or for a channel. Some examples include:\n" +
            "* `/remind me to drink water at 3pm every day`\n" +
            "* `/remind me on June 1st to wish Linda happy birthday`\n" +
            "* `/remind ~team-alpha to update the project status every Monday at 9am`\n" +
            "* `/remind @jessica about the interview in 3 hours`\n" +
            "* `/remind @peter tomorrow \"Please review the office seating plan\"`\n" +
            "Or, use `/remind list` to see the list of all your reminders.\n" +
            "Have a bug to report or a feature request?  [Submit your issue here](https://gitreports.com/issue/scottleedavis/mattermost-remind/).";

    public static String exceptionText = "Sorry, I didn’t quite get that. I’m easily confused. " +
            "Perhaps try the words in a different order? This usually works: " +
            "`/remind [@someone or ~channel] [what] [when]`.\n";

    public static String noUserRepeatText = "Sorry, you can't recurring reminders for other users.";

    private String appUrl;

    @Autowired
    private ReminderService reminderService;

    @Autowired
    private Formatter formatter;

    public void setAppUrl(String appUrl) {
        this.appUrl = appUrl;
    }

    public List<Action> setActions(Long id) {
        return Arrays.asList(delete(id), view(id));
    }

    public List<Action> finishedActions(Long id, boolean isRepeated, boolean isChannel) {
        if (isRepeated)
            return Arrays.asList(
                    snooze(id, ArgumentType.TWENTY_MINUTES),
                    snooze(id, ArgumentType.ONE_HOUR),
                    snooze(id, ArgumentType.THREE_HOURS),
                    snooze(id, ArgumentType.TOMORROW_AT_9AM),
                    snooze(id, ArgumentType.NEXT_WEEK));

        if (isChannel)
            return Arrays.asList(complete(id),
                    delete(id));

        return Arrays.asList(complete(id),
                delete(id),
                snooze(id, ArgumentType.TWENTY_MINUTES),
                snooze(id, ArgumentType.ONE_HOUR),
                snooze(id, ArgumentType.THREE_HOURS),
                snooze(id, ArgumentType.TOMORROW_AT_9AM),
                snooze(id, ArgumentType.NEXT_WEEK));
    }

    public String listReminders(String userName) {

        List<Reminder> reminders = reminderService.findByUsername(userName);

        if (reminders.size() > 0) {
            return "*Upcoming*:\n"
                    + reminders.stream()
                    .map(r -> "* \"" + r.getMessage() + "\" at "
                            + formatter.upcomingReminder(r.getOccurrences()))
                    .reduce("", String::concat);
        }

        return "I cannot find any reminders for you. Type `/remind` to set one.";
    }

    private Action delete(Long id) {
        Context context = new Context();
        context.setAction("delete");
        context.setId(id);
        Integration integration = new Integration();
        integration.setContext(context);
        integration.setUrl(appUrl + "delete");
        Action action = new Action();
        action.setIntegration(integration);
        action.setName("Delete");
        return action;
    }

    private Action view(Long id) {
        Context context = new Context();
        context.setAction("view");
        context.setId(id);
        Integration integration = new Integration();
        integration.setContext(context);
        integration.setUrl(appUrl + "view");
        Action action = new Action();
        action.setIntegration(integration);
        action.setName("View Reminders");
        return action;
    }

    private Action complete(Long id) {
        Context context = new Context();
        context.setAction("complete");
        context.setId(id);
        Integration integration = new Integration();
        integration.setContext(context);
        integration.setUrl(appUrl + "complete");
        Action action = new Action();
        action.setIntegration(integration);
        action.setName("Mark as Complete");
        return action;
    }

    private Action snooze(Long id, String argument) {
        Context context = new Context();
        context.setAction("snooze");
        context.setArgument(argument);
        context.setId(id);
        Integration integration = new Integration();
        integration.setContext(context);
        integration.setUrl(appUrl + "snooze");
        Action action = new Action();
        action.setIntegration(integration);
        action.setName("Snooze " + argument);
        return action;
    }
}
