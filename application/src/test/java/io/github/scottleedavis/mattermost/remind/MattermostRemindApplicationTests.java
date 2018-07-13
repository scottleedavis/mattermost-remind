package io.github.scottleedavis.mattermost.remind;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertNotNull;

@RunWith(SpringRunner.class)
@SpringBootTest
public class MattermostRemindApplicationTests {

    @Test
    public void contextLoads() {
        assertNotNull(this);
    }

}
