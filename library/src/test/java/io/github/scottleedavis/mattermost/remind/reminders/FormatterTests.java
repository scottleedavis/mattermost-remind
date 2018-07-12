package io.github.scottleedavis.mattermost.remind.reminders;

import io.github.scottleedavis.mattermost.remind.db.ReminderOccurrence;
import io.github.scottleedavis.mattermost.remind.messages.ParsedRequest;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

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

        //TODO test this better

        ReminderOccurrence reminderOccurrence = new ReminderOccurrence();
        reminderOccurrence.setOccurrence(LocalDateTime.parse("2018-08-04T10:11:30"));
        String output = formatter.upcomingReminder(Arrays.asList(reminderOccurrence));

        assertNotNull(output);

        assertEquals(output, "10:11AM Saturday, August 4th\n");

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

    }

    @Test
    public void normalizeTime() {

    }

    @Test
    public void normalizeDate() {

    }

    @Test
    public void wordToNumber() {

    }
}
