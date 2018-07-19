package io.github.scottleedavis.mattermost.remind.reminders;

import io.github.scottleedavis.mattermost.remind.db.Reminder;
import io.github.scottleedavis.mattermost.remind.db.ReminderOccurrence;
import io.github.scottleedavis.mattermost.remind.db.ReminderRepository;
import io.github.scottleedavis.mattermost.remind.messages.Action;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class OptionsTests {

    @Autowired
    private Options options;

    @Resource
    private ReminderRepository reminderRepository;

    @Before
    public void setUp() {
        options.setAppUrl("http://foo/");
    }

    @Test
    public void setActions() {
        List<Action> actions = options.setActions(1L);
        assertTrue(actions.size() > 0);
        actions.forEach(action -> assertNotNull(action));
    }

    @Test
    @Transactional
    public void listComplete() {

        Reminder reminder = new Reminder();
        reminder.setTarget("foo");
        reminder.setMessage("baz");
        reminder.setUserName("FOO");
        reminder.setCompleted(LocalDateTime.parse("2019-08-04T10:11:30"));
        ReminderOccurrence reminderOccurrence = new ReminderOccurrence();
        reminderOccurrence.setOccurrence(LocalDateTime.parse("2018-08-04T10:11:30"));
        reminderOccurrence.setReminder(reminder);
        reminder.setOccurrences(Arrays.asList(reminderOccurrence));
        List<ReminderOccurrence> reminderOccurrences = new ArrayList<>();
        reminderOccurrences.add(reminderOccurrence);
        reminder.setOccurrences(reminderOccurrences);
        reminderRepository.save(reminder);

        assertEquals(options.listComplete("FOO"), "*Complete*:\n" +
                "* \"baz\" (completed at 10:11AM Sunday, August 4th)\n" +
                "\n");

        Reminder reminder2 = new Reminder();
        reminder2.setTarget("foo");
        reminder2.setMessage("baz 2");
        reminder2.setUserName("FOO");
        reminder2.setCompleted(LocalDateTime.parse("2018-01-04T10:11:30"));
        ReminderOccurrence reminderOccurrence2 = new ReminderOccurrence();
        reminderOccurrence2.setOccurrence(LocalDateTime.parse("2017-08-04T10:11:30"));
        reminderOccurrence2.setReminder(reminder2);
        reminder2.setOccurrences(Arrays.asList(reminderOccurrence2));
        List<ReminderOccurrence> reminderOccurrences2 = new ArrayList<>();
        reminderOccurrences2.add(reminderOccurrence2);
        reminder2.setOccurrences(reminderOccurrences2);
        reminderRepository.save(reminder2);

        assertEquals(options.listComplete("FOO"), "*Complete*:\n" +
                "* \"baz\" (completed at 10:11AM Sunday, August 4th)\n" +
                "* \"baz 2\" (completed at 10:11AM Thursday, January 4th)\n" +
                "\n");

    }

    @Test
    @Transactional
    public void listReminders() {

        assertEquals(options.listReminders("FOO"),
                "I cannot find any reminders for you. Type `/remind` to set one.");

        Reminder reminder = new Reminder();
        reminder.setTarget("foo");
        reminder.setMessage("baz");
        reminder.setUserName("FOO");
        reminder.setCompleted(LocalDateTime.parse("2019-08-04T10:11:30"));
        ReminderOccurrence reminderOccurrence = new ReminderOccurrence();
        reminderOccurrence.setOccurrence(LocalDateTime.parse("2018-08-04T10:11:30"));
        reminderOccurrence.setReminder(reminder);
        reminder.setOccurrences(Arrays.asList(reminderOccurrence));
        List<ReminderOccurrence> reminderOccurrences = new ArrayList<>();
        reminderOccurrences.add(reminderOccurrence);
        reminder.setOccurrences(reminderOccurrences);
        reminderRepository.save(reminder);

        assertEquals(options.listReminders("FOO"),
                "*Note*:  To interact with these reminders use `/remind list` in your personal user channel");

        reminder.setCompleted(null);
        reminderOccurrence.setOccurrence(LocalDateTime.parse("2018-08-04T10:11:30"));
        reminderOccurrence.setReminder(reminder);
        reminderOccurrences = new ArrayList<>();
        reminderOccurrences.add(reminderOccurrence);
        reminder.setOccurrences(reminderOccurrences);
        reminderRepository.save(reminder);

        assertEquals(options.listReminders("FOO"),
                "*Upcoming*:\n" +
                        "* \"baz\" 10:11AM Saturday, August 4th\n" +
                        "\n" +
                        "*Note*:  To interact with these reminders use `/remind list` in your personal user channel");

        Reminder reminder2 = new Reminder();
        reminder2.setTarget("foo");
        reminder2.setMessage("baz 2");
        reminder2.setUserName("FOO");
        ReminderOccurrence reminderOccurrence2 = new ReminderOccurrence();
        reminderOccurrence2.setOccurrence(LocalDateTime.parse("2018-08-04T10:11:30"));
        reminderOccurrence2.setReminder(reminder2);
        reminderOccurrence2.setRepeat("every day");
        reminder2.setOccurrences(Arrays.asList(reminderOccurrence2));
        List<ReminderOccurrence> reminderOccurrences2 = new ArrayList<>();
        reminderOccurrences2.add(reminderOccurrence2);
        reminder2.setOccurrences(reminderOccurrences2);
        reminderRepository.save(reminder2);

        assertEquals(options.listReminders("FOO"),
                "*Upcoming*:\n" +
                        "* \"baz\" 10:11AM Saturday, August 4th\n" +
                        "\n" +
                        "*Recurring*:\n" +
                        "* \"baz 2\" 10:11AM Every Day\n" +
                        "\n" +
                        "*Note*:  To interact with these reminders use `/remind list` in your personal user channel");

        Reminder reminder3 = new Reminder();
        reminder3.setTarget("foo");
        reminder3.setMessage("baz 3");
        reminder3.setUserName("FOO");
        ReminderOccurrence reminderOccurrence3 = new ReminderOccurrence();
        reminderOccurrence3.setOccurrence(LocalDateTime.parse("2011-08-04T10:11:30"));
        reminderOccurrence3.setReminder(reminder3);
        reminder3.setOccurrences(Arrays.asList(reminderOccurrence3));
        List<ReminderOccurrence> reminderOccurrences3 = new ArrayList<>();
        reminderOccurrences3.add(reminderOccurrence3);
        reminder3.setOccurrences(reminderOccurrences3);
        reminderRepository.save(reminder3);

        assertEquals(options.listReminders("FOO"),
                "*Upcoming*:\n" +
                        "* \"baz\" 10:11AM Saturday, August 4th\n" +
                        "\n" +
                        "*Recurring*:\n" +
                        "* \"baz 2\" 10:11AM Every Day\n" +
                        "\n" +
                        "*Past and incomplete*:\n" +
                        "* \"baz 3\"\n" +
                        "\n" +
                        "*Note*:  To interact with these reminders use `/remind list` in your personal user channel");

    }

    @Test
    @Transactional
    public void listRemindersAttachments() {

        assertTrue(options.listRemindersAttachments("FOO").size() == 1);

        Reminder reminder = new Reminder();
        reminder.setTarget("foo");
        reminder.setMessage("baz");
        reminder.setUserName("FOO");
        reminder.setCompleted(LocalDateTime.parse("2019-08-04T10:11:30"));
        ReminderOccurrence reminderOccurrence = new ReminderOccurrence();
        reminderOccurrence.setOccurrence(LocalDateTime.parse("2018-08-04T10:11:30"));
        reminderOccurrence.setReminder(reminder);
        reminder.setOccurrences(Arrays.asList(reminderOccurrence));
        List<ReminderOccurrence> reminderOccurrences = new ArrayList<>();
        reminderOccurrences.add(reminderOccurrence);
        reminder.setOccurrences(reminderOccurrences);
        reminderRepository.save(reminder);

        Reminder reminder2 = new Reminder();
        reminder2.setTarget("foo");
        reminder2.setMessage("baz 2");
        reminder2.setUserName("FOO");
        ReminderOccurrence reminderOccurrence2 = new ReminderOccurrence();
        reminderOccurrence2.setOccurrence(LocalDateTime.parse("2018-08-04T10:11:30"));
        reminderOccurrence2.setReminder(reminder2);
        reminderOccurrence2.setRepeat("every day");
        reminder2.setOccurrences(Arrays.asList(reminderOccurrence2));
        List<ReminderOccurrence> reminderOccurrences2 = new ArrayList<>();
        reminderOccurrences2.add(reminderOccurrence2);
        reminder2.setOccurrences(reminderOccurrences2);
        reminderRepository.save(reminder2);

        Reminder reminder3 = new Reminder();
        reminder3.setTarget("foo");
        reminder3.setMessage("baz 3");
        reminder3.setUserName("FOO");
        ReminderOccurrence reminderOccurrence3 = new ReminderOccurrence();
        reminderOccurrence3.setOccurrence(LocalDateTime.parse("2011-08-04T10:11:30"));
        reminderOccurrence3.setReminder(reminder3);
        reminder3.setOccurrences(Arrays.asList(reminderOccurrence3));
        List<ReminderOccurrence> reminderOccurrences3 = new ArrayList<>();
        reminderOccurrences3.add(reminderOccurrence3);
        reminder3.setOccurrences(reminderOccurrences3);
        reminderRepository.save(reminder3);

        assertTrue(options.listRemindersAttachments("FOO").size() == 3);


    }

    @Test
    public void finishedActions() {
        List<Action> actions = options.finishedActions(1L, false, false);
        assertTrue(actions.size() == 7);

        actions = options.finishedActions(1L, true, true);
        assertTrue(actions.size() == 5);

        actions = options.finishedActions(1L, true, false);
        assertTrue(actions.size() == 5);

        actions = options.finishedActions(1L, false, true);
        assertTrue(actions.size() == 2);

    }

    @Test
    public void listActions() {
        List<Action> actions = options.listActions(1L, false, false);
        assertTrue(actions.size() == 2);

        actions = options.listActions(1L, true, true);
        assertTrue(actions.size() == 1);

        actions = options.listActions(1L, true, false);
        assertTrue(actions.size() == 1);

        actions = options.listActions(1L, false, true);
        assertTrue(actions.size() == 5);
    }
}
