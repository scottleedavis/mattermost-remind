package scottleedavis.mattermost.remind.db;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ReminderServiceTests {

    @Autowired
    ReminderService reminderService;

    @Resource
    ReminderRepository reminderRepository;

    @Resource
    ReminderOccurrenceRepository reminderOccurrenceRepository;

    @Before
    public void setUp() {
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
    }

    @After
    public void tearDown() {
        reminderRepository.deleteAll();
    }

    @Test
    public void findByOccurrence() {

        List<ReminderOccurrence> reminderOccurrences = reminderService.findByOccurrence(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS));
        assertTrue(reminderOccurrences.size() == 2);
    }

    @Test
    public void findByUsername() {
        List<Reminder> reminders = reminderService.findByUsername("@foo");
        assertTrue(reminders.size() == 1);
    }

    @Test
    public void schedule() {
        assertTrue(false);
    }

    @Test
    public void delete() {
        assertTrue(false);
    }

    @Test
    public void complete() {
        assertTrue(false);
    }

    @Test
    public void snooze() {
        assertTrue(false);
    }
}
