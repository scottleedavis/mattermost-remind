package io.github.scottleedavis.mattermost.remind.reminders;

import io.github.scottleedavis.mattermost.remind.messages.Response;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class SchedulerTests {

    @Autowired
    Scheduler scheduler;

    @Test
    public void setReminder() {

        String userName = "me";
        String userId = "@scottd";
        String channelName = "#Off-Topic";
        String payload;
        Response response;

        payload = "help";
        response = scheduler.setReminder(userName, payload, userId, channelName);
        assertTrue(response.getText().startsWith(":wave: Need some help"));

        payload = "list";
        response = scheduler.setReminder(userName, payload, userId, channelName);
        assertEquals(response.getText(), "I cannot find any reminders for you. Type `/remind` to set one.");

        payload = "version";
        response = scheduler.setReminder(userName, payload, userId, channelName);
        assertNotNull(response.getText());

        payload = "me test this service every other day at 11:00am";
        response = scheduler.setReminder(userName, payload, userId, channelName);
        assertEquals(response.getText(), ":thumbsup: I will remind you \"test this service\" at 11AM every other day");

    }
}
