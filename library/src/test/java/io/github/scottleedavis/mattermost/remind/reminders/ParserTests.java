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
    public void extract() throws Exception {

        ParsedRequest parsedRequest = parser.extract("me FooBar at noon");
        assertEquals(parsedRequest.getMessage(), "FooBar");
        assertEquals(parsedRequest.getTarget(), "me");
        assertEquals(parsedRequest.getWhen(), "at noon");

        parsedRequest = parser.extract("list");
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

        parsedRequest = parser.extract("@anybody FooBar at noon");
        assertEquals(parsedRequest.getMessage(), "FooBar");
        assertEquals(parsedRequest.getTarget(), "@anybody");
        assertEquals(parsedRequest.getWhen(), "at noon");

        parsedRequest = parser.extract("#Off-Topic FooBar at noon");
        assertEquals(parsedRequest.getMessage(), "FooBar");
        assertEquals(parsedRequest.getTarget(), "#Off-Topic");
        assertEquals(parsedRequest.getWhen(), "at noon");

        parsedRequest = parser.extract("me \"I'd really like to do it at at one every\" at noon");
        assertEquals(parsedRequest.getMessage(), "I'd really like to do it at at one every");
        assertEquals(parsedRequest.getTarget(), "me");
        assertEquals(parsedRequest.getWhen(), "at noon");

        parsedRequest = parser.extract("me \"Joe joes and \" joe \"is a joe|'|'\" joe joe\" at noon");
        assertEquals(parsedRequest.getMessage(), "Joe joes and \" joe \"is a joe|'|'\" joe joe");
        assertEquals(parsedRequest.getTarget(), "me");
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

    }

}