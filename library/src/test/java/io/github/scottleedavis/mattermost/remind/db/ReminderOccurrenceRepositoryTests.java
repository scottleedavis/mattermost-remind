package io.github.scottleedavis.mattermost.remind.db;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertTrue;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ReminderOccurrenceRepositoryTests {

    @Resource
    ReminderRepository reminderRepository;

    @Resource
    ReminderOccurrenceRepository reminderOccurrenceRepository;

    private Reminder reminder;

    @Before
    public void setUp() {
        reminderRepository.deleteAll();
        ;

        Reminder reminder = new Reminder();
        reminder.setMessage("foo to the bar");
        reminder.setTarget("@foo");
        reminder.setUserName("@foo");
        ReminderOccurrence reminderOccurrence = new ReminderOccurrence();
        reminderOccurrence.setOccurrence(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS));
        reminderOccurrence.setReminder(reminder);
        reminderOccurrenceRepository.save(reminderOccurrence);
        ReminderOccurrence reminderOccurrence2 = new ReminderOccurrence();
        reminderOccurrence2.setOccurrence(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS));
        reminderOccurrence2.setSnoozed(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS));
        reminderOccurrence2.setReminder(reminder);
        reminderOccurrenceRepository.save(reminderOccurrence2);
        List<ReminderOccurrence> list = new ArrayList<>();
        list.add(reminderOccurrence);
        list.add(reminderOccurrence2);
        reminder.setOccurrences(list);
        reminderRepository.save(reminder);
        this.reminder = reminder;
    }

    @Test
    @Transactional
    public void findAllByOccurrence() {

        List<ReminderOccurrence> reminderOccurrences = reminderOccurrenceRepository.findAllByOccurrence(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS));

        assertTrue(reminderOccurrences.size() == 2);
    }

    @Test
    @Transactional
    public void findAllByReminder() {
        List<ReminderOccurrence> reminderOccurrences = reminderOccurrenceRepository.findAllByReminder(this.reminder);

        assertTrue(reminderOccurrences.size() == 2);
    }

    @Test
    @Transactional
    public void findAllBySnoozed() {
        List<ReminderOccurrence> reminderOccurrences = reminderOccurrenceRepository.findAllBySnoozed(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS));

        assertTrue(reminderOccurrences.size() == 1);
    }
}
