package scottleedavis.mattermost.remind.reminders;

import scottleedavis.mattermost.remind.jpa.Reminder;
import scottleedavis.mattermost.remind.jpa.ReminderRepository;
import scottleedavis.mattermost.remind.messages.Action;
import scottleedavis.mattermost.remind.messages.Context;
import scottleedavis.mattermost.remind.messages.Integration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;

@Component
public class Options {

    public static String helpMessage = ":wave: Need some help with `/remind`?\n" +
            "Use `/remind` to set a reminder for yourself, someone else, or for a channel. Some examples include:\n" +
            "• `/remind me to drink water at 3pm every day`\n" +
            "• `/remind me on June 1st to wish Linda happy birthday`\n" +
            "• `/remind #team-alpha to update the project status every Monday at 9am`\n" +
            "• `/remind @jessica about the interview in 3 hours`\n" +
            "• `/remind @peter tomorrow \"Please review the office seating plan\"`\n" +
            "Or, use `/remind list` to see the list of all your reminders.";

    public static String exceptionText = "Sorry, I didn’t quite get that. I’m easily confused. " +
            "Perhaps try the words in a different order? This usually works: " +
            "`/remind [@someone or #channel] [what] [when]`.\n";

    private String appUrl;

    @Resource
    ReminderRepository reminderRepository;

    public void setAppUrl(String appUrl) {
        this.appUrl = appUrl;
    }

    public List<Action> setActions(Long id) {
        return Arrays.asList(delete(id), view(id));
    }

    public List<Action> finishedActions(Long id) {
        return Arrays.asList(complete(id), delete(id), snooze(id));
    }

    public String listReminders(String userName) {

        List<Reminder> reminders = reminderRepository.findByUserName(userName);

        if (reminders.size() > 0) {
            return "*Upcoming*:\n"
                    + reminders.stream()
                    .map(r -> "• \"" + r.getMessage() + "\" at "
                            + r.getOccurrence().getHour() + ":"
                            + r.getOccurrence().getMinute() + " "
                            + r.getOccurrence().getDayOfWeek().toString().substring(0, 1)
                            + r.getOccurrence().getDayOfWeek().toString().substring(1).toLowerCase() + ", "
                            + r.getOccurrence().getMonth().toString().substring(0, 1)
                            + r.getOccurrence().getMonth().toString().substring(1).toLowerCase() + " "
                            + r.getOccurrence().getDayOfMonth() + "\n")
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

    private Action snooze(Long id) {
        Context context = new Context();
        context.setAction("snooze");
        context.setId(id);
        Integration integration = new Integration();
        integration.setContext(context);
        integration.setUrl(appUrl + "snooze");
        Action action = new Action();
        action.setIntegration(integration);
        action.setName("Snooze 20 minutes");
        return action;
    }
}
