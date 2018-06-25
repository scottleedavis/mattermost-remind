package scottleedavis.mattermost.remind.reminders;

import static org.junit.Assert.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDate;
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

    @Test
    public void calculateAt() throws Exception {
        String when;
        LocalDateTime testDate;
        LocalDateTime checkDate;

        when = "at noon";
        testDate = occurrence.calculate(when);
        checkDate = LocalDate.now().atTime(12, 0).truncatedTo(ChronoUnit.SECONDS);
        assertTrue(checkDate.equals(testDate) || checkDate.plusDays(1).equals(testDate) );

        when = "at midnight";
        testDate = occurrence.calculate(when);
        checkDate = LocalDate.now().atTime(0, 0).truncatedTo(ChronoUnit.SECONDS);
        assertTrue(checkDate.equals(testDate) || checkDate.plusDays(1).equals(testDate) );

        // todo: at two
        // todo: at 7
        // todo: at 12:30pm
        // todo: at 1230am
        // todo: at 1400
    }
}
