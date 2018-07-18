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
            return "*Upcoming*:\n"
                    + reminders.stream()
                    .map(r -> "* \"" + r.getMessage() + "\" at "
                            + formatter.upcomingReminder(r.getOccurrences()))
                    .reduce("", String::concat)+"\n"+
                    "• “touch base  (snoozed until 9AM today) Complete · Delete\n"+
                    "*Recurring*:\n" +
                    "• “https://www.udacity.com/course/deep-learning--ud730” at noon every Friday Delete\n" +
                    "\n" +
                    "*Past and incomplete*:\n" +
                    "• “foo”  Complete · Delete · Snooze: 15 mins · 1 hr · Tomorrow\n" +
                    "\n" +
                    "View completed reminders · Close list";
        }

        return "I cannot find any reminders for you. Type `/remind` to set one.";
    }

    public List<Attachment> listRemindersAttachments(String userName) {
/*
*Upcoming*:
• MESSAGE at TIME DAYOFWEEK, MONTH DAY (Complete · Delete)
• MESSAGE (snoozed until MESSAGE at TIME DAYOFWEEK, MONTH DAY ) Complete · Delete

*Recurring*:
• MESSAGE at TIME EVERY DAYOFWEEK/DATE  Delete

*Past and incomplete*:
• MESSAGE  Complete · Delete · Snooze: 15 mins · 1 hr · Tomorrow

View completed reminders · Close list
 */
        List<Reminder> reminders = reminderService.findByUsername(userName);

        Attachment upcomingHeader = new Attachment();
        upcomingHeader.setText("##### Upcoming");

        List<Attachment> upcoming = reminders.stream().filter(r ->
            !r.isComplete() && r.getOccurrences().get(0).getRepeat() == null &&
            (r.getOccurrences().get(0).getOccurrence().isAfter(LocalDateTime.now()) ||
            (r.getOccurrences().get(0).getSnoozed() != null && r.getOccurrences().get(0).getSnoozed().isAfter(LocalDateTime.now())))
        ).map(r -> {
            Attachment attachment = new Attachment();
            attachment.setActions(listActions(r.getOccurrences().get(0).getId(),false, false));
            attachment.setText(formatter.upcomingReminder(r.getOccurrences()));
            return attachment;
        }).collect(Collectors.toList());
        if( upcoming.size() > 0 )
            upcoming.add(0,upcomingHeader);

        Attachment recurringHeader = new Attachment();
        recurringHeader.setText("##### Recurring");

        List<Attachment> recurring = reminders.stream().filter(r ->
                r.getOccurrences().get(0).getRepeat() != null &&
                r.getOccurrences().get(0).getOccurrence().isAfter(LocalDateTime.now())
        ).map(r -> {
            Attachment attachment = new Attachment();
            attachment.setActions(listActions(r.getOccurrences().get(0).getId(),true, false));
            attachment.setText(formatter.upcomingReminder(r.getOccurrences()));
            return attachment;
        }).collect(Collectors.toList());
        if( recurring.size() > 0 )
            recurring.add(0, recurringHeader);

        Attachment pastIncompleteHeader = new Attachment();
        pastIncompleteHeader.setText("##### Past and incomplete");

        List<Attachment> pastIncomplete = reminders.stream().filter(r ->
                !r.isComplete() && r.getOccurrences().get(0).getRepeat() == null &&
                        r.getOccurrences().get(0).getOccurrence().isBefore(LocalDateTime.now())
        ).map(r -> {
            Attachment attachment = new Attachment();
            attachment.setActions(listActions(r.getOccurrences().get(0).getId(),false, true));
            attachment.setText(formatter.upcomingReminder(r.getOccurrences()));
            return attachment;
        }).collect(Collectors.toList());
        if( pastIncomplete.size() > 0 )
            pastIncomplete.add(0, pastIncompleteHeader);

        Attachment viewCompleted = new Attachment();
        viewCompleted.setActions(Arrays.asList(viewCompleted(-1L)));

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
        integration.setUrl(appUrl + "view");
        Action action = new Action();
        action.setIntegration(integration);
        action.setName(completed ? "View Completed" : "View Reminders");
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
