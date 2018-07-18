package io.github.scottleedavis.mattermost.remind.reminders;

import io.github.scottleedavis.mattermost.remind.db.Reminder;
import io.github.scottleedavis.mattermost.remind.db.ReminderOccurrence;
import io.github.scottleedavis.mattermost.remind.db.ReminderService;
import io.github.scottleedavis.mattermost.remind.messages.Action;
import io.github.scottleedavis.mattermost.remind.messages.Attachment;
import io.github.scottleedavis.mattermost.remind.messages.Context;
import io.github.scottleedavis.mattermost.remind.messages.Integration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
        return Arrays.asList(delete(id), viewAll(id));
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

    public List<Action> listActions(Long id, boolean isRepeated, boolean isPastIncomplete) {

        if (isRepeated)
            return Arrays.asList(delete(id));

        if (isPastIncomplete)
            return Arrays.asList(
                    complete(id),
                    delete(id),
                    snooze(id, ArgumentType.TWENTY_MINUTES),
                    snooze(id, ArgumentType.ONE_HOUR),
                    snooze(id, ArgumentType.TOMORROW_AT_9AM));

        return Arrays.asList(
                complete(id),
                delete(id));
    }

    public String listReminders(String userName) {

        List<Reminder> reminders = reminderService.findByUsername(userName);

        if (reminders.size() > 0) {
            String reminderOutput = "";
            List<String> upcoming = reminders.stream().filter(r ->
                    !r.isComplete() && r.getOccurrences().get(0).getRepeat() == null &&
                            (r.getOccurrences().get(0).getOccurrence().isAfter(LocalDateTime.now()) ||
                                    (r.getOccurrences().get(0).getSnoozed() != null &&
                                            r.getOccurrences().get(0).getSnoozed().isAfter(LocalDateTime.now())))
            ).map(r -> "* " + formatter.upcomingReminder(r.getOccurrences()) + "\n").collect(Collectors.toList());
            if (upcoming.size() > 0)
                reminderOutput += "*Upcoming*:\n" +
                        upcoming.stream().reduce("", String::concat) + "\n";
            List<String> recurring = reminders.stream().filter(r ->
                    r.getOccurrences().get(0).getRepeat() != null &&
                            r.getOccurrences().get(0).getOccurrence().isAfter(LocalDateTime.now())
            ).map(r -> "* " + formatter.upcomingReminder(r.getOccurrences()) + "\n").collect(Collectors.toList());
            if (recurring.size() > 0)
                reminderOutput += "*Recurring*:\n" +
                        recurring.stream().reduce("", String::concat) + "\n";
            List<String> pastIncomplete = reminders.stream().filter(r ->
                    !r.isComplete() && r.getOccurrences().get(0).getRepeat() == null &&
                            r.getOccurrences().get(0).getOccurrence().isBefore(LocalDateTime.now())
            ).map(r -> "\"" + r.getMessage() + "\"").collect(Collectors.toList());
            if (pastIncomplete.size() > 0)
                reminderOutput += "*Past and incomplete*:\n" +
                        pastIncomplete.stream().reduce("", String::concat) + "\n";
            return reminderOutput + "*Note*:  To interact with these reminders use `/remind list` in your personal user channel";
        }

        return "I cannot find any reminders for you. Type `/remind` to set one.";
    }

    public String listComplete(String userName) {
        List<Reminder> reminders = reminderService.findByUsername(userName);

        //TODO  - likely have a 'delete all completed' button
        //TODO format the completed as follows
        /*
        *Completed*: Delete all completed reminders
        • “https://docs.spring.io/spring-boot/docs/current/reference/htmlsingle/#build-tool-plugins-maven-packaging”  (completed at 2:22PM Sunday, June 24th) Delete
        • “https://springframework.guru/configuring-spring-boot-for-microsoft-sql-server/”  (completed at 2:38PM Sunday, June 24th) Delete
        • “lalala”  (completed at 2:50PM Sunday, June 24th) Delete
        • “lalala”  (completed at 2:54PM Sunday, June 24th) Delete
        • “foo”  (completed at 4:22PM Sunday, June 24th) Delete
        • “do the chicken dance”  (completed at 9PM Sunday, June 24th) Delete
        • “foo”  (completed at 9PM Sunday, June 24th) Delete
        • “foo”  (completed at 7:10PM Monday, June 25th) Delete
        • “foo”  (completed at 7:10PM Monday, June 25th) Delete
        • “aloha”  (completed at 7:10PM Monday, June 25th) Delete
         */
        if (reminders.size() > 0) {
            String reminderOutput = "";
            List<String> complete = reminders.stream().filter(r -> r.isComplete())
                    .map(r -> "\"" + r.getMessage() + "\"").collect(Collectors.toList());
            if (complete.size() > 0)
                reminderOutput += "*Complete*:\n" +
                        complete.stream().reduce("", String::concat) + "\n";
            return reminderOutput;
        }

        return "I cannot find any reminders for you. Type `/remind` to set one.";
    }

    public List<Attachment> listRemindersAttachments(String userName) {

        List<Reminder> reminders = reminderService.findByUsername(userName);

        List<Attachment> upcoming = reminders.stream().filter(r ->
                !r.isComplete() && r.getOccurrences().get(0).getRepeat() == null &&
                        (r.getOccurrences().get(0).getOccurrence().isAfter(LocalDateTime.now()) ||
                                (r.getOccurrences().get(0).getSnoozed() != null &&
                                        r.getOccurrences().get(0).getSnoozed().isAfter(LocalDateTime.now())))
        ).map(r -> {
            Attachment attachment = new Attachment();
            attachment.setActions(listActions(r.getOccurrences().get(0).getId(), false, false));
            attachment.setText("**Upcoming** " + formatter.upcomingReminder(r.getOccurrences()));
            return attachment;
        }).collect(Collectors.toList());

        List<Attachment> recurring = reminders.stream().filter(r ->
                r.getOccurrences().get(0).getRepeat() != null &&
                        r.getOccurrences().get(0).getOccurrence().isAfter(LocalDateTime.now())
        ).map(r -> {
            Attachment attachment = new Attachment();
            attachment.setActions(listActions(r.getOccurrences().get(0).getId(), true, false));
            attachment.setText("**Recurring** " + formatter.upcomingReminder(r.getOccurrences()));
            return attachment;
        }).collect(Collectors.toList());

        List<Attachment> pastIncomplete = reminders.stream().filter(r ->
                !r.isComplete() && r.getOccurrences().get(0).getRepeat() == null &&
                        r.getOccurrences().get(0).getOccurrence().isBefore(LocalDateTime.now())
        ).map(r -> {
            Attachment attachment = new Attachment();
            attachment.setActions(listActions(r.getOccurrences().get(0).getId(), false, true));
            attachment.setText("**Past and incomplete** \"" + r.getMessage() + "\"");
            return attachment;
        }).collect(Collectors.toList());

        Attachment viewCompleted = new Attachment();
        if (reminders.size() > 0) {
            ReminderOccurrence reminderOccurrence = reminders.get(0).getOccurrences().get(0);
            viewCompleted.setActions(Arrays.asList(viewCompleted(reminderOccurrence.getId()), close()));
        }

        return Stream.of(
                upcoming,
                recurring,
                pastIncomplete,
                Arrays.asList(viewCompleted)
        ).flatMap(Collection::stream).collect(Collectors.toList());

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

    private Action viewAll(Long id) {
        return view(id, false);
    }

    private Action viewCompleted(Long id) {
        return view(id, true);
    }

    private Action view(Long id, boolean completed) {
        Context context = new Context();
        context.setAction("view");
        context.setId(id);
        Integration integration = new Integration();
        integration.setContext(context);
        integration.setUrl(appUrl + (completed ? "view/complete" : "view"));
        Action action = new Action();
        action.setIntegration(integration);
        action.setName(completed ? "View Completed Reminders" : "View Reminders");
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

    private Action close() {
        Context context = new Context();
        context.setAction("close");
        Integration integration = new Integration();
        integration.setContext(context);
        integration.setUrl(appUrl + "close");
        Action action = new Action();
        action.setIntegration(integration);
        action.setName("Close list");
        return action;
    }
}
