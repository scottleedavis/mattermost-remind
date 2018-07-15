package io.github.scottleedavis.mattermost.remind.reminders;

import io.github.scottleedavis.mattermost.remind.messages.ParsedRequest;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ParserTests {

    @Autowired
    private Parser parser;

    @Test
    public void extractTarget() throws Exception {

        ParsedRequest parsedRequest = parser.extract("me me me me, doe ray so fa la ti do at noon");
        assertEquals(parsedRequest.getMessage(), "me me me, doe ray so fa la ti do");
        assertEquals(parsedRequest.getTarget(), "me");
        assertEquals(parsedRequest.getWhen(), "at noon");

    }

    @Test
    public void extractOptions() throws Exception {
        ParsedRequest parsedRequest = parser.extract("list");
        assertNull(parsedRequest.getMessage());
        assertEquals(parsedRequest.getTarget(), "list");
        assertNull(parsedRequest.getWhen());

        parsedRequest = parser.extract("help");
        assertNull(parsedRequest.getMessage());
        assertEquals(parsedRequest.getTarget(), "help");
        assertNull(parsedRequest.getWhen());

        parsedRequest = parser.extract("version");
        assertNull(parsedRequest.getMessage());
        assertEquals(parsedRequest.getTarget(), "version");
        assertNull(parsedRequest.getWhen());
    }

    @Test
    public void extractQuotes() throws Exception {

        ParsedRequest parsedRequest = parser.extract("me \"I'd really like to do it at at one every\" at noon");
        assertEquals(parsedRequest.getMessage(), "I'd really like to do it at at one every");
        assertEquals(parsedRequest.getTarget(), "me");
        assertEquals(parsedRequest.getWhen(), "at noon");

        parsedRequest = parser.extract("me \"Joe joes and \" joe \"is a joe|'|'\" joe joe\" at noon");
        assertEquals(parsedRequest.getMessage(), "Joe joes and \" joe \"is a joe|'|'\" joe joe");
        assertEquals(parsedRequest.getTarget(), "me");
        assertEquals(parsedRequest.getWhen(), "at noon");

    }

    @Test
    public void extractDateTime() throws Exception {

        ParsedRequest parsedRequest = parser.extract("me FooBar at noon");
        assertEquals(parsedRequest.getMessage(), "FooBar");
        assertEquals(parsedRequest.getTarget(), "me");
        assertEquals(parsedRequest.getWhen(), "at noon");

        parsedRequest = parser.extract("@anybody FooBar at noon");
        assertEquals(parsedRequest.getMessage(), "FooBar");
        assertEquals(parsedRequest.getTarget(), "@anybody");
        assertEquals(parsedRequest.getWhen(), "at noon");

        parsedRequest = parser.extract("~Off-Topic FooBar at noon");
        assertEquals(parsedRequest.getMessage(), "FooBar");
        assertEquals(parsedRequest.getTarget(), "~Off-Topic");
        assertEquals(parsedRequest.getWhen(), "at noon");

        parsedRequest = parser.extract("me SuperFoo in 1 second");
        assertEquals(parsedRequest.getMessage(), "SuperFoo");
        assertEquals(parsedRequest.getTarget(), "me");
        assertEquals(parsedRequest.getWhen(), "in 1 second");

        parsedRequest = parser.extract("me SuperFoo every 4 minutes");
        assertEquals(parsedRequest.getMessage(), "SuperFoo");
        assertEquals(parsedRequest.getTarget(), "me");
        assertEquals(parsedRequest.getWhen(), "every 4 minutes");

        parsedRequest = parser.extract("me SuperFoo on January 12th");
        assertEquals(parsedRequest.getMessage(), "SuperFoo");
        assertEquals(parsedRequest.getTarget(), "me");
        assertEquals(parsedRequest.getWhen(), "on January 12th");

        parsedRequest = parser.extract("me SuperFoo at 3pm");
        assertEquals(parsedRequest.getMessage(), "SuperFoo");
        assertEquals(parsedRequest.getTarget(), "me");
        assertEquals(parsedRequest.getWhen(), "at 3pm");

        parsedRequest = parser.extract("me Smile May 30");
        assertEquals(parsedRequest.getMessage(), "Smile");
        assertEquals(parsedRequest.getTarget(), "me");
        assertEquals(parsedRequest.getWhen(), "May 30 2018");

    }

    @Test
    public void extractDayOfWeek() throws Exception {

        ParsedRequest parsedRequest = parser.extract("me SuperFoo at 3pm every tuesday");
        assertEquals(parsedRequest.getMessage(), "SuperFoo");
        assertEquals(parsedRequest.getTarget(), "me");
        assertEquals(parsedRequest.getWhen(), "at 3pm every tuesday");

        parsedRequest = parser.extract("me SuperFoo at 4:30am every day");
        assertEquals(parsedRequest.getMessage(), "SuperFoo");
        assertEquals(parsedRequest.getTarget(), "me");
        assertEquals(parsedRequest.getWhen(), "at 4:30am every day");

        parsedRequest = parser.extract("~design Join the meeting Monday");
        assertEquals(parsedRequest.getMessage(), "Join the meeting");
        assertEquals(parsedRequest.getTarget(), "~design");
        assertEquals(parsedRequest.getWhen(), "Monday");

        parsedRequest = parser.extract("me Smile tomorrow");
        assertEquals(parsedRequest.getMessage(), "Smile");
        assertEquals(parsedRequest.getTarget(), "me");
        assertEquals(parsedRequest.getWhen(), "Tomorrow");

        parsedRequest = parser.extract("me Smile everyday");
        assertEquals(parsedRequest.getMessage(), "Smile");
        assertEquals(parsedRequest.getTarget(), "me");
        assertEquals(parsedRequest.getWhen(), "Everyday");

        parsedRequest = parser.extract("me Smile Mondays");
        assertEquals(parsedRequest.getMessage(), "Smile");
        assertEquals(parsedRequest.getTarget(), "me");
        assertEquals(parsedRequest.getWhen(), "Mondays");

        parsedRequest = parser.extract("me Smile Tuesdays");
        assertEquals(parsedRequest.getMessage(), "Smile");
        assertEquals(parsedRequest.getTarget(), "me");
        assertEquals(parsedRequest.getWhen(), "Tuesdays");

        parsedRequest = parser.extract("me Smile Wednesdays");
        assertEquals(parsedRequest.getMessage(), "Smile");
        assertEquals(parsedRequest.getTarget(), "me");
        assertEquals(parsedRequest.getWhen(), "Wednesdays");

        parsedRequest = parser.extract("me Smile Thursdays");
        assertEquals(parsedRequest.getMessage(), "Smile");
        assertEquals(parsedRequest.getTarget(), "me");
        assertEquals(parsedRequest.getWhen(), "Thursdays");

        parsedRequest = parser.extract("me Smile Fridays");
        assertEquals(parsedRequest.getMessage(), "Smile");
        assertEquals(parsedRequest.getTarget(), "me");
        assertEquals(parsedRequest.getWhen(), "Fridays");

        parsedRequest = parser.extract("me Smile Saturdays");
        assertEquals(parsedRequest.getMessage(), "Smile");
        assertEquals(parsedRequest.getTarget(), "me");
        assertEquals(parsedRequest.getWhen(), "Saturdays");

        parsedRequest = parser.extract("me Smile Sundays");
        assertEquals(parsedRequest.getMessage(), "Smile");
        assertEquals(parsedRequest.getTarget(), "me");
        assertEquals(parsedRequest.getWhen(), "Sundays");

    }

    @Test
    public void outlierForms() throws Exception {

        ///remind
        ParsedRequest parsedRequest = parser.extract("@sam to book meeting room tomorrow at 4pm");
        assertEquals(parsedRequest.getMessage(), "to book meeting room");
        assertEquals(parsedRequest.getTarget(), "@sam");
        assertEquals(parsedRequest.getWhen(), "tomorrow at 4pm");

        parsedRequest = parser.extract("@sam to book meeting room everyday at 4pm");
        assertEquals(parsedRequest.getMessage(), "to book meeting room");
        assertEquals(parsedRequest.getTarget(), "@sam");
        assertEquals(parsedRequest.getWhen(), "everyday at 4pm");

        parsedRequest = parser.extract("@sam to book meeting room monday at 4pm");
        assertEquals(parsedRequest.getMessage(), "to book meeting room");
        assertEquals(parsedRequest.getTarget(), "@sam");
        assertEquals(parsedRequest.getWhen(), "monday at 4pm");

        parsedRequest = parser.extract("@sam to book meeting room tuesday at 4pm");
        assertEquals(parsedRequest.getMessage(), "to book meeting room");
        assertEquals(parsedRequest.getTarget(), "@sam");
        assertEquals(parsedRequest.getWhen(), "tuesday at 4pm");

        parsedRequest = parser.extract("@sam to book meeting room wednesday at 4pm");
        assertEquals(parsedRequest.getMessage(), "to book meeting room");
        assertEquals(parsedRequest.getTarget(), "@sam");
        assertEquals(parsedRequest.getWhen(), "wednesday at 4pm");

        parsedRequest = parser.extract("@sam to book meeting room thursday at 4pm");
        assertEquals(parsedRequest.getMessage(), "to book meeting room");
        assertEquals(parsedRequest.getTarget(), "@sam");
        assertEquals(parsedRequest.getWhen(), "thursday at 4pm");

        parsedRequest = parser.extract("@sam to book meeting room friday at 4pm");
        assertEquals(parsedRequest.getMessage(), "to book meeting room");
        assertEquals(parsedRequest.getTarget(), "@sam");
        assertEquals(parsedRequest.getWhen(), "friday at 4pm");

        parsedRequest = parser.extract("@sam to book meeting room saturday at 4pm");
        assertEquals(parsedRequest.getMessage(), "to book meeting room");
        assertEquals(parsedRequest.getTarget(), "@sam");
        assertEquals(parsedRequest.getWhen(), "saturday at 4pm");

        parsedRequest = parser.extract("@sam to book meeting room sunday at 4pm");
        assertEquals(parsedRequest.getMessage(), "to book meeting room");
        assertEquals(parsedRequest.getTarget(), "@sam");
        assertEquals(parsedRequest.getWhen(), "sunday at 4pm");

    }

}
