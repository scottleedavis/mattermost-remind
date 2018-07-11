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

        switch (chronoUnit) {
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
                if (recurrentDates.size() > 0) {
                    Integer builtHour = formatter.wordToNumber(chronoUnit);
                    return recurrentDates.stream().map(ldt_rd -> chooseClosest(ldt_rd.toLocalDate().atTime(builtHour, 0), now, false)).collect(Collectors.toList());
                }
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
                if (recurrentDates.size() > 0) {
                    return recurrentDates.stream().map(ldt_rd -> chooseClosest(ldt_rd.toLocalDate().atTime(Integer.parseInt(chronoUnit), 0), now, false)).collect(Collectors.toList());
                }
                closest = LocalDate.now().atTime(Integer.parseInt(chronoUnit), 0);
                return Arrays.asList(chooseClosest(closest, now, false));
            default:
                break;
        }

        if (Pattern.compile("(1[012]|[1-9]):[0-5][0-9](\\s)?(?i)(am|pm)",  // 12:30PM, 12:30 pm
                Pattern.CASE_INSENSITIVE).matcher(chronoUnit).find()) {
            int amPmOffset = (chronoUnit.charAt(chronoUnit.length() - 3) == ' ') ? 3 : 2;
            String amPm = chronoUnit.substring(chronoUnit.length() - amPmOffset).trim();
            time = Arrays.stream(chronoUnit.substring(0, chronoUnit.length() - amPmOffset).split(":"))
                    .mapToInt(Integer::parseInt).toArray();

            time[0] = amPm.toLowerCase().equals("pm") ? (time[0] < 12 ? ((time[0] + 12) % 24) : time[0]) : time[0] % 12;
            if (recurrentDates.size() > 0) {
                return recurrentDates.stream().map(ldt_rd -> chooseClosest(ldt_rd.toLocalDate().atTime(time[0], time[1]), now, true)).collect(Collectors.toList());
            }
            closest = LocalDate.now().atTime(time[0], time[1]);
            return Arrays.asList(chooseClosest(closest, now, true));

        } else if (Pattern.compile("(1[012]|[0-9]):[0-5][0-9]", // 12:30
                Pattern.CASE_INSENSITIVE).matcher(chronoUnit).find()) {
            time = Arrays.stream(chronoUnit.split(":")).mapToInt(Integer::parseInt).toArray();
            time[0] = time[0] % 24;
            if (recurrentDates.size() > 0) {
                return recurrentDates.stream().map(ldt_rd -> chooseClosest(ldt_rd.toLocalDate().atTime(time[0], time[1]), now, true)).collect(Collectors.toList());
            }
            closest = LocalDate.now().atTime(time[0], time[1]);
            return Arrays.asList(chooseClosest(closest, now, true));

        } else if (Pattern.compile("(1[012]|[1-9])[0-5][0-9](\\s)?(?i)(am|pm)", // 1230pm, 1230 pm
                Pattern.CASE_INSENSITIVE).matcher(chronoUnit).find()) {
            int amPmOffset = (chronoUnit.charAt(chronoUnit.length() - 3) == ' ') ? 3 : 2;
            String amPm = chronoUnit.substring(chronoUnit.length() - amPmOffset).trim();
            String subChronoUnit = chronoUnit.substring(0, chronoUnit.length() - amPmOffset);
            subChronoUnit = String.format("%4s", subChronoUnit).replace(' ', '0');
            String[] parts = {subChronoUnit.substring(0, 2), subChronoUnit.substring(2)};
            time = Arrays.stream(parts).mapToInt(Integer::parseInt).toArray();

            time[0] = amPm.equalsIgnoreCase("pm") ? (time[0] < 12 ? ((time[0] + 12) % 24) : time[0]) : time[0] % 12;
            if (recurrentDates.size() > 0) {
                return recurrentDates.stream().map(ldt_rd -> chooseClosest(ldt_rd.toLocalDate().atTime(time[0], time[1]), now, true)).collect(Collectors.toList());
            }
            closest = LocalDate.now().atTime(time[0], time[1]);
            return Arrays.asList(chooseClosest(closest, now, true));

        } else if (Pattern.compile("(1[012]|[1-9])(\\s)?(?i)(am|pm)",  // 5PM, 7 am
                Pattern.CASE_INSENSITIVE).matcher(chronoUnit).find()) {
            int amPmOffset = (chronoUnit.charAt(chronoUnit.length() - 3) == ' ') ? 3 : 2;
            String amPm = chronoUnit.substring(chronoUnit.length() - amPmOffset).trim();
            String subChronoUnit = chronoUnit.substring(0, chronoUnit.length() - amPmOffset);
            int time_check = Integer.parseInt(subChronoUnit);
            final int time_solo = amPm.equalsIgnoreCase("pm") ? (time_check < 12 ? ((time_check + 12) % 24) : time_check) : time_check % 12;
            if (recurrentDates.size() > 0) {
                return recurrentDates.stream().map(ldt_rd -> chooseClosest(ldt_rd.toLocalDate().atTime(time_solo, 0), now, true)).collect(Collectors.toList());
            }
            closest = LocalDate.now().atTime(time_solo, 0);
            return Arrays.asList(chooseClosest(closest, now, true));

        } else if (Pattern.compile("(1[012]|[1-9])[0-5][0-9]",  // 1200
                Pattern.CASE_INSENSITIVE).matcher(chronoUnit).find()) {
            String[] parts = {chronoUnit.substring(0, 2), chronoUnit.substring(2)};
            time = Arrays.stream(parts).mapToInt(Integer::parseInt).toArray();
            time[0] = time[0] % 24;
            if (recurrentDates.size() > 0) {
                return recurrentDates.stream().map(ldt_rd -> chooseClosest(ldt_rd.toLocalDate().atTime(time[0], time[1]), now, true)).collect(Collectors.toList());
            }
            closest = LocalDate.now().atTime(time[0], time[1]);
            return Arrays.asList(chooseClosest(closest, now, true));

        }

        throw new OccurrenceException("time mark not recognized");
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
                return Arrays.asList(LocalDate.now().with(TemporalAdjusters.next(DayOfWeek.valueOf(dateUnit))).atTime(LocalTime.parse(timeUnit))); //.atTime(9, 0));
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
        chronoUnit = chronoUnit.contains("other") ? chronoUnit.split("other")[1].trim() : chronoUnit;
        String[] dateTimeSplit = chronoUnit.split(" at ");
        final String time = dateTimeSplit.length == 1 ? DEFAULT_TIME : dateTimeSplit[1];
        List<LocalDateTime> ldts = new ArrayList<>();
        List<Exception> caughtExceptions = new ArrayList<>();
        Arrays.stream(dateTimeSplit[0].split("and|,")).map(s -> s.trim()).forEach(chrono -> {
            try {
                String timeUnit = formatter.normalizeTime(time);
                String dateUnit = formatter.normalizeDate(chrono);
                LocalDate ld;
                switch (dateUnit) {
                    case "DAY":
                        ld = LocalDate.now().plusDays(1);
                        dateUnit = ld.getMonth().name().toUpperCase() + " " + ld.getDayOfMonth() + " " + ld.getYear();
                        break;
                    case "MONDAY":
                    case "TUESDAY":
                    case "WEDNESDAY":
                    case "THURSDAY":
                    case "FRIDAY":
                    case "SATURDAY":
                    case "SUNDAY":
                        ld = LocalDate.now().with(TemporalAdjusters.next(DayOfWeek.valueOf(dateUnit)));
                        dateUnit = ld.getMonth().name().toUpperCase() + " " + ld.getDayOfMonth() + " " + ld.getYear();
                        break;
                    default:
                        break;
                }

                ldts.add(LocalDateTime.parse(dateUnit + " " + timeUnit, new DateTimeFormatterBuilder()
                        .parseCaseInsensitive().appendPattern("MMMM d yyyy HH:mm").toFormatter()));

            } catch (Exception e) {
                caughtExceptions.add(e);
            }
        });
        if (caughtExceptions.size() > 0)
            throw new OccurrenceException("error normalizing date");

        return ldts;
    }

    private List<LocalDateTime> freeForm(String when) throws Exception {

        switch (when.toUpperCase()) {
            case "MONDAY":
            case "TUESDAY":
            case "WEDNESDAY":
            case "THURSDAY":
            case "FRIDAY":
            case "SATURDAY":
            case "SUNDAY":
                return on("on " + when);
            case "TOMORROW":
                return on("on " + LocalDate.now().plusDays(1).getDayOfWeek().toString());

        }

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
