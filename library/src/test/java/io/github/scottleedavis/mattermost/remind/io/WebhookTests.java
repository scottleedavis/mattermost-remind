package io.github.scottleedavis.mattermost.remind.io;

import io.github.scottleedavis.mattermost.remind.db.Reminder;
import io.github.scottleedavis.mattermost.remind.db.ReminderOccurrence;
import io.github.scottleedavis.mattermost.remind.db.ReminderOccurrenceRepository;
import io.github.scottleedavis.mattermost.remind.db.ReminderRepository;
import io.github.scottleedavis.mattermost.remind.messages.Context;
import io.github.scottleedavis.mattermost.remind.messages.Interaction;
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

        ResponseEntity<String> responseEntity = webhook.remind(reminderOccurrence);
        assertNotNull(responseEntity);

        reminder.setTarget("~town-square");
        reminderRepository.save(reminder);
        responseEntity = webhook.remind(reminderOccurrence);
        assertNotNull(responseEntity);

        reminderOccurrence.setRepeat("every day");
        reminderOccurrenceRepository.save(reminderOccurrence);
        responseEntity = webhook.remind(reminderOccurrence);
        assertNotNull(responseEntity);

        reminder.setTarget("@bob");
        reminderRepository.save(reminder);
        responseEntity = webhook.remind(reminderOccurrence);
        assertNotNull(responseEntity);

        reminderOccurrence.setRepeat("tomorrow");
        reminderOccurrenceRepository.save(reminderOccurrence);
        responseEntity = webhook.remind(reminderOccurrence);
        assertNotNull(responseEntity);

    }

    @Test
    public void page() throws Exception {

        for (int i = 1; i < 15; i++) {
            Reminder reminder = new Reminder();
            reminder.setMessage("page item " + i);
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
        }

        Interaction interaction = new Interaction();
        Context context = new Context();
        interaction.setContext(context);
        interaction.getContext().setUserName("foo");
        interaction.getContext().setFirstIndex(0);

        ResponseEntity<String> responseEntity = webhook.page(interaction);
        assertNotNull(responseEntity);

        interaction.getContext().setLastIndex(4);
        interaction.getContext().setAction("next");
        responseEntity = webhook.page(interaction);
        assertNotNull(responseEntity);

        interaction.getContext().setAction("previous");
        responseEntity = webhook.page(interaction);
        assertNotNull(responseEntity);

    }
}
