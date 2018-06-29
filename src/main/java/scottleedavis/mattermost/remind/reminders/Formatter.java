package scottleedavis.mattermost.remind.reminders;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import scottleedavis.mattermost.remind.messages.ParsedRequest;

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
public class Formatter {

    @Autowired
    Occurrence occurrence;

    public String upcomingReminder(LocalDateTime occurrence) {
        return occurrence.getHour() + ":"
                + occurrence.getMinute()
                + amPm(occurrence) + " "
                + capitalize(occurrence.getDayOfWeek().toString() ) + ", "
                + capitalize( occurrence.getMonth().toString() ) + " "
                + daySuffix( occurrence.getDayOfMonth() ) + "\n";
    }

    public String reminderResponse(ParsedRequest parsedRequest) throws Exception {
        String when = parsedRequest.getWhen();
        LocalDateTime ldt;
        Integer timeRaw;
        String day;
        switch (occurrence.classify(parsedRequest.getWhen())) {
            case AT:
                ldt = occurrence.calculate(parsedRequest.getWhen());
                LocalDateTime now = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);
                timeRaw = ldt.getHour() % 12;
                timeRaw = timeRaw == 0 ? 12 : timeRaw;
                String time = Integer.toString(timeRaw);
                if (ldt.getMinute() > 0)
                    time += ":" + String.format("%02d", ldt.getMinute());
                day = (ldt.getDayOfMonth() == now.getDayOfMonth()) ? "today" : "tomorrow";
                when = time + amPm(ldt) + " " + day;
                break;
            case ON:
                ldt = occurrence.calculate(parsedRequest.getWhen());
                if (Pattern.compile("((mon|tues|wed(nes)?|thur(s)?|fri|sat(ur)?|sun)(day)?)",
                        Pattern.CASE_INSENSITIVE).matcher(when).find()) {
                    timeRaw = ldt.getHour() % 12;
                    timeRaw = timeRaw == 0 ? 12 : timeRaw;
                    String dayOfWeek = capitalize(DayOfWeek.of(ldt.getDayOfWeek().getValue()).toString());
                    String month = capitalize(ldt.getMonth().toString());
                    day = daySuffix(ldt.getDayOfMonth());
                    when = "at " + timeRaw + amPm(ldt) + " " + dayOfWeek + ", " + month + " " + day;
                } else {

                }
                break;
            case IN:
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

    public String normalizeDate(String text) throws Exception {

        if( Pattern.compile("((mon|tues|wed(nes)?|thur(s)?|fri|sat(ur)?|sun)(day)?)",
                Pattern.CASE_INSENSITIVE).matcher(text).find() ) {

            switch(text.toLowerCase()) {
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

            String[] parts = text.toLowerCase().split(" ");

            switch(Integer.toString(parts.length)) {
                case "1":
                    break;
                case "2":
                    for( int i = 0; i < suffixes.length; i++ ){
                        if( suffixes[i].equals(parts[1]) ) {
                            parts[1] = parts[1].substring(0, parts[1].length() - 2);
                            break;
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
                    for( int i = 0; i < suffixes.length; i++ ){
                        if( suffixes[i].equals(parts[1]) ) {
                            parts[1] = parts[1].substring(0, parts[1].length() - 2);
                            break;
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
                    throw new Exception("unrecognized date format");
            }

            //(jan(uary)?|feb(ruary)?|mar(ch)?|apr(il)?|may|june|july|aug(ust)?|sept(ember)?|oct(ober)?|nov(ember)?|dec(ember)
            switch(parts[0]) {
                case "jan":
                    parts[0] = "january";
                    break;
                case "feb":
                    parts[0] = "february";
                    break;
                case "mar":
                    parts[0] = "march";
                    break;
                case "apr":
                    parts[0] = "april";
                    break;
                case "aug":
                    parts[0] = "august";
                    break;
                case "sept":
                    parts[0] = "september";
                    break;
                case "oct":
                    parts[0] = "october";
                    break;
                case "nov":
                    parts[0] = "november";
                    break;
                case "dec":
                    parts[0] = "december";
                    break;
                default:
                    throw new Exception("month not found");
            }

            return Arrays.stream(parts).collect(Collectors.joining(" ")).toUpperCase();

        } else {

        }
        return text;
    }
    //todo: on December 15
    //todo: on jan 12
    //todo: on July 12th
    //todo: on July 12
    //todo: on July tenth
    //todo: on July 12th 2019
    //todo: on July tenth 2019
    //todo: on July 12 2019

    //todo: on 7 (next 7th of month)
    //todo: on 7th
    //todo: on seven
    //todo: on 12/17/18
    //todo: on 12/17


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
            throw new Exception("couldn't format number");

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
    }

}
