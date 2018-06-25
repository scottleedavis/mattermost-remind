package scottleedavis.mattermost.remind.reminders;

import static org.junit.Assert.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import sun.util.resources.LocaleData;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@RunWith(SpringRunner.class)
@SpringBootTest
public class OccurrenceTests {

    @Autowired
    Occurrence occurrence;

    @Test
    public void calcuateIn() throws Exception {

        String when;
        LocalDateTime testDate;
        LocalDateTime checkDate;

        when = "in one second";
        testDate = occurrence.calculate(when);
        checkDate = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);
        assertEquals(checkDate.plusSeconds(1).toString(), testDate.toString());

        when = "in 712 minutes";
        testDate = occurrence.calculate(when);
        checkDate = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);
        assertEquals(checkDate.plusMinutes(712).toString(), testDate.toString());

        when = "in three hours";
        testDate = occurrence.calculate(when);
        checkDate = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);
        assertEquals(checkDate.plusHours(3).toString(), testDate.toString());

        when = "in 2 days";
        testDate = occurrence.calculate(when);
        checkDate = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);
        assertEquals(checkDate.plusDays(2).toString(), testDate.toString());

        when = "in ninety weeks";
        testDate = occurrence.calculate(when);
        checkDate = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);
        assertEquals(checkDate.plusWeeks(90).toString(), testDate.toString());

        when = "in 4 months";
        testDate = occurrence.calculate(when);
        checkDate = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);
        assertEquals(checkDate.plusMonths(4).toString(), testDate.toString());

        when = "in one year";
        testDate = occurrence.calculate(when);
        checkDate = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);
        assertEquals(checkDate.plusYears(1).toString(), testDate.toString());


    }
}
