package scottleedavis.mattermost.remind.db;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import scottleedavis.mattermost.remind.messages.ParsedRequest;
import scottleedavis.mattermost.remind.reminders.Occurrence;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class ReminderService {

    @Autowired
    private Occurrence occurrence;

    @Resource
    private ReminderRepository reminderRepository;

    @Resource
    private ReminderOccurrenceRepository reminderOccurrenceRepository;

    public List<ReminderOccurrence> findByOccurrence(LocalDateTime occurrence) {
        return reminderOccurrenceRepository.findAllByOccurrence(occurrence);
    }

    public List<Reminder> findByUsername(String userName) {
        return reminderRepository.findByUserName(userName);
    }

    public Reminder schedule(String userName, ParsedRequest parsedRequest) throws Exception {
        Reminder reminder = new Reminder();
        reminder.setTarget(parsedRequest.getTarget().equals("me") ? "@" + userName : parsedRequest.getTarget());
        reminder.setUserName(userName);
        reminder.setMessage(parsedRequest.getMessage());
        reminder.setOccurrences(occurrence.calculate(parsedRequest.getWhen()).stream().map( ldt -> {
            ReminderOccurrence reminderOccurrence = new ReminderOccurrence();
            reminderOccurrence.setReminder(reminder);
            reminderOccurrence.setOccurrence(ldt);
//            reminderOccurrence.setRepeat();
            return reminderOccurrence;
        }).collect(Collectors.toList()));
        reminderRepository.save(reminder);
        return reminder;
    }

    //todo delete
    //todo complete
    //todo snooze
}
