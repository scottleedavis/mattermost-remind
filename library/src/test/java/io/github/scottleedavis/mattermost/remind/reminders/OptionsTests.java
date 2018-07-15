package io.github.scottleedavis.mattermost.remind.reminders;

import io.github.scottleedavis.mattermost.remind.messages.Action;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class OptionsTests {

    @Autowired
    private Options options;

    @Before
    public void setUp() {
        options.setAppUrl("http://foo/");
    }

    @Test
    public void setActions() {
        List<Action> actions = options.setActions(1L);
        assertTrue(actions.size() > 0);
        actions.forEach(action -> assertNotNull(action));
    }

    @Test
    public void listReminders() {
        String response = options.listReminders("FOO");
        assertEquals(response, "I cannot find any reminders for you. Type `/remind` to set one.");
    }

    @Test
    public void finishedActions() {
        List<Action> actions = options.finishedActions(1L, false);
        assertTrue(actions.size() == 7);

        actions = options.finishedActions(1L, true);
        assertTrue( actions.size() == 5);
    }
}
