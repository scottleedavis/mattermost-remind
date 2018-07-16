package io.github.scottleedavis.mattermost.remind.messages;

import static org.junit.Assert.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class AllMessagesTests {

    @Test
    public void action() {
        assertNotNull(new Action());
    }

    @Test
    public void attachment() {
        assertNotNull(new Attachment());
    }

    @Test
    public void context() {
        assertNotNull(new Context());
    }

    @Test
    public void integration() {
        assertNotNull(new Integration());
    }

    @Test
    public void interaction() {
        assertNotNull(new Interaction());
    }

    @Test
    public void parsedRequest() {
        assertNotNull(new ParsedRequest());
    }

    @Test
    public void response() {
        assertNotNull(new Response());
    }

    @Test
    public void update() {
        assertNotNull(new Update());
    }

    @Test
    public void updateResponse() {
        assertNotNull(new UpdateResponse());
    }

}
