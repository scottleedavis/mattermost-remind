package scottleedavis.mattermost.remind.db;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import scottleedavis.mattermost.remind.messages.ParsedRequest;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class ReminderService {

    @Resource
    ReminderRepository reminderRepository;

    @Resource
    ReminderOccurrenceRepository reminderOccurrenceRepository;

    public List<ReminderOccurrence> findByOccurrence(LocalDateTime occurrence) {
        return reminderOccurrenceRepository.findAllByOccurrence(occurrence);
    }

    public List<Reminder> findRemindersByUsername(String userName) {
        return reminderRepository.findByUserName(userName);
    }

    public Reminder schedule(String userName, ParsedRequest parsedRequest) {
        Reminder reminder = new Reminder();
        reminder.setTarget(parsedRequest.getTarget().equals("me") ? "@" + userName : parsedRequest.getTarget());
        reminder.setUserName(userName);
        reminder.setMessage(parsedRequest.getMessage());
//        reminder.setOccurrence(occurrence.calculate(parsedRequest.getWhen()));
        //TODO FIX THIS
        reminderRepository.save(reminder);
        return reminder;
    }

    //todo delete
    //todo complete
    //todo snooze
}
