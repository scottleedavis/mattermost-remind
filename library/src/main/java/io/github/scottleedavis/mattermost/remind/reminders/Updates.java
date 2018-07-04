package io.github.scottleedavis.mattermost.remind.reminders;

import io.github.scottleedavis.mattermost.remind.db.Reminder;
import io.github.scottleedavis.mattermost.remind.db.ReminderService;
import io.github.scottleedavis.mattermost.remind.messages.Interaction;
import io.github.scottleedavis.mattermost.remind.messages.Update;
import io.github.scottleedavis.mattermost.remind.messages.UpdateResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

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
        update.setMessage("Ok! I’ll remind you “" + reminder.getMessage() + "” in 20 minutes");
        updateResponse.setUpdate(update);
        //TODO extract different snooze values
        reminderService.snooze(reminder, LocalDateTime.now().plusMinutes(20).truncatedTo(ChronoUnit.SECONDS));
        return updateResponse;
    }

}
