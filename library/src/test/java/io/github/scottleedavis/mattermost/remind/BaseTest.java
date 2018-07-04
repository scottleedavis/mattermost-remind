package io.github.scottleedavis.mattermost.remind;

import static org.junit.Assert.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
@SpringBootApplication(scanBasePackages = {"io.github.scottleedavis.mattermost.remind"})
public class BaseTest {

    @Test
    public void sanityCheck() {
        //sanity check
        assertTrue(true);
    }

    public static void main(String[] args) {
        SpringApplication.run(BaseTest.class, args);
    }

}
