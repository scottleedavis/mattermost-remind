package scottleedavis.mattermost.remind.reminders;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjuster;
import java.time.temporal.TemporalAdjusters;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

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
        assertTrue(checkDate.equals(testDate) || checkDate.plusDays(1).equals(testDate));

        when = "at midnight";
        testDate = occurrence.calculate(when);
        checkDate = LocalDate.now().atTime(0, 0).truncatedTo(ChronoUnit.SECONDS);
        assertTrue(checkDate.equals(testDate) || checkDate.plusDays(1).equals(testDate));

        when = "at two";
        testDate = occurrence.calculate(when);
        checkDate = LocalDate.now().atTime(2, 0).truncatedTo(ChronoUnit.SECONDS);
        assertTrue(checkDate.equals(testDate) || checkDate.plusHours(12).equals(testDate) || checkDate.plusDays(1).equals(testDate));

        when = "at 7";
        testDate = occurrence.calculate(when);
        checkDate = LocalDate.now().atTime(7, 0).truncatedTo(ChronoUnit.SECONDS);
        assertTrue(checkDate.equals(testDate) || checkDate.plusHours(12).equals(testDate) || checkDate.plusDays(1).equals(testDate));

        when = "at 12:30pm";
        testDate = occurrence.calculate(when);
        checkDate = LocalDate.now().atTime(12, 30).truncatedTo(ChronoUnit.SECONDS);
        assertTrue(checkDate.equals(testDate) || checkDate.plusDays(1).equals(testDate));

        when = "at 7:12 pm";
        testDate = occurrence.calculate(when);
        checkDate = LocalDate.now().atTime(19, 12).truncatedTo(ChronoUnit.SECONDS);
        assertTrue(checkDate.equals(testDate) || checkDate.plusDays(1).equals(testDate));

        when = "at 8:05 PM";
        testDate = occurrence.calculate(when);
        checkDate = LocalDate.now().atTime(20, 5).truncatedTo(ChronoUnit.SECONDS);
        assertTrue(checkDate.equals(testDate) || checkDate.plusDays(1).equals(testDate));

        when = "at 9:52 am";
        testDate = occurrence.calculate(when);
        checkDate = LocalDate.now().atTime(9, 52).truncatedTo(ChronoUnit.SECONDS);
        assertTrue(checkDate.equals(testDate) || checkDate.plusDays(1).equals(testDate));

        when = "at 9:12";
        testDate = occurrence.calculate(when);
        checkDate = LocalDate.now().atTime(9, 12).truncatedTo(ChronoUnit.SECONDS);
        assertTrue(checkDate.equals(testDate) || checkDate.plusDays(1).equals(testDate));

        when = "at 17:15";
        testDate = occurrence.calculate(when);
        checkDate = LocalDate.now().atTime(17, 15).truncatedTo(ChronoUnit.SECONDS);
        assertTrue(checkDate.equals(testDate) || checkDate.plusDays(1).equals(testDate));

        when = "at 930am";
        testDate = occurrence.calculate(when);
        checkDate = LocalDate.now().atTime(9, 30).truncatedTo(ChronoUnit.SECONDS);
        assertTrue(checkDate.equals(testDate) || checkDate.plusDays(1).equals(testDate));

        when = "at 1230 am";
        testDate = occurrence.calculate(when);
        checkDate = LocalDate.now().atTime(0, 30).truncatedTo(ChronoUnit.SECONDS);
        assertTrue(checkDate.equals(testDate) || checkDate.plusDays(1).equals(testDate));

        when = "at 5PM";
        testDate = occurrence.calculate(when);
        checkDate = LocalDate.now().atTime(17, 0).truncatedTo(ChronoUnit.SECONDS);
        assertTrue(checkDate.equals(testDate) || checkDate.plusDays(1).equals(testDate));


        when = "at 4 am";
        testDate = occurrence.calculate(when);
        checkDate = LocalDate.now().atTime(4, 0).truncatedTo(ChronoUnit.SECONDS);
        assertTrue(checkDate.equals(testDate) || checkDate.plusDays(1).equals(testDate));

        when = "at 1400";
        testDate = occurrence.calculate(when);
        checkDate = LocalDate.now().atTime(14, 00).truncatedTo(ChronoUnit.SECONDS);
        assertTrue(checkDate.equals(testDate) || checkDate.plusDays(1).equals(testDate));

    }

    @Test
    public void calculateOn() throws Exception {
        String when;
        LocalDateTime testDate;
        LocalDateTime checkDate;

        when = "on Friday";
        testDate = occurrence.calculate(when);
        checkDate = LocalDate.now().with(TemporalAdjusters.next(DayOfWeek.FRIDAY)).atTime(9, 0);
        assertEquals(testDate, checkDate);

        when = "on mon";
        testDate = occurrence.calculate(when);
        checkDate = LocalDate.now().with(TemporalAdjusters.next(DayOfWeek.MONDAY)).atTime(9, 0);
        assertEquals(testDate, checkDate);

        when = "on WEDNEs";
        testDate = occurrence.calculate(when);
        checkDate = LocalDate.now().with(TemporalAdjusters.next(DayOfWeek.WEDNESDAY)).atTime(9, 0);
        assertEquals(testDate, checkDate);

        when = "on December 15";
        testDate = occurrence.calculate(when);
        checkDate = LocalDateTime.parse( "December 15 "+LocalDateTime.now().getYear()+" 09:00", new DateTimeFormatterBuilder()
                .parseCaseInsensitive().appendPattern("MMMM d yyyy HH:mm").toFormatter());
        assertTrue(checkDate.equals(testDate) || checkDate.plusYears(1).equals(testDate));

        when = "on jan 12";
        testDate = occurrence.calculate(when);
        checkDate = LocalDateTime.parse( "January 12 "+LocalDateTime.now().getYear()+" 09:00", new DateTimeFormatterBuilder()
                .parseCaseInsensitive().appendPattern("MMMM d yyyy HH:mm").toFormatter());
        assertTrue(checkDate.equals(testDate) || checkDate.plusYears(1).equals(testDate));

        when = "on July 12th";
        testDate = occurrence.calculate(when);
        checkDate = LocalDateTime.parse( "July 12 "+LocalDateTime.now().getYear()+" 09:00", new DateTimeFormatterBuilder()
                .parseCaseInsensitive().appendPattern("MMMM d yyyy HH:mm").toFormatter());
        assertTrue(checkDate.equals(testDate) || checkDate.plusYears(1).equals(testDate));

        when = "on March 22";
        testDate = occurrence.calculate(when);
        checkDate = LocalDateTime.parse( "March 22 "+LocalDateTime.now().getYear()+" 09:00", new DateTimeFormatterBuilder()
                .parseCaseInsensitive().appendPattern("MMMM d yyyy HH:mm").toFormatter());
        assertTrue(checkDate.equals(testDate) || checkDate.plusYears(1).equals(testDate));

        when = "on September 7th 2019";
        testDate = occurrence.calculate(when);
        checkDate = LocalDateTime.parse( "September 7 "+LocalDateTime.now().getYear()+" 09:00", new DateTimeFormatterBuilder()
                .parseCaseInsensitive().appendPattern("MMMM d yyyy HH:mm").toFormatter());
        assertTrue(checkDate.equals(testDate) || checkDate.plusYears(1).equals(testDate));

        when = "on April 17 2019";
        testDate = occurrence.calculate(when);
        checkDate = LocalDateTime.parse( "April 17 "+LocalDateTime.now().getYear()+" 09:00", new DateTimeFormatterBuilder()
                .parseCaseInsensitive().appendPattern("MMMM d yyyy HH:mm").toFormatter());
        assertTrue(checkDate.equals(testDate) || checkDate.plusYears(1).equals(testDate));

        when = "on auguSt tenth 2019";
        testDate = occurrence.calculate(when);
        checkDate = LocalDateTime.parse( "August 10 "+LocalDateTime.now().getYear()+" 09:00", new DateTimeFormatterBuilder()
                .parseCaseInsensitive().appendPattern("MMMM d yyyy HH:mm").toFormatter());
        assertTrue(checkDate.equals(testDate) || checkDate.plusYears(1).equals(testDate));

        //todo: on 7 (next 7th of month)
        when = "on 7";
        testDate = occurrence.calculate(when);
        checkDate = LocalDateTime.parse( LocalDate.now().getMonth().name() + " 7 "+LocalDateTime.now().getYear()+" 09:00", new DateTimeFormatterBuilder()
                .parseCaseInsensitive().appendPattern("MMMM d yyyy HH:mm").toFormatter());
        assertTrue(checkDate.equals(testDate) || checkDate.plusMonths(1).equals(testDate));

        //todo: on 7th
        when = "on 7th";
        testDate = occurrence.calculate(when);
        checkDate = LocalDateTime.parse( LocalDate.now().getMonth().name() + " 7 "+LocalDateTime.now().getYear()+" 09:00", new DateTimeFormatterBuilder()
                .parseCaseInsensitive().appendPattern("MMMM d yyyy HH:mm").toFormatter());
        assertTrue(checkDate.equals(testDate) || checkDate.plusMonths(1).equals(testDate));

        //todo: on seven
        when = "on seven";
        testDate = occurrence.calculate(when);
        checkDate = LocalDateTime.parse( LocalDate.now().getMonth().name() + " 7 "+LocalDateTime.now().getYear()+" 09:00", new DateTimeFormatterBuilder()
                .parseCaseInsensitive().appendPattern("MMMM d yyyy HH:mm").toFormatter());
        assertTrue(checkDate.equals(testDate) || checkDate.plusMonths(1).equals(testDate));


        //todo: on 12/17/18
        //todo: on 12/17


    }
}
