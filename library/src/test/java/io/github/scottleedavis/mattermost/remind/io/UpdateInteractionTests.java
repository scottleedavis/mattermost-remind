package io.github.scottleedavis.mattermost.remind.io;

import io.github.scottleedavis.mattermost.remind.db.Reminder;
import io.github.scottleedavis.mattermost.remind.db.ReminderOccurrence;
import io.github.scottleedavis.mattermost.remind.db.ReminderOccurrenceRepository;
import io.github.scottleedavis.mattermost.remind.db.ReminderRepository;
import io.github.scottleedavis.mattermost.remind.messages.Context;
import io.github.scottleedavis.mattermost.remind.messages.Interaction;
import io.github.scottleedavis.mattermost.remind.messages.UpdateResponse;
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
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class UpdateInteractionTests {

    @Autowired
    private UpdateInteraction updateInteraction;

    @Resource
    private ReminderRepository reminderRepository;

    @Resource
    private ReminderOccurrenceRepository reminderOccurrenceRepository;

    private ReminderOccurrence reminderOccurrence;

    @Before
    public void setUp() {
        Reminder reminder = new Reminder();
        reminder.setMessage("foo to the bar");
        reminder.setTarget("@foo");
        reminder.setUserName("@foo");
        ReminderOccurrence reminderOccurrence = new ReminderOccurrence();
        reminderOccurrence.setOccurrence(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS));
        reminderOccurrence.setReminder(reminder);
        this.reminderOccurrence = reminderOccurrence;
        reminderOccurrenceRepository.save(reminderOccurrence);
        List<ReminderOccurrence> list = new ArrayList<>();
        list.add(reminderOccurrence);
        reminder.setOccurrences(list);
        reminderRepository.save(reminder);


    }

    @Test
    public void delete() throws Exception {
        Interaction interaction = new Interaction();
        Context context = new Context();
        context.setId(reminderOccurrence.getId());
        context.setAction("delete");
        interaction.setContext(context);
        interaction.setUserId("TEST");

        UpdateResponse updateResponse = updateInteraction.delete(interaction);

        assertEquals(updateResponse.getUpdate().getMessage(), "Ok! I’ve deleted the reminder “foo to the bar”.");
    }

    @Test
    @Transactional
    public void view() throws Exception {
        Interaction interaction = new Interaction();
        Context context = new Context();
        context.setId(reminderOccurrence.getId());
        context.setAction("view");
        interaction.setContext(context);
        interaction.setUserId("TEST");

        UpdateResponse updateResponse = updateInteraction.view(interaction);

        assertTrue(updateResponse.getEphemeralText().startsWith("*Upcoming*:\n"));

    }

    @Test
    @Transactional
    public void viewComplete() throws Exception {

        assertFalse(true);

    }


    @Test
    public void complete() throws Exception {
        Interaction interaction = new Interaction();
        Context context = new Context();
        context.setId(reminderOccurrence.getId());
        context.setAction("complete");
        interaction.setContext(context);
        interaction.setUserId("TEST");

        UpdateResponse updateResponse = updateInteraction.complete(interaction);

        assertEquals(updateResponse.getUpdate().getMessage(), "Ok! I’ve marked the reminder  “foo to the bar” as complete.");

    }

    @Test
    public void snooze() throws Exception {
        Interaction interaction = new Interaction();
        Context context = new Context();
        context.setId(reminderOccurrence.getId());
        context.setAction("snooze");
        context.setArgument("20 minutes");
        interaction.setContext(context);
        interaction.setUserId("TEST");

        UpdateResponse updateResponse = updateInteraction.snooze(interaction);

        assertEquals(updateResponse.getUpdate().getMessage(), "Ok! I’ll remind you “foo to the bar” in 20 minutes");

    }

    @Test
    public void close() throws Exception {
        assertTrue(false);
    }

}
