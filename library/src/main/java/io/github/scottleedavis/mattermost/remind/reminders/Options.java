package io.github.scottleedavis.mattermost.remind.reminders;

import io.github.scottleedavis.mattermost.remind.db.Reminder;
import io.github.scottleedavis.mattermost.remind.db.ReminderOccurrence;
import io.github.scottleedavis.mattermost.remind.db.ReminderService;
import io.github.scottleedavis.mattermost.remind.messages.Action;
import io.github.scottleedavis.mattermost.remind.messages.Attachment;
import io.github.scottleedavis.mattermost.remind.messages.Context;
import io.github.scottleedavis.mattermost.remind.messages.Integration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component(value = "remind")
public class Options {

    private static Logger logger = LoggerFactory.getLogger(Options.class);

    public static Integer remindListMaxLength = 3;

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

    public static String noReminderList = "I cannot find any reminders for you. Type `/remind` to set one.";

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

    public List<Action> listActions(Long id, String userName, boolean isRepeated, boolean isPastIncomplete) {

        if (isRepeated)
            return Arrays.asList(delete(id, userName));

        if (isPastIncomplete)
            return Arrays.asList(
                    complete(id, userName),
                    delete(id, userName),
                    snooze(id, userName, ArgumentType.TWENTY_MINUTES),
                    snooze(id, userName, ArgumentType.ONE_HOUR),
                    snooze(id, userName, ArgumentType.TOMORROW_AT_9AM));

        return Arrays.asList(
                complete(id, userName),
                delete(id, userName));
    }

    public List<Action> pagedActions(String userName, Integer firstIndex, Integer lastIndex, Integer size) {
        if (firstIndex > 0 && lastIndex < size - 1)
            return Arrays.asList(previous(userName, firstIndex, lastIndex), next(userName, firstIndex, lastIndex), close());

        if (firstIndex > 0 && firstIndex > remindListMaxLength - 1 && lastIndex == size - 1)
            return Arrays.asList(previous(userName, firstIndex, lastIndex), close());
        if (firstIndex.equals(0) && lastIndex.equals(size - 1) && size.equals(remindListMaxLength))
            return Arrays.asList(close());
        return Arrays.asList(next(userName, firstIndex, lastIndex), close());
    }

    public String listComplete(String userName) {
        List<Reminder> reminders = reminderService.findByUsername(userName);

        if (reminders.size() > 0) {
            List<String> complete = reminders.stream().filter(r -> r.getCompleted() != null)
                    .map(r -> formatter.completedReminder(r.getOccurrences())).collect(Collectors.toList());
            if (complete.size() > 0)
                return "*Complete*:\n" +
                        complete.stream().reduce("", String::concat) + "\n";
        }

        return noReminderList;
    }

    public String listReminders(String userName, String channelName) {

        List<Reminder> reminders = reminderService.findByUsername(userName);

        if (reminders.size() > 0) {
            String reminderOutput = "";
            List<String> upcoming = reminders.stream().filter(r ->
                    (r.getCompleted() == null) &&
                            r.getOccurrences().get(0).getRepeat() == null &&
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
                    (r.getCompleted() == null) && r.getOccurrences().get(0).getRepeat() == null &&
                            r.getOccurrences().get(0).getOccurrence().isBefore(LocalDateTime.now()) &&
                            r.getOccurrences().get(0).getSnoozed() == null
            ).map(r -> "* \"" + r.getMessage() + "\"\n").collect(Collectors.toList());
            if (pastIncomplete.size() > 0)
                reminderOutput += "*Past and incomplete*:\n" +
                        pastIncomplete.stream().reduce("", String::concat) + "\n";

            if (channelName != null) {
                List<String> channelReminders = reminderService.findByTarget("~" + channelName).stream().filter(r ->
                        (r.getCompleted() == null)
                ).map(r -> "* " + formatter.upcomingReminder(r.getOccurrences()) + "\n").collect(Collectors.toList());
                if (channelReminders.size() > 0)
                    reminderOutput += "*Channel*:\n" +
                            channelReminders.stream().reduce("", String::concat) + "\n";
            }

            return reminderOutput + "*Note*:  To interact with these reminders use `/remind list` in a direct message with user mattermost-remind";
        }

        return noReminderList;
    }

    public List<Attachment> listRemindersAttachments(String userName, Integer firstIndex) {

        List<Reminder> reminders = reminderService.findByUsername(userName);
        List<Reminder> remindersNotComplete = reminders.stream().filter(r -> r.getCompleted() == null).collect(Collectors.toList());
        List<Reminder> remindersFiltered = remindersNotComplete;

        Integer lastIndex = firstIndex + remindListMaxLength - 1;
        if (remindersFiltered.size() > (lastIndex)) {
            remindersFiltered = remindersFiltered.subList(firstIndex, lastIndex + 1);
        } else if (remindersFiltered.size() > 0) {
            lastIndex = remindersFiltered.size() - 1;
            remindersFiltered = remindersFiltered.subList(firstIndex, lastIndex + 1);
        }

        logger.info("firstIndex {}, lastIndex {}, size {}", firstIndex, lastIndex, remindersFiltered.size());

        List<Attachment> upcoming = remindersFiltered.stream().filter(r ->
                (r.getCompleted() == null) && r.getOccurrences().get(0).getRepeat() == null &&
                        (r.getOccurrences().get(0).getOccurrence().isAfter(LocalDateTime.now()) ||
                                (r.getOccurrences().get(0).getSnoozed() != null &&
                                        r.getOccurrences().get(0).getSnoozed().isAfter(LocalDateTime.now())))
        ).map(r -> {
            Attachment attachment = new Attachment();
            attachment.setActions(listActions(r.getOccurrences().get(0).getId(), r.getUserName(), false, false));
            attachment.setText("**Upcoming** " + formatter.upcomingReminder(r.getOccurrences()));
            return attachment;
        }).collect(Collectors.toList());

        List<Attachment> recurring = remindersFiltered.stream().filter(r ->
                r.getOccurrences().get(0).getRepeat() != null &&
                        r.getOccurrences().get(0).getOccurrence().isAfter(LocalDateTime.now())
        ).map(r -> {
            Attachment attachment = new Attachment();
            attachment.setActions(listActions(r.getOccurrences().get(0).getId(), r.getUserName(), true, false));
            attachment.setText("**Recurring** " + formatter.upcomingReminder(r.getOccurrences()));
            return attachment;
        }).collect(Collectors.toList());

        List<Attachment> pastIncomplete = remindersFiltered.stream().filter(r ->
                (r.getCompleted() == null) && r.getOccurrences().get(0).getRepeat() == null &&
                        r.getOccurrences().get(0).getOccurrence().isBefore(LocalDateTime.now()) &&
                        r.getOccurrences().get(0).getSnoozed() == null
        ).map(r -> {
            Attachment attachment = new Attachment();
            attachment.setActions(listActions(r.getOccurrences().get(0).getId(), r.getUserName(), false, true));
            attachment.setText("**Past and incomplete** \"" + r.getMessage() + "\"");
            return attachment;
        }).collect(Collectors.toList());

        List<Attachment> pageList = new ArrayList<>();
        if (remindersNotComplete.size() >= remindListMaxLength) {
            Attachment paged = new Attachment();
            paged.setActions(pagedActions(remindersFiltered.get(0).getUserName(), firstIndex, lastIndex, remindersNotComplete.size()));
            paged.setText("Reminders " + (firstIndex + 1) + " to " + (lastIndex + 1) + " (of " + remindersNotComplete.size() + " total)");
            pageList.add(paged);
        }

        List<Attachment> completed = new ArrayList<>();
        if (remindersFiltered.size() > 0) {
            Attachment viewCompleted = new Attachment();
            ReminderOccurrence reminderOccurrence = reminders.get(0).getOccurrences().get(0);
            viewCompleted.setActions(Arrays.asList(
                    viewCompleted(reminderOccurrence.getId()),
                    deleteAllCompleted(reminderOccurrence.getId()),
                    close()));
            completed.add(viewCompleted);
        }

        List<Attachment> noList = new ArrayList<>();
        if (upcoming.size() == 0 &&
                recurring.size() == 0 &&
                pastIncomplete.size() == 0 &&
                completed.size() == 0) {
            Attachment noListReminders = new Attachment();
            noListReminders.setText(noReminderList);
            noList.add(noListReminders);
        }

        return Stream.of(
                upcoming,
                recurring,
                pastIncomplete,
                completed,
                noList,
                pageList
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

    private Action delete(Long id, String userName) {
        Context context = new Context();
        context.setAction("delete");
        context.setId(id);
        context.setUserName(userName);
        Integration integration = new Integration();
        integration.setContext(context);
        integration.setUrl(appUrl + "delete");
        Action action = new Action();
        action.setIntegration(integration);
        action.setName("Delete");
        return action;
    }

    private Action deleteAllCompleted(Long id) {
        Context context = new Context();
        context.setAction("deleteCompleted");
        context.setId(id);
        Integration integration = new Integration();
        integration.setContext(context);
        integration.setUrl(appUrl + "delete/completed");
        Action action = new Action();
        action.setIntegration(integration);
        action.setName("Delete all completed");
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

    private Action complete(Long id, String userName) {
        Context context = new Context();
        context.setAction("complete");
        context.setUserName(userName);
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

    private Action snooze(Long id, String userName, String argument) {
        Context context = new Context();
        context.setAction("snooze");
        context.setArgument(argument);
        context.setId(id);
        context.setUserName(userName);
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

    private Action previous(String userName, Integer firstIndex, Integer lastIndex) {
        Context context = new Context();
        context.setAction("previous");
        context.setUserName(userName);
        context.setFirstIndex(firstIndex);
        context.setLastIndex(lastIndex);
        Integration integration = new Integration();
        integration.setContext(context);
        integration.setUrl(appUrl + "previous");
        Action action = new Action();
        action.setIntegration(integration);
        Integer indexDifference = lastIndex - firstIndex;
        if (indexDifference < remindListMaxLength) {
            indexDifference -= remindListMaxLength;
            if (indexDifference < 0)
                indexDifference = remindListMaxLength;
        } else
            indexDifference += 1;
        action.setName("Previous " + indexDifference + " reminders");
        return action;
    }

    private Action next(String userName, Integer firstIndex, Integer lastIndex) {
        Context context = new Context();
        context.setAction("next");
        context.setUserName(userName);
        context.setFirstIndex(firstIndex);
        context.setLastIndex(lastIndex);
        Integration integration = new Integration();
        integration.setContext(context);
        integration.setUrl(appUrl + "next");
        Action action = new Action();
        action.setIntegration(integration);
        action.setName("Next " + (lastIndex - firstIndex + 1) + " reminders");
        return action;
    }
}
