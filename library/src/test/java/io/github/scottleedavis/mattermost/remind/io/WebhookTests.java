package io.github.scottleedavis.mattermost.remind.io;

import io.github.scottleedavis.mattermost.remind.db.Reminder;
import io.github.scottleedavis.mattermost.remind.db.ReminderOccurrence;
import io.github.scottleedavis.mattermost.remind.db.ReminderOccurrenceRepository;
import io.github.scottleedavis.mattermost.remind.db.ReminderRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertNotNull;

@RunWith(SpringRunner.class)
@SpringBootTest
public class WebhookTests {

    @Autowired
    private Webhook webhook;

    @Resource
    private ReminderRepository reminderRepository;

    @Resource
    private ReminderOccurrenceRepository reminderOccurrenceRepository;


    @Test
    public void invoke() throws Exception {
        Reminder reminder = new Reminder();
        reminder.setMessage("foo to the bar");
        reminder.setTarget("@foo");
        reminder.setUserName("@foo");
        ReminderOccurrence reminderOccurrence = new ReminderOccurrence();
        reminderOccurrence.setOccurrence(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS));
        reminderOccurrence.setReminder(reminder);
        reminderOccurrenceRepository.save(reminderOccurrence);
        List<ReminderOccurrence> list = new ArrayList<>();
        list.add(reminderOccurrence);
        reminder.setOccurrences(list);
        reminderRepository.save(reminder);

        ResponseEntity<String> responseEntity = webhook.invoke(reminderOccurrence);
        assertNotNull(responseEntity);

        reminder.setTarget("~town-square");
        reminderRepository.save(reminder);
        responseEntity = webhook.invoke(reminderOccurrence);
        assertNotNull(responseEntity);

        reminderOccurrence.setRepeat("every day");
        reminderOccurrenceRepository.save(reminderOccurrence);
        responseEntity = webhook.invoke(reminderOccurrence);
        assertNotNull(responseEntity);

    }
}
