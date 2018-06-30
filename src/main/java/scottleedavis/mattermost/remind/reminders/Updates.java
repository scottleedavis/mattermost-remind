package scottleedavis.mattermost.remind.reminders;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import scottleedavis.mattermost.remind.db.Reminder;
import scottleedavis.mattermost.remind.db.ReminderRepository;
import scottleedavis.mattermost.remind.messages.Interaction;
import scottleedavis.mattermost.remind.messages.Update;
import scottleedavis.mattermost.remind.messages.UpdateResponse;

import javax.annotation.Resource;
import java.time.temporal.ChronoUnit;

@Service
public class Updates {

    @Autowired
    private Options options;

    @Resource
    private ReminderRepository reminderRepository;

    public UpdateResponse delete(Interaction interaction) throws Exception {
        Reminder reminder = getReminder(interaction);
        UpdateResponse updateResponse = new UpdateResponse();
        Update update = new Update();
        update.setMessage("Ok! I’ve deleted the reminder “" + reminder.getMessage() + "”.");
        updateResponse.setUpdate(update);
        reminderRepository.delete(reminder);
        return updateResponse;
    }

    public UpdateResponse view(Interaction interaction) throws Exception {
        Reminder reminder = getReminder(interaction);
        UpdateResponse updateResponse = new UpdateResponse();
        updateResponse.setEphemeralText(options.listReminders(reminder.getUserName()));
        return updateResponse;
    }

    public UpdateResponse complete(Interaction interaction) throws Exception {
        Reminder reminder = getReminder(interaction);
        UpdateResponse updateResponse = new UpdateResponse();
        Update update = new Update();
        update.setMessage("Ok! I’ve marked the reminder  “" + reminder.getMessage() + "” as complete.");
        updateResponse.setUpdate(update);
        reminder.setComplete(true);
        reminderRepository.save(reminder);
        return updateResponse;
    }

    public UpdateResponse snooze(Interaction interaction) throws Exception {
        Reminder reminder = getReminder(interaction);
        UpdateResponse updateResponse = new UpdateResponse();
        Update update = new Update();
        update.setMessage("Ok! I’ll remind you “" + reminder.getMessage() + "” in 20 minutes");
        updateResponse.setUpdate(update);
//        reminder.setOccurrence(reminder.getOccurrence().plusMinutes(20).truncatedTo(ChronoUnit.SECONDS));
        //TODO FIX THIS
        reminderRepository.save(reminder);
        return updateResponse;
    }

    private Reminder getReminder(Interaction interaction) throws Exception {
        return reminderRepository.findById(interaction.getContext().getId())
                .orElseThrow(() -> new Exception("No reminder found with that id"));
    }
}
