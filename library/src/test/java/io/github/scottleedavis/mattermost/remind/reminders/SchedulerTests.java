package io.github.scottleedavis.mattermost.remind.reminders;

import io.github.scottleedavis.mattermost.remind.db.Reminder;
import io.github.scottleedavis.mattermost.remind.db.ReminderOccurrence;
import io.github.scottleedavis.mattermost.remind.db.ReminderOccurrenceRepository;
import io.github.scottleedavis.mattermost.remind.db.ReminderRepository;
import io.github.scottleedavis.mattermost.remind.io.Webhook;
import io.github.scottleedavis.mattermost.remind.messages.Response;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;

@RunWith(SpringRunner.class)
@SpringBootTest
public class SchedulerTests {

    @Autowired
    private Scheduler scheduler;

    @SpyBean
    private Webhook webhook;

    @Resource
    private ReminderRepository reminderRepository;

    @Resource
    private ReminderOccurrenceRepository reminderOccurrenceRepository;

    @Test
    public void setReminder() {

        String userName = "me";
        String userId = "@scottd";
        String channelName = "#Off-Topic";
        String payload;
        Response response;

        payload = "help";
        response = scheduler.setReminder(userName, payload, userId, channelName);
        assertTrue(response.getText().startsWith(":wave: Need some help"));

        payload = "list";
        response = scheduler.setReminder(userName, payload, userId, channelName);
        assertEquals(response.getText(), "I cannot find any reminders for you. Type `/remind` to set one.");

        payload = "version";
        response = scheduler.setReminder(userName, payload, userId, channelName);
        assertNotNull(response.getText());

        payload = "me test this service every other day at 11:00am";
        response = scheduler.setReminder(userName, payload, userId, channelName);
        assertEquals(response.getText(), ":thumbsup: I will remind you \"test this service\" at 11AM every other day");

        payload = "@bob test this service every other day at 11:00am";
        response = scheduler.setReminder(userName, payload, userId, channelName);
        assertEquals(response.getText(), "Sorry, you can't recurring reminders for other users.");

    }

    @Test
    public void runSchedule() throws Exception {

        LocalDateTime ldt = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);
        Reminder reminder = new Reminder();
        reminder.setMessage("foo to the bar");
        reminder.setTarget("@foo");
        reminder.setUserName("@foo");
        ReminderOccurrence reminderOccurrence = new ReminderOccurrence();
        reminderOccurrence.setOccurrence(ldt);
        reminderOccurrence.setSnoozed(ldt);
        reminderOccurrence.setReminder(reminder);
        reminderOccurrenceRepository.save(reminderOccurrence);
        List<ReminderOccurrence> list = new ArrayList<>();
        list.add(reminderOccurrence);
        reminder.setOccurrences(list);
        reminderRepository.save(reminder);

        scheduler.runSchedule();

        Mockito.verify(webhook, Mockito.times(2)).invoke(any());


    }

}
