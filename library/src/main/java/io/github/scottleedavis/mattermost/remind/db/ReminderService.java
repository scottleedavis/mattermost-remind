package io.github.scottleedavis.mattermost.remind.db;

import io.github.scottleedavis.mattermost.remind.exceptions.ReminderException;
import io.github.scottleedavis.mattermost.remind.exceptions.ReminderServiceException;
import io.github.scottleedavis.mattermost.remind.messages.Interaction;
import io.github.scottleedavis.mattermost.remind.messages.ParsedRequest;
import io.github.scottleedavis.mattermost.remind.reminders.Occurrence;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    public List<ReminderOccurrence> findBySnoozed(LocalDateTime snoozed) {
        return reminderOccurrenceRepository.findAllBySnoozed(snoozed);
    }

    public List<Reminder> findByUsername(String userName) {
        return reminderRepository.findByUserName(userName);
    }

    public ReminderOccurrence findByInteraction(Interaction interaction) throws Exception {
        return reminderOccurrenceRepository.findById(interaction.getContext().getId())
                .orElseThrow(() -> new ReminderServiceException("No occurrence found with that id. (" + Long.toString(interaction.getContext().getId()) + ")"));

    }

    public Reminder schedule(String userName, ParsedRequest parsedRequest) throws Exception {
        Reminder reminder = new Reminder();
        reminder.setTarget(parsedRequest.getTarget().equals("me") ? "@" + userName : parsedRequest.getTarget());
        reminder.setUserName(userName);
        reminder.setMessage(parsedRequest.getMessage());
        reminder.setOccurrences(occurrence.calculate(parsedRequest.getWhen()).stream().map(ldt -> {
            ReminderOccurrence reminderOccurrence = new ReminderOccurrence();
            reminderOccurrence.setReminder(reminder);
            reminderOccurrence.setOccurrence(ldt);
            if (parsedRequest.getWhen().contains("every"))
                reminderOccurrence.setRepeat(parsedRequest.getWhen());
            return reminderOccurrence;
        }).collect(Collectors.toList()));
        reminderRepository.save(reminder);
        return reminder;
    }

    public void delete(Reminder reminder) {
        reminderRepository.delete(reminder);
    }

    public void deleteCompleted(String userName) {
        reminderRepository.findByUserName(userName).stream()
                .filter(r -> r.getCompleted() != null).collect(Collectors.toList())
                .forEach(r -> reminderRepository.delete(r));
    }

    public void complete(Reminder reminder) {
        reminder.setCompleted(LocalDateTime.now());
        reminderRepository.save(reminder);
    }

    public void snooze(ReminderOccurrence reminderOccurrence, LocalDateTime ldt) {
        reminderOccurrence.setSnoozed(ldt);
        reminderOccurrenceRepository.save(reminderOccurrence);
    }

    public void clearSnooze(ReminderOccurrence reminderOccurrence) {
        reminderOccurrence.setSnoozed(null);
        reminderOccurrenceRepository.save(reminderOccurrence);
    }

    public void reschedule(ReminderOccurrence reminderOccurrence) throws Exception {

        List<LocalDateTime> occurrences = occurrence.calculate(reminderOccurrence.getRepeat());

        LocalDateTime newOccurrence = occurrences.stream()
                .filter(o -> o.toLocalTime().equals(reminderOccurrence.getOccurrence().toLocalTime()))
                .findFirst().orElse(null);

        if (newOccurrence != null) {
            reminderOccurrence.setOccurrence(newOccurrence);
            reminderOccurrenceRepository.save(reminderOccurrence);
        } else {
            newOccurrence = occurrences.stream()
                    .filter(o -> o.getDayOfYear() == reminderOccurrence.getOccurrence().getDayOfYear())
                    .findFirst().orElseThrow(() -> new ReminderException("No matching occurrences to reschedule"));
            reminderOccurrence.setOccurrence(newOccurrence);
            reminderOccurrenceRepository.save(reminderOccurrence);
        }

    }
}
