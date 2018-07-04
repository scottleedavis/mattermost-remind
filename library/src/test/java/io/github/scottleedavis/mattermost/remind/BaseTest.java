package io.github.scottleedavis.mattermost.remind;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertNotNull;

@RunWith(SpringRunner.class)
@SpringBootTest
@SpringBootApplication(scanBasePackages = {"io.github.scottleedavis.mattermost.remind"})
public class BaseTest {

    @Test
    public void sanityCheck() {
        //sanity check
        assertNotNull(BaseTest.class);
    }

    public static void main(String[] args) {
        SpringApplication.run(BaseTest.class, args);
    }

}
