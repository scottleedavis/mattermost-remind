package scottleedavis.mattermost.remind.db;

import static org.junit.Assert.*;
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
import java.util.Arrays;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ReminderRepositoryTests {

    @Resource
    ReminderRepository reminderRepository;

    @Resource
    ReminderOccurrenceRepository reminderOccurrenceRepository;

    @Before
    public void setUp() {

    }

    @Test
    @Transactional
    public void findByUserName() {
        Reminder reminder = new Reminder();
        reminder.setMessage("foo to the bar");
        reminder.setTarget("@foo");
        reminder.setUserName("@foo");
        reminderRepository.save(reminder);

        List<Reminder> reminders = reminderRepository.findByUserName("@foo");

        assertTrue(reminders.size() == 1);

        assertTrue(reminders.get(0).getOccurrences().size() == 0);

    }

    @Test
    @Transactional
    public void findByUserNameOccurrences() {
        Reminder reminder = new Reminder();
        reminder.setMessage("foo to the bar");
        reminder.setTarget("@foo");
        reminder.setUserName("@foo");
        ReminderOccurrence reminderOccurrence = new ReminderOccurrence();
        reminderOccurrence.setOccurrence(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS));
        reminderOccurrence.setReminder(reminder);
        ReminderOccurrence reminderOccurrence2 = new ReminderOccurrence();
        reminderOccurrence2.setOccurrence(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS));
        reminderOccurrence2.setReminder(reminder);
        reminder.setOccurrences(Arrays.asList(reminderOccurrence, reminderOccurrence2));
        reminderRepository.save(reminder);

        List<Reminder> reminders = reminderRepository.findByUserName("@foo");
        Reminder reminder1 = reminders.get(0);

        assertTrue(reminder1.getOccurrences().size() == 2);

    }

    @Test
    @Transactional
    public void deleteAllByUsername() {
        Reminder reminder = new Reminder();
        reminder.setMessage("foo to the bar");
        reminder.setTarget("@foo");
        reminder.setUserName("@foo");
        ReminderOccurrence reminderOccurrence = new ReminderOccurrence();
        reminderOccurrence.setOccurrence(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS));
        reminderOccurrence.setReminder(reminder);
        ReminderOccurrence reminderOccurrence2 = new ReminderOccurrence();
        reminderOccurrence2.setOccurrence(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS));
        reminderOccurrence2.setReminder(reminder);
        reminder.setOccurrences(Arrays.asList(reminderOccurrence, reminderOccurrence2));
        reminderRepository.save(reminder);

        reminderRepository.delete(reminder);

        List<Reminder> reminders = reminderRepository.findByUserName("@foo");

        assertTrue(reminders.size() == 0);

        List<ReminderOccurrence> occurrences = reminderOccurrenceRepository.findAll();

        assertTrue( occurrences.size() == 0 );

    }

    @Test
    @Transactional
    public void addOneOccurrenceToUsername() {
        Reminder reminder = new Reminder();
        reminder.setMessage("foo to the bar");
        reminder.setTarget("@foo");
        reminder.setUserName("@foo");
        ReminderOccurrence reminderOccurrence = new ReminderOccurrence();
        reminderOccurrence.setOccurrence(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS));
        reminderOccurrence.setReminder(reminder);
//        ReminderOccurrence reminderOccurrence2 = new ReminderOccurrence();
//        reminderOccurrence2.setOccurrence(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS));
//        reminderOccurrence2.setReminder(reminder);
        reminder.setOccurrences(Arrays.asList(reminderOccurrence));
        reminderRepository.save(reminder);

        List<Reminder> reminders = reminderRepository.findByUserName("@foo");
        Reminder reminder1 = reminders.get(0);

        assertTrue(reminder1.getOccurrences().size() == 1);

        ReminderOccurrence reminderOccurrence2 = new ReminderOccurrence();
        reminderOccurrence2.setOccurrence(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS));
        reminderOccurrence2.setReminder(reminder);
        List<ReminderOccurrence> occurrences = new ArrayList<>(reminder1.getOccurrences());
        occurrences.add(reminderOccurrence2);
        reminder1.setOccurrences(occurrences);
        reminderRepository.save(reminder1);

        reminders = reminderRepository.findByUserName("@foo");
        reminder1 = reminders.get(0);

        assertTrue(reminder1.getOccurrences().size() == 2);

    }

    @Test
    @Transactional
    public void removeOneOccurrenceToUsername() {
        Reminder reminder = new Reminder();
        reminder.setMessage("foo to the bar");
        reminder.setTarget("@foo");
        reminder.setUserName("@foo");
        ReminderOccurrence reminderOccurrence = new ReminderOccurrence();
        reminderOccurrence.setOccurrence(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS));
        reminderOccurrence.setReminder(reminder);
        ReminderOccurrence reminderOccurrence2 = new ReminderOccurrence();
        reminderOccurrence2.setOccurrence(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS));
        reminderOccurrence2.setReminder(reminder);
        reminder.setOccurrences(Arrays.asList(reminderOccurrence));
        reminderRepository.save(reminder);

        List<Reminder> reminders = reminderRepository.findByUserName("@foo");
        Reminder reminder1 = reminders.get(0);

        assertTrue(reminder1.getOccurrences().size() == 1);

        List<ReminderOccurrence> occurrences = new ArrayList<>(reminder1.getOccurrences());
        occurrences.remove(reminderOccurrence2);
        reminder1.setOccurrences(occurrences);
        reminderRepository.save(reminder1);

        reminders = reminderRepository.findByUserName("@foo");
        reminder1 = reminders.get(0);

        assertTrue(reminder1.getOccurrences().size() == 1);
    }

}
