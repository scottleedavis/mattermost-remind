package io.github.scottleedavis.mattermost.remind.io;

import io.github.scottleedavis.mattermost.remind.messages.Response;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertTrue;

@RunWith(SpringRunner.class)
@SpringBootTest
public class SlashCommandTests {

    @Autowired
    private SlashCommand slashCommand;

    @Test
    public void remind() throws Exception {
        Response response = slashCommand.remind("fake_token", "", "", "", "", "", "", "", "", "", "", "", "");
        assertTrue(response.getText().equals("Sorry, I didn’t quite get that. I’m easily confused. Perhaps try the words in a different order? This usually works: `/remind [@someone or ~channel] [what] [when]`.\n"));
    }
}
