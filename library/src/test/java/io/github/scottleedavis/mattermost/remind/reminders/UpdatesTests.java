package io.github.scottleedavis.mattermost.remind.reminders;

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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(SpringRunner.class)
@SpringBootTest
public class UpdatesTests {

    @Autowired
    private Updates updates;

    @Resource
    private ReminderRepository reminderRepository;

    @Resource
    private ReminderOccurrenceRepository reminderOccurrenceRepository;

    private Reminder reminder;

    private ReminderOccurrence reminderOccurrence;

    @Before
    public void setUp() {
        Reminder reminder = new Reminder();
        reminder.setMessage("updates test");
        reminder.setTarget("@foo");
        reminder.setUserName("@foo");
        reminderRepository.save(reminder);
        this.reminder = reminder;
        ReminderOccurrence reminderOccurrence = new ReminderOccurrence();
        reminderOccurrence.setOccurrence(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS));
        reminderOccurrence.setReminder(reminder);
        List<ReminderOccurrence> list = new ArrayList<>();
        list.add(reminderOccurrence);
        reminder.setOccurrences(list);
        reminderRepository.save(reminder);
        this.reminderOccurrence = reminderOccurrenceRepository.findAllByReminder(this.reminder).get(0);
    }

    @Test
    public void close() throws Exception {

        assertNotNull(updates.close().getUpdate().getMessage());

    }

    @Test
    public void delete() throws Exception {
        Interaction interaction = new Interaction();
        Context context = new Context();
        context.setAction("delete");
        context.setId(this.reminderOccurrence.getId());
        interaction.setContext(context);
        interaction.setUserId("FOO");
        interaction.setContext(context);
        UpdateResponse updateResponse = updates.delete(interaction);
        assertEquals(updateResponse.getUpdate().getMessage(), "Ok! I’ve deleted the reminder “" + reminder.getMessage() + "”.");

    }

    @Test
    public void deleteCompleted() throws Exception {
        Interaction interaction = new Interaction();
        Context context = new Context();
        context.setAction("deleteCompleted");
        context.setId(this.reminderOccurrence.getId());
        interaction.setContext(context);
        interaction.setUserId("FOO");
        interaction.setContext(context);
        UpdateResponse updateResponse = updates.deleteCompleted(interaction);
        assertEquals(updateResponse.getUpdate().getMessage(), "Ok! I’ve deleted all completed reminders.");

    }

    @Test
    @Transactional
    public void view() throws Exception {
        Interaction interaction = new Interaction();
        Context context = new Context();
        context.setAction("view");
        context.setId(this.reminderOccurrence.getId());
        interaction.setContext(context);
        interaction.setUserId("FOO");
        interaction.setContext(context);
        UpdateResponse updateResponse = updates.view(interaction);
        assertNotNull(updateResponse.getEphemeralText());

    }

    @Test
    @Transactional
    public void viewComplete() throws Exception {

        Interaction interaction = new Interaction();
        Context context = new Context();
        context.setAction("viewComplete");
        context.setId(this.reminderOccurrence.getId());
        interaction.setContext(context);
        interaction.setUserId("FOO");
        interaction.setContext(context);
        UpdateResponse updateResponse = updates.view(interaction);
        assertNotNull(updateResponse.getEphemeralText());

    }

    @Test
    public void complete() throws Exception {
        Interaction interaction = new Interaction();
        Context context = new Context();
        context.setAction("view");
        context.setId(this.reminderOccurrence.getId());
        interaction.setContext(context);
        interaction.setUserId("FOO");
        interaction.setContext(context);
        UpdateResponse updateResponse = updates.complete(interaction);
        assertEquals(updateResponse.getUpdate().getMessage(), "Ok! I’ve marked the reminder  “" + reminder.getMessage() + "” as complete.");

    }

    @Test
    @Transactional
    public void snooze() throws Exception {

        Interaction interaction = new Interaction();
        Context context = new Context();
        context.setAction("snooze");
        context.setId(this.reminderOccurrence.getId());
        interaction.setContext(context);
        interaction.setUserId("FOO");
        UpdateResponse updateResponse;

        context.setArgument(ArgumentType.TWENTY_MINUTES);
        interaction.setContext(context);
        updateResponse = updates.snooze(interaction);
        assertEquals(updateResponse.getUpdate().getMessage(), "Ok! I’ll remind you “updates test” in 20 minutes");

        context.setArgument(ArgumentType.ONE_HOUR);
        interaction.setContext(context);
        updateResponse = updates.snooze(interaction);
        assertEquals(updateResponse.getUpdate().getMessage(), "Ok! I’ll remind you “updates test” in 1 hour");

        context.setArgument(ArgumentType.THREE_HOURS);
        interaction.setContext(context);
        updateResponse = updates.snooze(interaction);
        assertEquals(updateResponse.getUpdate().getMessage(), "Ok! I’ll remind you “updates test” in 3 hours");

        context.setArgument(ArgumentType.TOMORROW_AT_9AM);
        interaction.setContext(context);
        updateResponse = updates.snooze(interaction);
        assertEquals(updateResponse.getUpdate().getMessage(), "Ok! I’ll remind you “updates test” Tomorrow at 9am");

        context.setArgument(ArgumentType.NEXT_WEEK);
        interaction.setContext(context);
        updateResponse = updates.snooze(interaction);
        assertEquals(updateResponse.getUpdate().getMessage(), "Ok! I’ll remind you “updates test” Next week");

    }
}
