package io.github.scottleedavis.mattermost.remind.exceptions;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertNotNull;

@RunWith(SpringRunner.class)
@SpringBootTest
public class AllExceptionsTests {

    @Test
    public void formatterException() {
        assertNotNull(new FormatterException("foo"));
    }

    @Test
    public void occurrenceException() {
        assertNotNull(new OccurrenceException("foo"));
    }

    @Test
    public void parserException() {
        assertNotNull(new ParserException("foo"));
    }

    @Test
    public void reminderException() {
        assertNotNull(new ReminderException("foo"));
    }

    @Test
    public void reminderServiceException() {
        assertNotNull(new ReminderServiceException("foo"));
    }

    @Test
    public void tokenException() {
        assertNotNull(new TokenException("foo"));
    }

    @Test
    public void webhookException() { assertNotNull(new WebhookException("fooo", new Throwable())); }

}
