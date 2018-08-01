package io.github.scottleedavis.mattermost.remind.reminders;

import io.github.scottleedavis.mattermost.remind.db.Reminder;
import io.github.scottleedavis.mattermost.remind.db.ReminderOccurrence;
import io.github.scottleedavis.mattermost.remind.db.ReminderService;
import io.github.scottleedavis.mattermost.remind.io.Webhook;
import io.github.scottleedavis.mattermost.remind.messages.Interaction;
import io.github.scottleedavis.mattermost.remind.messages.Update;
import io.github.scottleedavis.mattermost.remind.messages.UpdateResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;

@Service
@Transactional
public class Updates {

    @Autowired
    private Options options;

    @Autowired
    private ReminderService reminderService;

    @Autowired
    private Webhook webhook;

    public UpdateResponse close() {
        UpdateResponse updateResponse = new UpdateResponse();
        Update update = new Update();
        update.setMessage("");
        updateResponse.setUpdate(update);
        return updateResponse;
    }

    public UpdateResponse delete(Interaction interaction) throws Exception {
        Reminder reminder = reminderService.findByInteraction(interaction).getReminder();
        UpdateResponse updateResponse = new UpdateResponse();
        Update update = new Update();
        update.setMessage("Ok! I’ve deleted the reminder “" + reminder.getMessage() + "”.");
        updateResponse.setUpdate(update);
        reminderService.delete(reminder);
        return updateResponse;
    }

    public UpdateResponse deleteCompleted(Interaction interaction) throws Exception {
        Reminder reminder = reminderService.findByInteraction(interaction).getReminder();
        UpdateResponse updateResponse = new UpdateResponse();
        Update update = new Update();
        update.setMessage("Ok! I’ve deleted all completed reminders.");
        updateResponse.setUpdate(update);
        reminderService.deleteCompleted(reminder.getUserName());
        return updateResponse;
    }

    public UpdateResponse view(Interaction interaction) throws Exception {
        Reminder reminder = reminderService.findByInteraction(interaction).getReminder();
        UpdateResponse updateResponse = new UpdateResponse();
        updateResponse.setEphemeralText(options.listReminders(reminder.getUserName()));
        return updateResponse;
    }

    public UpdateResponse viewComplete(Interaction interaction) throws Exception {
        Reminder reminder = reminderService.findByInteraction(interaction).getReminder();
        UpdateResponse updateResponse = new UpdateResponse();
        updateResponse.setEphemeralText(options.listComplete(reminder.getUserName()));
        return updateResponse;
    }

    public UpdateResponse complete(Interaction interaction) throws Exception {
        Reminder reminder = reminderService.findByInteraction(interaction).getReminder();
        UpdateResponse updateResponse = new UpdateResponse();
        Update update = new Update();
        update.setMessage("Ok! I’ve marked the reminder  “" + reminder.getMessage() + "” as complete.");
        updateResponse.setUpdate(update);
        reminderService.complete(reminder);
        return updateResponse;
    }

    public UpdateResponse snooze(Interaction interaction) throws Exception {
        ReminderOccurrence reminderOccurrence = reminderService.findByInteraction(interaction);
        Reminder reminder = reminderOccurrence.getReminder();
        UpdateResponse updateResponse = new UpdateResponse();
        Update update = new Update();
        switch (interaction.getContext().getArgument()) {
            case ArgumentType.TWENTY_MINUTES:
            case ArgumentType.ONE_HOUR:
            case ArgumentType.THREE_HOURS:
                update.setMessage("Ok! I’ll remind you “" + reminder.getMessage() + "” in " + interaction.getContext().getArgument());
                break;
            case ArgumentType.TOMORROW_AT_9AM:
            case ArgumentType.NEXT_WEEK:
                update.setMessage("Ok! I’ll remind you “" + reminder.getMessage() + "” " + interaction.getContext().getArgument());
                break;
            default:
                update.setMessage("Whoops!   Something went wrong.");
                break;
        }
        updateResponse.setUpdate(update);
        switch (interaction.getContext().getArgument()) {
            case ArgumentType.TWENTY_MINUTES:
                reminderService.snooze(reminderOccurrence, LocalDateTime.now().plusMinutes(20).truncatedTo(ChronoUnit.SECONDS));
                break;
            case ArgumentType.ONE_HOUR:
                reminderService.snooze(reminderOccurrence, LocalDateTime.now().plusHours(1).truncatedTo(ChronoUnit.SECONDS));
                break;
            case ArgumentType.THREE_HOURS:
                reminderService.snooze(reminderOccurrence, LocalDateTime.now().plusHours(3).truncatedTo(ChronoUnit.SECONDS));
                break;
            case ArgumentType.TOMORROW_AT_9AM:
                reminderService.snooze(reminderOccurrence, LocalDate.now().plusDays(1).atTime(9, 0).truncatedTo(ChronoUnit.SECONDS));
                break;
            case ArgumentType.NEXT_WEEK:
                reminderService.snooze(reminderOccurrence, LocalDate.now().with(TemporalAdjusters.next(DayOfWeek.MONDAY)).atTime(9, 0).truncatedTo(ChronoUnit.SECONDS));
                break;
            default:
                update.setMessage("Whoops!   Something went wrong.");
                break;
        }
        return updateResponse;
    }

    public UpdateResponse previous(Interaction interaction) throws Exception {
        return page(interaction);

    }

    public UpdateResponse next(Interaction interaction) throws Exception {
        return page(interaction);
    }

    private UpdateResponse page(Interaction interaction) throws Exception {
        webhook.page(interaction);
        UpdateResponse updateResponse = new UpdateResponse();
        Update update = new Update();
        updateResponse.setUpdate(update);
        return updateResponse;
    }

}