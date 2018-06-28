package scottleedavis.mattermost.remind.reminders;

import org.springframework.stereotype.Component;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Component
public class Occurrence {

    public LocalDateTime calculate(String when) throws Exception {
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

    private LocalDateTime in(String when) throws Exception {

        LocalDateTime date;

        String[] timeChunks = when.split(" ");
        if (timeChunks.length != 3)
            throw new Exception("unrecognized time mark.");

        Integer count;
        try {
            count = Integer.parseInt(timeChunks[1]);
        } catch (NumberFormatException e) {
            count = wordToNumber(timeChunks[1]);
        }

        String chronoUnit = timeChunks[2].toLowerCase();
        switch (chronoUnit) {
            case "seconds":
            case "second":
                date = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS).plusSeconds(count);
                break;
            case "minutes":
            case "minute":
                date = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS).plusMinutes(count);
                break;
            case "hours":
            case "hour":
                date = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS).plusHours(count);
                break;
            case "days":
            case "day":
                date = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS).plusDays(count);
                break;
            case "weeks":
            case "week":
                date = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS).plusWeeks(count);
                break;
            case "months":
            case "month":
                date = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS).plusMonths(count);
                break;
            case "years":
            case "year":
                date = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS).plusYears(count);
                break;
            default:
                throw new Exception("Unrecognized time specification");
        }
        return date;

    }

    private LocalDateTime at(String when) throws Exception {

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime closest;

        String[] timeChunks = when.split(" ");
        if (timeChunks.length < 2)
            throw new Exception("unrecognized time mark.");

        String chronoUnit = Arrays.asList(timeChunks).stream().skip(1).collect(Collectors.joining(" "));
        switch (chronoUnit) {
            case "noon":
                closest = LocalDate.now().atTime(12, 0);
                return chooseClosest(closest, now, true);
            case "midnight":
                closest = LocalDate.now().atTime(0, 0);
                return chooseClosest(closest, now, true);
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
                closest = LocalDate.now().atTime(wordToNumber(chronoUnit), 0);
                return chooseClosest(closest, now, false);
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
                return chooseClosest(closest, now, false);
            default:
                break;
        }

        // 12:30PM, 12:30 pm
        Matcher match = Pattern.compile("(1[012]|[1-9]):[0-5][0-9](\\s)?(?i)(am|pm)",
                Pattern.CASE_INSENSITIVE).matcher(chronoUnit);
        if (match.find()) {
            int amPmOffset = (chronoUnit.charAt(chronoUnit.length() - 3) == ' ') ? 3 : 2;
            String amPm = chronoUnit.substring(chronoUnit.length() - amPmOffset).trim();
            int[] time = Arrays.stream(chronoUnit.substring(0, chronoUnit.length() - amPmOffset).split(":"))
                    .mapToInt(Integer::parseInt).toArray();

            time[0] = amPm.toLowerCase().equals("pm") ? (time[0] < 12 ? ((time[0] + 12) % 24) : time[0]) : time[0] % 12;
            closest = LocalDate.now().atTime(time[0], time[1]);
            return chooseClosest(closest, now, true);
        }
        // 12:30
        match = Pattern.compile("(1[012]|[1-9]):[0-5][0-9]",
                Pattern.CASE_INSENSITIVE).matcher(chronoUnit);
        if (match.find()) {
            int[] time = Arrays.stream(chronoUnit.split(":")).mapToInt(Integer::parseInt).toArray();
            time[0] = time[0] % 24;
            closest = LocalDate.now().atTime(time[0], time[1]);
            return chooseClosest(closest, now, true);
        }
        // 1230pm, 1230 pm
        match = Pattern.compile("(1[012]|[1-9])[0-5][0-9](\\s)?(?i)(am|pm)",
                Pattern.CASE_INSENSITIVE).matcher(chronoUnit);
        if (match.find()) {
            int amPmOffset = (chronoUnit.charAt(chronoUnit.length() - 3) == ' ') ? 3 : 2;
            String amPm = chronoUnit.substring(chronoUnit.length() - amPmOffset).trim();
            String subChronoUnit = chronoUnit.substring(0, chronoUnit.length() - amPmOffset);
            subChronoUnit = String.format("%4s", subChronoUnit).replace(' ', '0');
            String[] parts = {subChronoUnit.substring(0, 2), subChronoUnit.substring(2)};
            int[] time = Arrays.stream(parts).mapToInt(Integer::parseInt).toArray();

            time[0] = amPm.toLowerCase().equals("pm") ? (time[0] < 12 ? ((time[0] + 12) % 24) : time[0]) : time[0] % 12;
            closest = LocalDate.now().atTime(time[0], time[1]);
            return chooseClosest(closest, now, true);
        }
        // 5PM, 7 am
        match = Pattern.compile("(1[012]|[1-9])(\\s)?(?i)(am|pm)",
                Pattern.CASE_INSENSITIVE).matcher(chronoUnit);
        if (match.find()) {
            int amPmOffset = (chronoUnit.charAt(chronoUnit.length() - 3) == ' ') ? 3 : 2;
            String amPm = chronoUnit.substring(chronoUnit.length() - amPmOffset).trim();
            String subChronoUnit = chronoUnit.substring(0, chronoUnit.length() - amPmOffset);
            int time = Integer.parseInt(subChronoUnit);
            time = amPm.toLowerCase().equals("pm") ? (time < 12 ? ((time + 12) % 24) : time) : time % 12;
            closest = LocalDate.now().atTime(time, 0);
            return chooseClosest(closest, now, true);
        }
        // 1200
        match = Pattern.compile("(1[012]|[1-9])[0-5][0-9]",
                Pattern.CASE_INSENSITIVE).matcher(chronoUnit);
        if (match.find()) {
            String[] parts = {chronoUnit.substring(0, 2), chronoUnit.substring(2)};
            int[] time = Arrays.stream(parts).mapToInt(Integer::parseInt).toArray();
            time[0] = time[0] % 24;
            closest = LocalDate.now().atTime(time[0], time[1]);
            return chooseClosest(closest, now, true);
        }

        throw new Exception("time mark not recognized");
    }

    private LocalDateTime on(String when) throws Exception {

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime closest;

        String[] timeChunks = when.split(" ");
        if (timeChunks.length < 2)
            throw new Exception("unrecognized time mark.");

        String chronoUnit = Arrays.asList(timeChunks).stream().skip(1).collect(Collectors.joining(" ")).toUpperCase();
        // TODO do a better search here with regex.
        // "((mon|tues|wed(nes)?|thur(s)?|fri|sat(ur)?|sun)(day)?)"
        // TODO, ensure day select automatically selects 9AM
        switch(chronoUnit) {
            case "MONDAY":
            case "TUESDAY":
            case "WEDNESDAY":
            case "THURSDAY":
            case "FRIDAY":
            case "SATURDAY":
            case "SUNDAY":
                DayOfWeek today = LocalDate.now().getDayOfWeek();
                DayOfWeek chosen = DayOfWeek.valueOf(chronoUnit);
                if ( chosen.ordinal() > today.ordinal() ) {
                    long delta_days = chosen.ordinal() - today.ordinal();
                    return now.plusDays(delta_days);
                } else {
                    long delta_days = chosen.ordinal() - today.ordinal() + 7;
                    return now.plusDays(delta_days);
                }
            default:
                break;

        }
        //todo: on Friday
        //todo: on December 15
        //todo: on jan 12
        //todo: on July 12th
        //todo: on July 12
        //todo: on July 12th 2019
        //todo: on July 12 2019
        //todo: on 7 (next 7th of month)
        //todo: on 12/17/18
        //todo: on 12/17



        throw new Exception("time mark not recognized");
    }

    private LocalDateTime every(String when) throws Exception {
        throw new Exception("not yet supported");
    }

    private LocalDateTime freeForm(String when) throws Exception {
        throw new Exception("unrecognized time mark.");
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

    private static HashMap<String, Integer> numbers = new HashMap<String, Integer>();
    private static HashMap<String, Integer> onumbers = new HashMap<String, Integer>();
    private static HashMap<String, Integer> tnumbers = new HashMap<String, Integer>();

    static {
        numbers.put("zero", 0);
        numbers.put("one", 1);
        numbers.put("two", 2);
        numbers.put("three", 3);
        numbers.put("four", 4);
        numbers.put("five", 5);
        numbers.put("six", 6);
        numbers.put("seven", 7);
        numbers.put("eight", 8);
        numbers.put("nine", 9);
        numbers.put("ten", 10);
        numbers.put("eleven", 11);
        numbers.put("twelve", 12);
        numbers.put("thirteen", 13);
        numbers.put("fourteen", 14);
        numbers.put("fifteen", 15);
        numbers.put("sixteen", 16);
        numbers.put("seventeen", 17);
        numbers.put("eighteen", 18);
        numbers.put("nineteen", 19);

        tnumbers.put("twenty", 20);
        tnumbers.put("thirty", 30);
        tnumbers.put("fourty", 40);
        tnumbers.put("fifty", 50);
        tnumbers.put("sixty", 60);
        tnumbers.put("seventy", 70);
        tnumbers.put("eighty", 80);
        tnumbers.put("ninety", 90);

        onumbers.put("hundred", 100);
        onumbers.put("thousand", 1000);
        onumbers.put("million", 1000000);
        onumbers.put("billion", 1000000000);
    }

    private Integer wordToNumber(String input) throws Exception {
        Integer sum = 0;
        Integer temp = null;
        Integer previous = 0;
        String[] splitted = input.toLowerCase().split(" ");

        for (String split : splitted) {
            if (numbers.get(split) != null) {
                temp = numbers.get(split);
                sum = sum + temp;
                previous = previous + temp;
            } else if (onumbers.get(split) != null) {
                if (sum != 0)
                    sum = sum - previous;
                sum = sum + previous * onumbers.get(split);
                temp = null;
                previous = 0;
            } else if (tnumbers.get(split) != null) {
                temp = tnumbers.get(split);
                sum = sum + temp;
                previous = temp;
            }
        }

        if (sum == 0)
            throw new Exception("couldn't format number");

        return sum;
    }
}
