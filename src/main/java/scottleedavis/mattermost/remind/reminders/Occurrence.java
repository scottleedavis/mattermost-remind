package scottleedavis.mattermost.remind.reminders;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import scottleedavis.mattermost.remind.exceptions.OccurrenceException;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Component
public class Occurrence {

    private String DEFAULT_TIME = "09:00";

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
        if (when.startsWith("in"))
            return OccurrenceType.IN;
        else if (when.startsWith("at"))
            return OccurrenceType.AT;
        else if (when.startsWith("on"))
            return OccurrenceType.ON;
        else if (when.startsWith("every"))
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
        LocalDateTime closest;

        String[] timeChunks = when.split(" ");
        if (timeChunks.length < 2)
            throw new OccurrenceException("unrecognized time mark.");

        String chronoUnit = Arrays.asList(timeChunks).stream().skip(1).collect(Collectors.joining(" "));
        switch (chronoUnit) {
            case "noon":
                closest = LocalDate.now().atTime(12, 0);
                return Arrays.asList(chooseClosest(closest, now, true));
            case "midnight":
                closest = LocalDate.now().atTime(0, 0);
                return Arrays.asList(chooseClosest(closest, now, true));
            case "one":
            case "two":
            case "three":
            case "four":
            case "five":
            case "six":
            case "seven":
            case "eight":
            case "nine":
            case "ten":
            case "eleven":
            case "twelve":
                closest = LocalDate.now().atTime(formatter.wordToNumber(chronoUnit), 0);
                return Arrays.asList(chooseClosest(closest, now, false));
            case "0":
            case "1":
            case "2":
            case "3":
            case "4":
            case "5":
            case "6":
            case "7":
            case "8":
            case "9":
            case "10":
            case "11":
            case "12":
            case "13":
            case "14":
            case "15":
            case "16":
            case "17":
            case "18":
            case "19":
            case "20":
            case "21":
            case "22":
            case "23":
                closest = LocalDate.now().atTime(Integer.parseInt(chronoUnit), 0);
                return Arrays.asList(chooseClosest(closest, now, false));
            default:
                break;
        }

        if (Pattern.compile("(1[012]|[1-9]):[0-5][0-9](\\s)?(?i)(am|pm)",  // 12:30PM, 12:30 pm
                Pattern.CASE_INSENSITIVE).matcher(chronoUnit).find()) {
            int amPmOffset = (chronoUnit.charAt(chronoUnit.length() - 3) == ' ') ? 3 : 2;
            String amPm = chronoUnit.substring(chronoUnit.length() - amPmOffset).trim();
            int[] time = Arrays.stream(chronoUnit.substring(0, chronoUnit.length() - amPmOffset).split(":"))
                    .mapToInt(Integer::parseInt).toArray();

            time[0] = amPm.toLowerCase().equals("pm") ? (time[0] < 12 ? ((time[0] + 12) % 24) : time[0]) : time[0] % 12;
            closest = LocalDate.now().atTime(time[0], time[1]);
            return Arrays.asList(chooseClosest(closest, now, true));

        } else if (Pattern.compile("(1[012]|[1-9]):[0-5][0-9]", // 12:30
                Pattern.CASE_INSENSITIVE).matcher(chronoUnit).find()) {
            int[] time = Arrays.stream(chronoUnit.split(":")).mapToInt(Integer::parseInt).toArray();
            time[0] = time[0] % 24;
            closest = LocalDate.now().atTime(time[0], time[1]);
            return Arrays.asList(chooseClosest(closest, now, true));

        } else if (Pattern.compile("(1[012]|[1-9])[0-5][0-9](\\s)?(?i)(am|pm)", // 1230pm, 1230 pm
                Pattern.CASE_INSENSITIVE).matcher(chronoUnit).find()) {
            int amPmOffset = (chronoUnit.charAt(chronoUnit.length() - 3) == ' ') ? 3 : 2;
            String amPm = chronoUnit.substring(chronoUnit.length() - amPmOffset).trim();
            String subChronoUnit = chronoUnit.substring(0, chronoUnit.length() - amPmOffset);
            subChronoUnit = String.format("%4s", subChronoUnit).replace(' ', '0');
            String[] parts = {subChronoUnit.substring(0, 2), subChronoUnit.substring(2)};
            int[] time = Arrays.stream(parts).mapToInt(Integer::parseInt).toArray();

            time[0] = amPm.equalsIgnoreCase("pm") ? (time[0] < 12 ? ((time[0] + 12) % 24) : time[0]) : time[0] % 12;
            closest = LocalDate.now().atTime(time[0], time[1]);
            return Arrays.asList(chooseClosest(closest, now, true));

        } else if (Pattern.compile("(1[012]|[1-9])(\\s)?(?i)(am|pm)",  // 5PM, 7 am
                Pattern.CASE_INSENSITIVE).matcher(chronoUnit).find()) {
            int amPmOffset = (chronoUnit.charAt(chronoUnit.length() - 3) == ' ') ? 3 : 2;
            String amPm = chronoUnit.substring(chronoUnit.length() - amPmOffset).trim();
            String subChronoUnit = chronoUnit.substring(0, chronoUnit.length() - amPmOffset);
            int time = Integer.parseInt(subChronoUnit);
            time = amPm.equalsIgnoreCase("pm") ? (time < 12 ? ((time + 12) % 24) : time) : time % 12;
            closest = LocalDate.now().atTime(time, 0);
            return Arrays.asList(chooseClosest(closest, now, true));

        } else if (Pattern.compile("(1[012]|[1-9])[0-5][0-9]",  // 1200
                Pattern.CASE_INSENSITIVE).matcher(chronoUnit).find()) {
            String[] parts = {chronoUnit.substring(0, 2), chronoUnit.substring(2)};
            int[] time = Arrays.stream(parts).mapToInt(Integer::parseInt).toArray();
            time[0] = time[0] % 24;
            closest = LocalDate.now().atTime(time[0], time[1]);
            return Arrays.asList(chooseClosest(closest, now, true));

        }

        throw new OccurrenceException("time mark not recognized");
    }

    private List<LocalDateTime> on(String when) throws Exception {

        String[] timeChunks = when.split(" ");
        if (timeChunks.length < 2)
            throw new OccurrenceException("unrecognized time mark.");

        //todo ensure this works with on <day|date> at <time>

        String chronoUnit = Arrays.asList(timeChunks).stream().skip(1).collect(Collectors.joining(" "));
        chronoUnit = formatter.normalizeDate(chronoUnit);

        switch (chronoUnit) {
            case "MONDAY":
            case "TUESDAY":
            case "WEDNESDAY":
            case "THURSDAY":
            case "FRIDAY":
            case "SATURDAY":
            case "SUNDAY":
                return Arrays.asList(LocalDate.now().with(TemporalAdjusters.next(DayOfWeek.valueOf(chronoUnit))).atTime(9, 0));
            default:
                break;

        }

        return Arrays.asList(LocalDateTime.parse(chronoUnit + " "+DEFAULT_TIME, new DateTimeFormatterBuilder()
                .parseCaseInsensitive().appendPattern("MMMM d yyyy HH:mm").toFormatter()));

    }

    private List<LocalDateTime> every(String when) throws Exception {

        String[] timeChunks = when.split(" ");
        if (timeChunks.length < 2)
            throw new OccurrenceException("unrecognized time mark.");
        String chronoUnit = Arrays.asList(timeChunks).stream().skip(1).collect(Collectors.joining(" "));

        boolean everyOther = chronoUnit.contains("other");
        if( everyOther )
            chronoUnit = chronoUnit.split("other")[1].trim();

        String[] dateTimeSplit = chronoUnit.split(" at ");
        if(dateTimeSplit.length == 1)
            dateTimeSplit = new String[]{dateTimeSplit[0], DEFAULT_TIME };

        String [] chronoChunks = new String[0];
        boolean multiDay = dateTimeSplit[0].matches(".*(and|,)*");
        if (multiDay)
            chronoChunks = Arrays.stream(dateTimeSplit[0].split("and|,")).map(s -> s.trim()).toArray(String[]::new);

        List<LocalDateTime> ldts = new ArrayList<>();
        List<Exception> caughtExceptions = new ArrayList<>();
        Arrays.stream(chronoChunks).forEach( chrono -> {
            try {
                String dateUnit = formatter.normalizeDate(chrono);
//                String timeUnit =

            } catch (Exception e) {
                caughtExceptions.add(e);
            }
        });
        if( caughtExceptions.size() > 0 )
            throw new OccurrenceException("error normalizing date");

        //todo every Thursday
        //todo every day
        //todo every 12/18
        //todo every January 25
        //todo every monday and wednesday
        //todo every wednesday, thursday
        //todo every other Wednesday
        //todo every other friday and saturday
        //todo every day at 11:32am
        //todo every Monday at 9am
        //todo every 7/20 at 1100
        //todo every monday, tuesday and sunday at 11:00
        //todo every monday, tuesday at 2pm


        String foo = "bar";

        return null;
    }

    private List<LocalDateTime> freeForm(String when) throws Exception {
        throw new OccurrenceException("unrecognized time mark.");
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

}
