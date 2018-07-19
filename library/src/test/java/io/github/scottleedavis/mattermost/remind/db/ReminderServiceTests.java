package io.github.scottleedavis.mattermost.remind.db;

import io.github.scottleedavis.mattermost.remind.messages.ParsedRequest;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ReminderServiceTests {

    private Reminder reminder;

    @Autowired
    private ReminderService reminderService;

    @Resource
    private ReminderRepository reminderRepository;

    @Resource
    private ReminderOccurrenceRepository reminderOccurrenceRepository;

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
        reminderOccurrence2.setSnoozed(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS));
        reminderOccurrence2.setReminder(reminder);
        List<ReminderOccurrence> list = new ArrayList<>();
        list.add(reminderOccurrence);
        list.add(reminderOccurrence2);
        reminder.setOccurrences(list);
        reminderRepository.save(reminder);
        this.reminder = reminder;
    }

    @After
    public void tearDown() {
        reminderRepository.deleteAll();
    }

    @Test
    @Transactional
    public void findByOccurrence() {

        List<ReminderOccurrence> reminderOccurrences = reminderService.findByOccurrence(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS));
        assertTrue(reminderOccurrences.size() == 2);
    }

    @Test
    public void findBySnoozed() {

        List<ReminderOccurrence> reminderOccurrences = reminderService.findBySnoozed(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS));
        assertTrue(reminderOccurrences.size() == 1);
    }

    @Test
    public void findByUsername() {
        List<Reminder> reminders = reminderService.findByUsername("@foo");
        assertTrue(reminders.size() == 1);
    }

    @Test
    public void schedule() throws Exception {
        ParsedRequest parsedRequest = new ParsedRequest();
        parsedRequest.setWhen("on 12/18");
        parsedRequest.setTarget("@scottd");
        parsedRequest.setMessage("Super doo");
        String userName = "scottd";
        Reminder reminder = reminderService.schedule(userName, parsedRequest);

        assertEquals(reminder.getUserName(), userName);

        assertEquals(reminder.getMessage(), "Super doo");

        assertEquals(reminder.getTarget(), "@scottd");

        assertTrue(reminder.getOccurrences().size() == 1);

    }

    @Test
    public void delete() {

        reminderService.delete(reminder);

        List<Reminder> reminders = reminderRepository.findAll();

        assertTrue(reminders.size() == 0);
    }

    @Test
    public void deleteCompleted() {
        LocalDateTime testTime = LocalDateTime.now().truncatedTo(ChronoUnit.HOURS);
        reminder.setCompleted(testTime);
        reminderRepository.save(reminder);

        reminderService.deleteCompleted(reminder.getUserName());

        List<Reminder> reminders = reminderRepository.findByUserName(reminder.getUserName()).stream()
                .filter(r -> r.getCompleted() != null).collect(Collectors.toList());

        assertTrue(reminders.size() == 0);
    }

    @Test
    public void complete() {

        reminderService.complete(reminder);

        Reminder reminder1 = reminderRepository.findById(reminder.getId()).orElse(null);

        assertNotNull(reminder1);

        assertNotNull(reminder1.getCompleted());
    }

    @Test
    @Transactional
    public void snooze() {

        LocalDateTime testTime = LocalDateTime.now().truncatedTo(ChronoUnit.HOURS);
        reminderService.snooze(reminder.getOccurrences().get(1), testTime);

        List<ReminderOccurrence> reminderOccurrence = reminderOccurrenceRepository.findAllBySnoozed(testTime);

        assertTrue(reminderOccurrence.size() == 1);

        assertEquals(reminderOccurrence.get(0).getSnoozed(), testTime);

        assertEquals(reminder.getOccurrences().get(1).getId(), reminderOccurrence.get(0).getId());


    }

    @Test
    public void clearSnooze() {
        LocalDateTime testTime = LocalDateTime.now().truncatedTo(ChronoUnit.HOURS);
        reminder.getOccurrences().get(0).setSnoozed(testTime);
        reminderOccurrenceRepository.save(reminder.getOccurrences().get(0));

        reminderService.clearSnooze(reminder.getOccurrences().get(0));

        ReminderOccurrence reminderOccurrence = reminderOccurrenceRepository.findById(reminder.getOccurrences().get(0).getId()).orElse(null);

        assertNotNull(reminderOccurrence);

        assertTrue(reminderOccurrence.getSnoozed() == null);
    }

    @Test
    @Transactional
    public void reschedule() throws Exception {

        ReminderOccurrence reminderOccurrence = new ReminderOccurrence();
        reminderOccurrence.setOccurrence(LocalDate.now().with(TemporalAdjusters.next(DayOfWeek.WEDNESDAY)).minusWeeks(1l).atTime(9, 0).truncatedTo(ChronoUnit.SECONDS));
        reminderOccurrence.setReminder(reminder);
        reminderOccurrence.setRepeat("every wednesday");
        LocalDateTime testTime = LocalDate.now().with(TemporalAdjusters.next(DayOfWeek.WEDNESDAY)).atTime(9, 0).truncatedTo(ChronoUnit.SECONDS);
        reminderService.reschedule(reminderOccurrence);
        assertEquals(reminderOccurrence.getOccurrence(), testTime);

    }

    @Test
    @Transactional
    public void rescheduleEveryOther() throws Exception {

        ReminderOccurrence reminderOccurrence = new ReminderOccurrence();
        reminderOccurrence.setOccurrence(LocalDate.now().with(TemporalAdjusters.next(DayOfWeek.WEDNESDAY)).minusWeeks(1l).atTime(9, 0).truncatedTo(ChronoUnit.SECONDS));
        reminderOccurrence.setReminder(reminder);
        reminderOccurrence.setRepeat("every other wednesday");
        LocalDateTime testTime = LocalDate.now().with(TemporalAdjusters.next(DayOfWeek.WEDNESDAY)).plusWeeks(1).atTime(9, 0).truncatedTo(ChronoUnit.SECONDS);
        reminderService.reschedule(reminderOccurrence);
        assertEquals(reminderOccurrence.getOccurrence(), testTime);

    }

    @Test
    @Transactional
    public void rescheduleEveryYear() throws Exception {

        ReminderOccurrence reminderOccurrence = new ReminderOccurrence();
        reminderOccurrence.setOccurrence(LocalDate.now().withMonth(1).withDayOfMonth(18).atStartOfDay().truncatedTo(ChronoUnit.SECONDS));
        reminderOccurrence.setReminder(reminder);
        reminderOccurrence.setRepeat("every 1/18");
        LocalDateTime testTime = LocalDate.now().withMonth(1).withDayOfMonth(18).plusYears(1L).atTime(9, 0).truncatedTo(ChronoUnit.SECONDS);
        reminderService.reschedule(reminderOccurrence);
        assertEquals(reminderOccurrence.getOccurrence(), testTime);

    }
}
