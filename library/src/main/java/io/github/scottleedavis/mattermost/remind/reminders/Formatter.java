package io.github.scottleedavis.mattermost.remind.reminders;

import io.github.scottleedavis.mattermost.remind.db.ReminderOccurrence;
import io.github.scottleedavis.mattermost.remind.exceptions.FormatterException;
import io.github.scottleedavis.mattermost.remind.messages.ParsedRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Component
public class Formatter {

    @Autowired
    private Occurrence occurrence;

    public String upcomingReminder(List<ReminderOccurrence> occurrences) {
        if (occurrences.size() > 1)
            return "NOT YET IMPLMENTED (occurences > 1";
        else {
            ReminderOccurrence reminderOccurrence = occurrences.get(0);
            LocalDateTime ldt = reminderOccurrence.getOccurrence();
            return ldt.getHour() + ":"
                    + ldt.getMinute()
                    + amPm(ldt) + " "
                    + capitalize(ldt.getDayOfWeek().toString()) + ", "
                    + capitalize(ldt.getMonth().toString()) + " "
                    + daySuffix(ldt.getDayOfMonth()) + "\n";
        }
    }

    public String reminderResponse(ParsedRequest parsedRequest) throws Exception {
        String when = parsedRequest.getWhen();
        List<LocalDateTime> ldts;
        LocalDateTime ldt;
        Integer timeRaw;
        String time;
        String dayOfWeek;
        String month;
        String day;
        String year;
        switch (occurrence.classify(parsedRequest.getWhen())) {
            case AT: //TODO handle multiple values (e.g. 4pm and 2:32 am)
                ldt = occurrence.calculate(parsedRequest.getWhen()).get(0);
                LocalDateTime now = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);
                timeRaw = ldt.getHour() % 12;
                timeRaw = timeRaw == 0 ? 12 : timeRaw;
                time = Integer.toString(timeRaw);
                if (ldt.getMinute() > 0)
                    time += ":" + String.format("%02d", ldt.getMinute());
                day = (ldt.getDayOfMonth() == now.getDayOfMonth()) ? "today" : "tomorrow";
                when = time + amPm(ldt) + " " + day;
                break;
            case ON:  //TODO handle multiple values  (e.g. 12/18 and 4-01)
                ldt = occurrence.calculate(parsedRequest.getWhen()).get(0);
                timeRaw = ldt.getHour() % 12;
                timeRaw = timeRaw == 0 ? 12 : timeRaw;
                time = ldt.getMinute() > 0 ? timeRaw + ":" + ldt.getMinute() : Integer.toString(timeRaw);
                dayOfWeek = capitalize(DayOfWeek.of(ldt.getDayOfWeek().getValue()).toString());
                month = capitalize(ldt.getMonth().toString());
                day = daySuffix(ldt.getDayOfMonth());
                year = "";
                if (Pattern.compile("((\\d{2}|\\d{1})(-|/)(\\d{2}|\\d{1})((-|/)(\\d{2}|\\d{4})))",
                        Pattern.CASE_INSENSITIVE).matcher(parsedRequest.getWhen()).find()) {
                    String[] parts = parsedRequest.getWhen().split("(-|/)");
                    year = ", " + LocalDate.now().withYear(Integer.parseInt(parts[2])).getYear();
                }
                when = "at " + time + amPm(ldt) + " " + dayOfWeek + ", " + month + " " + day + year;
                break;
            case EVERY:
                ldts = occurrence.calculate(parsedRequest.getWhen());
                String other = parsedRequest.getWhen().contains("other") ? " other" : "";
                ldt = ldts.get(0);
                timeRaw = ldt.getHour() % 12;
                timeRaw = timeRaw == 0 ? 12 : timeRaw;
                time = ldt.getMinute() > 0 ? timeRaw + ":" + ldt.getMinute() : Integer.toString(timeRaw);

                if (parsedRequest.getWhen().contains(" day ")) {

                    when = "at " + time + amPm(ldt) + " every" + other + " day";
                } else if (Pattern.compile("((mon|tues|wed(nes)?|thur(s)?|fri|sat(ur)?|sun)(day)?)",
                        Pattern.CASE_INSENSITIVE).matcher(parsedRequest.getWhen()).find()) {
                    when = "at " + time + amPm(ldt) + " every" + other;
                    String chronoUnit = Arrays.asList(parsedRequest.getWhen().split(" ")).stream().skip(1).collect(Collectors.joining(" "));
                    chronoUnit = chronoUnit.contains("other") ? chronoUnit.split("other")[1].trim() : chronoUnit;
                    String[] dateTimeSplit = chronoUnit.split(" at ");
                    String[] days = Arrays.stream(dateTimeSplit[0].split("and|,")).map(dt -> capitalize(dt.trim())).toArray(String[]::new);
                    Arrays.sort(days);

                    String daysStr = days[0];
                    if (days.length > 1) {
                        String[] subDays = Arrays.copyOfRange(days, 0, days.length - 1);
                        daysStr = String.join(", ", subDays) + " and " + days[days.length - 1];
                    }
                    when += " " + daysStr;

                } else {
                    when = "at " + time + amPm(ldt) + " every" + other + " ";
                    String[] dates = ldts.stream().map(date -> {
                        return capitalize(date.getMonth().toString()) + " " + daySuffix(date.getDayOfMonth());
                    }).toArray(String[]::new);

                    String datesStr = dates[0];
                    if (dates.length > 1) {
                        String[] subDays = Arrays.copyOfRange(dates, 0, dates.length - 1);
                        datesStr = String.join(", ", subDays) + " and " + dates[dates.length - 1];
                    }
                    when += " " + datesStr;
                }

                break;
            case IN: // TODO handle multiple values
                // TODO  normalize the name (e.g. no s, sec... just seconds) e.g. 2 sec => 2 seconds
                break;
            default:
                break;
        }
        return ":thumbsup: I will remind " +
                (parsedRequest.getTarget().equals("me") ? "you" : parsedRequest.getTarget()) +
                " \"" + parsedRequest.getMessage() + "\" " + when;
    }

    public String capitalize(String text) {
        return text.substring(0, 1).toUpperCase() + text.substring(1).toLowerCase();
    }

    public String daySuffix(Integer dayOfMonth) {
        return suffixes[dayOfMonth];
    }

    public String amPm(LocalDateTime ldt) {
        return (ldt.getHour() >= 12) ? "PM" : "AM";
    }

    public String normalizeTime(String text) throws Exception {
        switch (text) {
            case "noon":
                return LocalTime.of(12, 0).toString();
            case "midnight":
                return LocalTime.of(0, 0).toString();
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
                return LocalTime.of(wordToNumber(text), 0).toString();
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
                return LocalTime.of(Integer.parseInt(text), 0).toString();
            default:
                break;
        }

        if (Pattern.compile("(1[012]|[1-9]):[0-5][0-9](\\s)?(?i)(am|pm)",  // 12:30PM, 12:30 pm
                Pattern.CASE_INSENSITIVE).matcher(text).find()) {
            int amPmOffset = (text.charAt(text.length() - 3) == ' ') ? 3 : 2;
            String amPm = text.substring(text.length() - amPmOffset).trim();
            int[] time = Arrays.stream(text.substring(0, text.length() - amPmOffset).split(":"))
                    .mapToInt(Integer::parseInt).toArray();
            time[0] = amPm.toLowerCase().equals("pm") ? (time[0] < 12 ? ((time[0] + 12) % 24) : time[0]) : time[0] % 12;
            return LocalTime.of(time[0], time[1]).toString();
        } else if (Pattern.compile("(1[012]|[1-9]):[0-5][0-9]", // 12:30
                Pattern.CASE_INSENSITIVE).matcher(text).find()) {
            int[] time = Arrays.stream(text.split(":")).mapToInt(Integer::parseInt).toArray();
            time[0] = time[0] % 24;
            return LocalTime.of(time[0], time[1]).toString();
        } else if (Pattern.compile("(1[012]|[1-9])[0-5][0-9](\\s)?(?i)(am|pm)", // 1230pm, 1230 pm
                Pattern.CASE_INSENSITIVE).matcher(text).find()) {
            int amPmOffset = (text.charAt(text.length() - 3) == ' ') ? 3 : 2;
            String amPm = text.substring(text.length() - amPmOffset).trim();
            String subChronoUnit = text.substring(0, text.length() - amPmOffset);
            subChronoUnit = String.format("%4s", subChronoUnit).replace(' ', '0');
            String[] parts = {subChronoUnit.substring(0, 2), subChronoUnit.substring(2)};
            int[] time = Arrays.stream(parts).mapToInt(Integer::parseInt).toArray();
            time[0] = amPm.equalsIgnoreCase("pm") ? (time[0] < 12 ? ((time[0] + 12) % 24) : time[0]) : time[0] % 12;
            return LocalTime.of(time[0], time[1]).toString();
        } else if (Pattern.compile("(1[012]|[1-9])(\\s)?(?i)(am|pm)",  // 5PM, 7 am
                Pattern.CASE_INSENSITIVE).matcher(text).find()) {
            int amPmOffset = (text.charAt(text.length() - 3) == ' ') ? 3 : 2;
            String amPm = text.substring(text.length() - amPmOffset).trim();
            String subChronoUnit = text.substring(0, text.length() - amPmOffset);
            int time = Integer.parseInt(subChronoUnit);
            time = amPm.equalsIgnoreCase("pm") ? (time < 12 ? ((time + 12) % 24) : time) : time % 12;
            return LocalTime.of(time, 0).toString();
        } else if (Pattern.compile("(1[012]|[1-9])[0-5][0-9]",  // 1200
                Pattern.CASE_INSENSITIVE).matcher(text).find()) {
            String[] parts = {text.substring(0, 2), text.substring(2)};
            int[] time = Arrays.stream(parts).mapToInt(Integer::parseInt).toArray();
            time[0] = time[0] % 24;
            return LocalTime.of(time[0], time[1]).toString();
        }

        throw new FormatterException("unable to normalize time");
    }

    public String normalizeDate(String text) throws Exception {

        if (text.equalsIgnoreCase("day"))
            return text.toUpperCase();
        else if (Pattern.compile("((mon|tues|wed(nes)?|thur(s)?|fri|sat(ur)?|sun)(day)?)",
                Pattern.CASE_INSENSITIVE).matcher(text).find()) {

            switch (text.toLowerCase()) {
                case "mon":
                    text = "monday";
                    break;
                case "tues":
                    text = "tuesday";
                    break;
                case "wed":
                    text = "wednesday";
                    break;
                case "wednes":
                    text = "wednesday";
                    break;
                case "thur":
                    text = "thursday";
                    break;
                case "thurs":
                    text = "thursday";
                    break;
                case "fri":
                    text = "friday";
                    break;
                case "sat":
                    text = "saturday";
                    break;
                case "satur":
                    text = "saturday";
                    break;
                case "sun":
                    text = "sunday";
                    break;
                default:
                    break;
            }
            return text.toUpperCase();
        } else if (Pattern.compile("(jan(uary)?|feb(ruary)?|mar(ch)?|apr(il)?|may|june|july|aug(ust)?|sept(ember)?|oct(ober)?|nov(ember)?|dec(ember)?)",
                Pattern.CASE_INSENSITIVE).matcher(text).find()) {

            text = text.replace(",", "");
            String[] parts = text.toLowerCase().split(" ");

            switch (Integer.toString(parts.length)) {
                case "1":
                    break;
                case "2":
                    if (parts[1].length() > 2) {
                        for (int i = 0; i < suffixes.length; i++) {
                            if (suffixes[i].equals(parts[1])) {
                                parts[1] = parts[1].substring(0, parts[1].length() - 2);
                                break;
                            }
                        }
                    }
                    try {
                        Integer.parseInt(parts[1]);
                    } catch (Exception e) {
                        parts[1] = Integer.toString(wordToNumber(parts[1]));
                    }
                    String[] temp = parts;
                    parts = new String[3];
                    parts[0] = temp[0];
                    parts[1] = temp[1];
                    parts[2] = Integer.toString(LocalDateTime.now().getYear());
                    break;
                case "3":
                    if (parts[1].length() > 2) {
                        for (int i = 0; i < suffixes.length; i++) {
                            if (suffixes[i].equals(parts[1])) {
                                parts[1] = parts[1].substring(0, parts[1].length() - 2);
                                break;
                            }
                        }
                    }
                    try {
                        Integer.parseInt(parts[1]);
                    } catch (Exception e) {
                        parts[1] = Integer.toString(wordToNumber(parts[1]));
                    }

                    Integer.parseInt(parts[2]);

                    break;
                default:
                    throw new FormatterException("unrecognized date format");
            }

            switch (parts[0]) {
                case "january":
                case "jan":
                    parts[0] = "january";
                    break;
                case "february":
                case "feb":
                    parts[0] = "february";
                    break;
                case "march":
                case "mar":
                    parts[0] = "march";
                    break;
                case "april":
                case "apr":
                    parts[0] = "april";
                    break;
                case "may":
                    parts[0] = "may";
                    break;
                case "june":
                    parts[0] = "june";
                    break;
                case "july":
                    parts[0] = "july";
                    break;
                case "august":
                case "aug":
                    parts[0] = "august";
                    break;
                case "september":
                case "sept":
                    parts[0] = "september";
                    break;
                case "october":
                case "oct":
                    parts[0] = "october";
                    break;
                case "november":
                case "nov":
                    parts[0] = "november";
                    break;
                case "december":
                case "dec":
                    parts[0] = "december";
                    break;
                default:
                    throw new FormatterException("month not found");
            }

            return Arrays.stream(parts).collect(Collectors.joining(" ")).toUpperCase();

        } else if (Pattern.compile("((\\d{2}|\\d{1})(-|/)(\\d{2}|\\d{1})((-|/)(\\d{2}|\\d{4}))?)",
                Pattern.CASE_INSENSITIVE).matcher(text).find()) {

            String[] parts = text.split("(-|/)");
            LocalDateTime ldt;

            switch (Integer.toString(parts.length)) {
                case "2":
                    ldt = LocalDateTime.now().withMonth(Integer.parseInt(parts[0])).withDayOfMonth(Integer.parseInt(parts[1]));
                    if (ldt.withYear(LocalDateTime.now().getYear()).isBefore(LocalDateTime.now())) {
                        ldt = ldt.withYear(LocalDateTime.now().getYear()).withYear(LocalDateTime.now().getYear()).plusYears(1);
                    } else {
                        ldt = ldt.withYear(LocalDateTime.now().getYear()).withYear(LocalDateTime.now().getYear());
                    }
                    return ldt.getMonth().toString() + " " + ldt.getDayOfMonth() + " " + ldt.getYear();
                case "3":
                    ldt = LocalDateTime.now().withMonth(Integer.parseInt(parts[0])).withDayOfMonth(Integer.parseInt(parts[1]));
                    if (ldt.withYear(Integer.parseInt(parts[2])).isBefore(LocalDateTime.now())) {
                        ldt = ldt.withYear(LocalDateTime.now().getYear()).withYear(LocalDateTime.now().getYear()).plusYears(1);
                    } else {
                        ldt = ldt.withYear(LocalDateTime.now().getYear()).withYear(Integer.parseInt(parts[2]));
                    }
                    return ldt.getMonth().toString() + " " + ldt.getDayOfMonth() + " " + ldt.getYear();
                default:
                    throw new FormatterException("unrecognized date");
            }

        } else {  //single 'number'

            for (int i = 0; i < suffixes.length; i++) {
                if (suffixes[i].equals(text)) {
                    text = text.substring(0, text.length() - 2);
                    break;
                }
            }
            try {
                Integer.parseInt(text);
            } catch (Exception e) {
                text = Integer.toString(wordToNumber(text));
            }

            String month = LocalDateTime.now().getMonth().name();
            String year = Integer.toString(LocalDateTime.now().getYear());
            if (LocalDate.of(LocalDateTime.now().getYear(), LocalDateTime.now().getMonth(), Integer.parseInt(text)).isBefore(LocalDate.now())) {
                month = LocalDateTime.now().getMonth().plus(1).name();
            }
            return month + " " + text + " " + year;

        }

    }


    public Integer wordToNumber(String input) throws Exception {
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
            throw new FormatterException("couldn't format number");

        return sum;
    }

    private static String[] suffixes =
            {"0th", "1st", "2nd", "3rd", "4th", "5th", "6th", "7th", "8th", "9th",
                    "10th", "11th", "12th", "13th", "14th", "15th", "16th", "17th", "18th", "19th",
                    "20th", "21st", "22nd", "23rd", "24th", "25th", "26th", "27th", "28th", "29th",
                    "30th", "31st"};

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

        numbers.put("first", 1);
        numbers.put("second", 2);
        numbers.put("third", 3);
        numbers.put("fourth", 4);
        numbers.put("fifth", 5);
        numbers.put("sixth", 6);
        numbers.put("seventh", 7);
        numbers.put("eighth", 8);
        numbers.put("nineth", 9);
        numbers.put("tenth", 10);
        numbers.put("eleventh", 11);
        numbers.put("twelveth", 12);
        numbers.put("thirteenth", 13);
        numbers.put("fourteenth", 14);
        numbers.put("fifteenth", 15);
        numbers.put("sixteenth", 16);
        numbers.put("seventeenth", 17);
        numbers.put("eighteenth", 18);
        numbers.put("nineteenth", 19);
        tnumbers.put("twenteth", 20);
        tnumbers.put("twentyfirst", 21);
        tnumbers.put("twentysecond", 22);
        tnumbers.put("twentythird", 23);
        tnumbers.put("twentyfourth", 24);
        tnumbers.put("twentyfifth", 25);
        tnumbers.put("twentysixth", 26);
        tnumbers.put("twentyseventh", 27);
        tnumbers.put("twentyeight", 28);
        tnumbers.put("twentynineth", 29);
        tnumbers.put("thirteth", 30);
        tnumbers.put("thirtyfirst", 31);

    }

}
