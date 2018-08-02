package io.github.scottleedavis.mattermost.remind.io;

import io.github.scottleedavis.mattermost.remind.db.Reminder;
import io.github.scottleedavis.mattermost.remind.db.ReminderOccurrence;
import io.github.scottleedavis.mattermost.remind.db.ReminderOccurrenceRepository;
import io.github.scottleedavis.mattermost.remind.db.ReminderRepository;
import io.github.scottleedavis.mattermost.remind.messages.*;
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
        reminderRepository.deleteAll();
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
    public void deleteCompleted() throws Exception {
        Interaction interaction = new Interaction();
        Context context = new Context();
        context.setId(reminderOccurrence.getId());
        context.setAction("deleteCompleted");
        interaction.setContext(context);
        interaction.setUserId("TEST");

        UpdateResponse updateResponse = updateInteraction.deleteCompleted(interaction);

        assertEquals(updateResponse.getUpdate().getMessage(), "Ok! I’ve deleted all completed reminders.");
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

        assertTrue(updateResponse.getEphemeralText().startsWith("*Past and incomplete*:\n" +
                "* \"foo to the bar\"\n" +
                "\n" +
                "*Note*:  To interact with these reminders use `/remind list` in your personal user channel"));

    }

    @Test
    @Transactional
    public void viewComplete() throws Exception {
        Interaction interaction = new Interaction();
        Context context = new Context();
        context.setId(reminderOccurrence.getId());
        context.setAction("viewComplete");
        interaction.setContext(context);
        interaction.setUserId("TEST");

        UpdateResponse updateResponse = updateInteraction.viewComplete(interaction);

        assertEquals(updateResponse.getEphemeralText(), "I cannot find any reminders for you. Type `/remind` to set one.");
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
        Interaction interaction = new Interaction();
        Context context = new Context();
        context.setId(reminderOccurrence.getId());
        context.setAction("close");
        interaction.setContext(context);
        interaction.setUserId("TEST");

        UpdateResponse updateResponse = updateInteraction.close();

        assertNotNull(updateResponse.getUpdate());

        assertEquals(updateResponse.getUpdate().getMessage(), "");

    }

    @Test
    public void previous() throws Exception {
        Integer firstIndex = 5;
        Integer lastIndex = 10;
        Interaction interaction = new Interaction();
        Context context = new Context();
        context.setAction("previous");
        context.setUserName("test");
        context.setFirstIndex(firstIndex);
        context.setLastIndex(lastIndex);
        interaction.setContext(context);
        interaction.setUserId("TEST");

        UpdateResponse updateResponse = updateInteraction.previous(interaction);

        assertNotNull(updateResponse.getUpdate());

        assertNull(updateResponse.getUpdate().getMessage());
    }

    @Test
    public void next() throws Exception {
        Integer firstIndex = 0;
        Integer lastIndex = 5;
        Interaction interaction = new Interaction();
        Context context = new Context();
        context.setAction("next");
        context.setUserName("test");
        context.setFirstIndex(firstIndex);
        context.setLastIndex(lastIndex);
        interaction.setContext(context);
        interaction.setUserId("TEST");

        UpdateResponse updateResponse = updateInteraction.next(interaction);

        assertNotNull(updateResponse.getUpdate());

        assertNull(updateResponse.getUpdate().getMessage());
    }

}
