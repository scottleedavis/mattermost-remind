package io.github.scottleedavis.mattermost.remind.reminders;

import io.github.scottleedavis.mattermost.remind.db.Reminder;
import io.github.scottleedavis.mattermost.remind.db.ReminderService;
import io.github.scottleedavis.mattermost.remind.messages.Interaction;
import io.github.scottleedavis.mattermost.remind.messages.Update;
import io.github.scottleedavis.mattermost.remind.messages.UpdateResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;

@Service
public class Updates {

    @Autowired
    private Options options;

    @Autowired
    private ReminderService reminderService;

    public UpdateResponse delete(Interaction interaction) throws Exception {
        Reminder reminder = reminderService.findByInteraction(interaction);
        UpdateResponse updateResponse = new UpdateResponse();
        Update update = new Update();
        update.setMessage("Ok! I’ve deleted the reminder “" + reminder.getMessage() + "”.");
        updateResponse.setUpdate(update);
        reminderService.delete(reminder);
        return updateResponse;
    }

    public UpdateResponse view(Interaction interaction) throws Exception {
        Reminder reminder = reminderService.findByInteraction(interaction);
        UpdateResponse updateResponse = new UpdateResponse();
        updateResponse.setEphemeralText(options.listReminders(reminder.getUserName()));
        return updateResponse;
    }

    public UpdateResponse complete(Interaction interaction) throws Exception {
        Reminder reminder = reminderService.findByInteraction(interaction);
        UpdateResponse updateResponse = new UpdateResponse();
        Update update = new Update();
        update.setMessage("Ok! I’ve marked the reminder  “" + reminder.getMessage() + "” as complete.");
        updateResponse.setUpdate(update);
        reminderService.complete(reminder);
        return updateResponse;
    }

    public UpdateResponse snooze(Interaction interaction) throws Exception {
        Reminder reminder = reminderService.findByInteraction(interaction);
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
                reminderService.snooze(reminder, LocalDateTime.now().plusMinutes(20).truncatedTo(ChronoUnit.SECONDS));
                break;
            case ArgumentType.ONE_HOUR:
                reminderService.snooze(reminder, LocalDateTime.now().plusHours(1).truncatedTo(ChronoUnit.SECONDS));
                break;
            case ArgumentType.THREE_HOURS:
                reminderService.snooze(reminder, LocalDateTime.now().plusHours(3).truncatedTo(ChronoUnit.SECONDS));
                break;
            case ArgumentType.TOMORROW_AT_9AM:
                reminderService.snooze(reminder, LocalDate.now().plusDays(1).atTime(9, 0).truncatedTo(ChronoUnit.SECONDS));
                break;
            case ArgumentType.NEXT_WEEK:
                reminderService.snooze(reminder, LocalDate.now().with(TemporalAdjusters.next(DayOfWeek.MONDAY)).atTime(9, 0).truncatedTo(ChronoUnit.SECONDS));
                break;
            default:
                update.setMessage("Whoops!   Something went wrong.");
                break;
        }
        return updateResponse;
    }

}
