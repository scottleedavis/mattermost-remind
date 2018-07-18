package io.github.scottleedavis.mattermost.remind.reminders;

import io.github.scottleedavis.mattermost.remind.db.ReminderOccurrence;
import io.github.scottleedavis.mattermost.remind.messages.ParsedRequest;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class FormatterTests {

    @Autowired
    private Formatter formatter;

    @Test
    public void upcomingReminder() {

        ReminderOccurrence reminderOccurrence = new ReminderOccurrence();
        reminderOccurrence.setOccurrence(LocalDateTime.parse("2018-08-04T10:11:30"));
        String output = formatter.upcomingReminder(Arrays.asList(reminderOccurrence));

        assertNotNull(output);

        assertEquals(output, "10:11AM Saturday, August 4th\n");


        assertTrue(false);

    }

    @Test
    public void reminderResponse() throws Exception {
        String output;
        ParsedRequest parsedRequest = new ParsedRequest();
        parsedRequest.setMessage("FooBar");
        parsedRequest.setTarget("scottd");

        parsedRequest.setWhen("on August 4th at 10:11am");
        output = formatter.reminderResponse(parsedRequest);
        assertNotNull(output);
        assertEquals(output, ":thumbsup: I will remind scottd \"FooBar\" at 10:11AM Saturday, August 4th");

        parsedRequest.setWhen("at 10:11am");
        output = formatter.reminderResponse(parsedRequest);
        assertNotNull(output);
        assertTrue(output.contains("scottd") && output.contains("FooBar") && output.contains("10:11AM"));

        parsedRequest.setWhen("every August 4th at 10:11am");
        output = formatter.reminderResponse(parsedRequest);
        assertNotNull(output);
        assertEquals(output, ":thumbsup: I will remind scottd \"FooBar\" at 10:11AM every  August 4th");

        parsedRequest.setWhen("in 20 seconds");
        output = formatter.reminderResponse(parsedRequest);
        assertNotNull(output);
        assertEquals(output, ":thumbsup: I will remind scottd \"FooBar\" in 20 seconds");

//        parsedRequest.setWhen("in 2 secs");
//        output = formatter.reminderResponse(parsedRequest);
//        assertNotNull(output);
//        assertEquals(output, ":thumbsup: I will remind scottd \"FooBar\" in 2 seconds");

        parsedRequest.setMessage("to FooBar");
        parsedRequest.setWhen("in 20 seconds");
        output = formatter.reminderResponse(parsedRequest);
        assertNotNull(output);
        assertEquals(output, ":thumbsup: I will remind scottd to \"FooBar\" in 20 seconds");

    }

    @Test
    public void capitalize() {
        String test = "aBCDEfg";
        String check = formatter.capitalize(test);
        assertEquals("Abcdefg", check);
    }

    @Test
    public void daySuffix() {
        assertEquals(formatter.daySuffix(1), "1st");
        assertEquals(formatter.daySuffix(2), "2nd");
        assertEquals(formatter.daySuffix(3), "3rd");
        assertEquals(formatter.daySuffix(4), "4th");
        assertEquals(formatter.daySuffix(5), "5th");
        assertEquals(formatter.daySuffix(6), "6th");
        assertEquals(formatter.daySuffix(7), "7th");
        assertEquals(formatter.daySuffix(8), "8th");
        assertEquals(formatter.daySuffix(9), "9th");
        assertEquals(formatter.daySuffix(10), "10th");
        assertEquals(formatter.daySuffix(11), "11th");
        assertEquals(formatter.daySuffix(12), "12th");
        assertEquals(formatter.daySuffix(13), "13th");
        assertEquals(formatter.daySuffix(14), "14th");
        assertEquals(formatter.daySuffix(15), "15th");
        assertEquals(formatter.daySuffix(16), "16th");
        assertEquals(formatter.daySuffix(17), "17th");
        assertEquals(formatter.daySuffix(18), "18th");
        assertEquals(formatter.daySuffix(19), "19th");
        assertEquals(formatter.daySuffix(20), "20th");
        assertEquals(formatter.daySuffix(21), "21st");
        assertEquals(formatter.daySuffix(22), "22nd");
        assertEquals(formatter.daySuffix(23), "23rd");
        assertEquals(formatter.daySuffix(24), "24th");
        assertEquals(formatter.daySuffix(25), "25th");
        assertEquals(formatter.daySuffix(26), "26th");
        assertEquals(formatter.daySuffix(27), "27th");
        assertEquals(formatter.daySuffix(28), "28th");
        assertEquals(formatter.daySuffix(29), "29th");
        assertEquals(formatter.daySuffix(30), "30th");
        assertEquals(formatter.daySuffix(31), "31st");
    }

    @Test
    public void normalizeTime() throws Exception {
        assertEquals(formatter.normalizeTime("noon"), "12:00");
        assertEquals(formatter.normalizeTime("midnight"), "00:00");
        assertEquals(formatter.normalizeTime("three"), "03:00");
        assertEquals(formatter.normalizeTime("22"), "22:00");
        assertEquals(formatter.normalizeTime("12:30 pm"), "12:30");
    }

    @Test
    public void normalizeDate() throws Exception {
        assertEquals(formatter.normalizeDate("day"), "DAY");
        assertEquals(formatter.normalizeDate("everyday"), "EVERYDAY");
        assertEquals(formatter.normalizeDate("tomorrow"), "TOMORROW");
        assertEquals(formatter.normalizeDate("wednES"), "WEDNESDAY");
        assertEquals(formatter.normalizeDate("jan 12"), "JANUARY 12 2018");
        assertEquals(formatter.normalizeDate("4/4/18"), "APRIL 4 2019");
    }

    @Test
    public void wordToNumber() throws Exception {
        assertTrue(formatter.wordToNumber("first") == 1);
        assertTrue(formatter.wordToNumber("third") == 3);
        assertTrue(formatter.wordToNumber("fourteenth") == 14);
    }

    @Test
    public void amPm() {
        assertEquals(formatter.amPm(LocalDate.now().atTime(9, 0)), "AM");
        assertEquals(formatter.amPm(LocalDate.now().atTime(14, 0)), "PM");
    }

    @Test
    public void militaryToTwelveHour() {
        assertTrue(formatter.militaryToTwelveHour(0) == 12);
        assertTrue(formatter.militaryToTwelveHour(3) == 3);
        assertTrue(formatter.militaryToTwelveHour(12) == 12);
        assertTrue(formatter.militaryToTwelveHour(17) == 5);

    }
}
