package io.github.scottleedavis.mattermost.remind.reminders;

import io.github.scottleedavis.mattermost.remind.exceptions.OccurrenceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Component
public class Occurrence {

    public static String DEFAULT_TIME = "09:00";

    @Autowired
    private Formatter formatter;

    public List<LocalDateTime> calculate(String when) throws Exception {
        switch (classify(when)) {
            case IN:
                return in(when);
            case AT:
                return at(when);
            case ON:
                return on(when);
            case EVERY:
                return every(when);
            default:
                return freeForm(when);
        }
    }

    public OccurrenceType classify(String when) {
        if (when.startsWith("in "))
            return OccurrenceType.IN;
        else if (when.startsWith("at "))
            return OccurrenceType.AT;
        else if (when.startsWith("on "))
            return OccurrenceType.ON;
        else if (when.startsWith("every "))
            return OccurrenceType.EVERY;
        else
            return OccurrenceType.FREEFORM;
    }

    private List<LocalDateTime> in(String when) throws Exception {

        LocalDateTime date;

        String[] timeChunks = when.split(" ");
        if (timeChunks.length != 3)
            throw new OccurrenceException("unrecognized time mark.");

        Integer count;
        try {
            count = Integer.parseInt(timeChunks[1]);
        } catch (NumberFormatException e) {
            count = formatter.wordToNumber(timeChunks[1]);
        }

        String chronoUnit = timeChunks[2].toLowerCase();
        switch (chronoUnit) {
            case "seconds":
            case "second":
            case "sec":
            case "s":
                date = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS).plusSeconds(count);
                break;
            case "minutes":
            case "minute":
            case "min":
                date = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS).plusMinutes(count);
                break;
            case "hours":
            case "hour":
            case "hrs":
            case "hr":
                date = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS).plusHours(count);
                break;
            case "days":
            case "day":
            case "d":
                date = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS).plusDays(count);
                break;
            case "weeks":
            case "week":
            case "wks":
            case "wk":
                date = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS).plusWeeks(count);
                break;
            case "months":
            case "month":
            case "m":
                date = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS).plusMonths(count);
                break;
            case "years":
            case "year":
            case "y":
                date = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS).plusYears(count);
                break;
            default:
                throw new OccurrenceException("Unrecognized time specification");
        }
        return Arrays.asList(date);

    }

    private List<LocalDateTime> at(String when) throws Exception {

        LocalDateTime now = LocalDateTime.now();
        List<LocalDateTime> recurrentDates = new ArrayList<>();
        LocalDateTime closest;
        int[] time;

        String[] timeChunks = when.split(" ");
        if (timeChunks.length < 2)
            throw new OccurrenceException("unrecognized time mark.");

        String[] dateTimeSplit = Arrays.asList(timeChunks).stream().skip(1).collect(Collectors.joining(" ")).split(" every ");
        final String chronoUnit = formatter.normalizeTime(dateTimeSplit[0]);
        if (dateTimeSplit.length == 2)
            recurrentDates = every("every " + formatter.normalizeDate(dateTimeSplit[1]));

        switch (dateTimeSplit[0]) {
            case "noon":
                if (recurrentDates.size() > 0) {
                    return recurrentDates.stream().map(ldt_rd -> chooseClosest(ldt_rd.toLocalDate().atTime(12, 0), now, true)).collect(Collectors.toList());
                }
                closest = LocalDate.now().atTime(12, 0);
                return Arrays.asList(chooseClosest(closest, now, true));
            case "midnight":
                if (recurrentDates.size() > 0) {
                    return recurrentDates.stream().map(ldt_rd -> chooseClosest(ldt_rd.toLocalDate().atTime(0, 0), now, true)).collect(Collectors.toList());
                }
                closest = LocalDate.now().atTime(0, 0);
                return Arrays.asList(chooseClosest(closest, now, true));
        }

        time = Arrays.stream(chronoUnit.split(":")).mapToInt(Integer::parseInt).toArray();
        final boolean dayInterval = isDayInterval(dateTimeSplit[0]);
        time[0] = time[0] % 24;
        if (recurrentDates.size() > 0) {
            return recurrentDates.stream().map(ldt_rd -> chooseClosest(ldt_rd.toLocalDate().atTime(time[0], time[1]), now, dayInterval)).collect(Collectors.toList());
        }
        closest = LocalDate.now().atTime(time[0], time[1]);
        return Arrays.asList(chooseClosest(closest, now, dayInterval));

    }

    private List<LocalDateTime> on(String when) throws Exception {

        String[] timeChunks = when.split(" ");
        if (timeChunks.length < 2)
            throw new OccurrenceException("unrecognized time mark.");

        String chronoUnit = Arrays.asList(timeChunks).stream().skip(1).collect(Collectors.joining(" "));
        String[] dateTimeSplit = chronoUnit.split(" at ");
        final String time = dateTimeSplit.length == 1 ? DEFAULT_TIME : dateTimeSplit[1];

        String dateUnit = formatter.normalizeDate(dateTimeSplit[0]);
        String timeUnit = formatter.normalizeTime(time);

        switch (dateUnit) {
            case "MONDAY":
            case "TUESDAY":
            case "WEDNESDAY":
            case "THURSDAY":
            case "FRIDAY":
            case "SATURDAY":
            case "SUNDAY":
                return Arrays.asList(LocalDate.now().with(TemporalAdjusters.next(DayOfWeek.valueOf(dateUnit))).atTime(LocalTime.parse(timeUnit)));
            case "MONDAYS":
            case "TUESDAYS":
            case "WEDNESDAYS":
            case "THURSDAYS":
            case "FRIDAYS":
            case "SATURDAYS":
            case "SUNDAYS":
                return every("every " + dateUnit.substring(0, dateUnit.length() - 1) + " at " + timeUnit);
            default:
                break;
        }

        return Arrays.asList(LocalDateTime.parse(dateUnit + " " + timeUnit, new DateTimeFormatterBuilder()
                .parseCaseInsensitive().appendPattern("MMMM d yyyy HH:mm").toFormatter()));

    }

    private List<LocalDateTime> every(String when) throws Exception {


        String[] timeChunks = when.split(" ");
        if (timeChunks.length < 2)
            throw new OccurrenceException("unrecognized time mark.");

        String chronoUnit = Arrays.asList(timeChunks).stream().skip(1).collect(Collectors.joining(" "));
        boolean everyOther = chronoUnit.contains("other");
        chronoUnit = everyOther ? chronoUnit.split("other")[1].trim() : chronoUnit;
        String[] dateTimeSplit = chronoUnit.split(" at ");
        final String time = dateTimeSplit.length == 1 ? DEFAULT_TIME : dateTimeSplit[1];
        List<LocalDateTime> ldts = new ArrayList<>();
        List<Exception> caughtExceptions = new ArrayList<>();
        Arrays.stream(dateTimeSplit[0].split("and|,")).map(s -> s.trim()).forEach(chrono -> {
            try {
                String timeUnit = formatter.normalizeTime(time);
                String dateUnit = formatter.normalizeDate(chrono);
                LocalDate ld;
                LocalDateTime ldt;
                switch (dateUnit) {
                    case "DAY":
                        ld = LocalDate.now().plusDays(everyOther ? 2 : 1);
                        dateUnit = ld.getMonth().name().toUpperCase() + " " + ld.getDayOfMonth() + " " + ld.getYear();
                        ldt = LocalDateTime.parse(dateUnit + " " + timeUnit, new DateTimeFormatterBuilder()
                                .parseCaseInsensitive().appendPattern("MMMM d yyyy HH:mm").toFormatter());
                        break;
                    case "MONDAY":
                    case "TUESDAY":
                    case "WEDNESDAY":
                    case "THURSDAY":
                    case "FRIDAY":
                    case "SATURDAY":
                    case "SUNDAY":
                        ld = LocalDate.now().with(TemporalAdjusters.next(DayOfWeek.valueOf(dateUnit))).plusWeeks(everyOther ? 1 : 0);
                        dateUnit = ld.getMonth().name().toUpperCase() + " " + ld.getDayOfMonth() + " " + ld.getYear();
                        ldt = LocalDateTime.parse(dateUnit + " " + timeUnit, new DateTimeFormatterBuilder()
                                .parseCaseInsensitive().appendPattern("MMMM d yyyy HH:mm").toFormatter());
                        break;
                    default:
                        ldt = LocalDateTime.parse(dateUnit + " " + timeUnit, new DateTimeFormatterBuilder()
                                .parseCaseInsensitive().appendPattern("MMMM d yyyy HH:mm").toFormatter()).plusYears(everyOther ? 1 : 0);
                        break;
                }

                ldts.add(ldt);

            } catch (Exception e) {
                caughtExceptions.add(e);
            }
        });
        if (caughtExceptions.size() > 0)
            throw new OccurrenceException("error normalizing date");

        return ldts;
    }

    private List<LocalDateTime> freeForm(String when) throws Exception {

        String[] dateTimeSplit = when.split(" at ");
        final String time = dateTimeSplit.length == 1 ? DEFAULT_TIME : dateTimeSplit[1];
        String dateUnit = formatter.normalizeDate(dateTimeSplit[0]);
        String timeUnit = formatter.normalizeTime(time);

        switch (dateUnit) {
            case "TODAY":
                return at("at " + time);
            case "TOMORROW":
                return on("on " + LocalDate.now().plusDays(1).getDayOfWeek().toString() + " at " + timeUnit);
            case "EVERYDAY":
                return every("every day at " + timeUnit);
            case "MONDAYS":
            case "TUESDAYS":
            case "WEDNESDAYS":
            case "THURSDAYS":
            case "FRIDAYS":
            case "SATURDAYS":
            case "SUNDAYS":
                return every("every " + dateUnit.substring(0, when.length() - 1) + " at " + timeUnit);
            case "MONDAY":
            case "TUESDAY":
            case "WEDNESDAY":
            case "THURSDAY":
            case "FRIDAY":
            case "SATURDAY":
            case "SUNDAY":
            default:
                return on("on " + dateUnit + " at " + timeUnit);

        }

    }

    private LocalDateTime chooseClosest(LocalDateTime closest, LocalDateTime now, boolean dayInterval) {
        if (dayInterval) {
            if (closest.isBefore(now))
                return closest.plusDays(1);
            else
                return closest;
        } else {
            if (closest.isBefore(now)) {
                if (closest.plusHours(12).isBefore(now))
                    return closest.plusHours(24);
                else
                    return closest.plusHours(12);
            } else
                return closest;
        }

    }

    private boolean isDayInterval(String dateTime) {

        if (Pattern.compile("(am|pm)",  // 12:30PM, 12:30 pm
                Pattern.CASE_INSENSITIVE).matcher(dateTime).find())
            return true;

        if (Pattern.compile("(1[012]|[1-9])[0-5][0-9]",  // 1200
                Pattern.CASE_INSENSITIVE).matcher(dateTime).find()) {
            return true;
        }

        return false;
    }

}
