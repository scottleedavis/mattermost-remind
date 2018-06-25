package scottleedavis.mattermost.remind.reminders;

import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;

@Component
public class Occurrence {

    public LocalDateTime calculate(String when) throws Exception {

        if ( when.startsWith("in") )
            return in(when);
        else if ( when.startsWith("at") )
            return at(when);
        else if ( when.startsWith("on") )
            return on(when);
        else if ( when.startsWith("every") )
            return every(when);
        else
            return freeForm(when);

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
        switch(chronoUnit) {
            case "seconds":
            case "second":
                date =  LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS).plusSeconds(count);
                break;
            case "minutes":
            case "minute":
                date =  LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS).plusMinutes(count);
                break;
            case "hours":
            case "hour":
                date =  LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS).plusHours(count);
                break;
            case "days":
            case "day":
                date =  LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS).plusDays(count);
                break;
            case "weeks":
            case "week":
                date =  LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS).plusWeeks(count);
                break;
            case "months":
            case "month":
                date =  LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS).plusMonths(count);
                break;
            case "years":
            case "year":
                date =  LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS).plusYears(count);
                break;
            default:
                throw new Exception("Unrecognized time specification");
        }
        return date;

    }

    private LocalDateTime at(String when) throws Exception {

        LocalDateTime date;
        LocalDateTime now = LocalDateTime.now();

        String[] timeChunks = when.split(" ");
        if (timeChunks.length != 2)
            throw new Exception("unrecognized time mark.");

        // todo: at noon
        // todo: at midnight
        // todo: at two
        // todo: at 7
        // todo: at 12:30pm
        // todo: at 1230am
        // todo: at 1400
        String chronoUnit = timeChunks[1].toLowerCase();
        switch(chronoUnit) {
            case "noon":
                LocalDateTime todayAtNoon = LocalDate.now().atTime(12, 0);
                if ( todayAtNoon.isBefore(now) )
                    return todayAtNoon.plusDays(1);
                else
                    return todayAtNoon;
            case "midnight":
                break;
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
                break;
            default:
                break;
        }

        throw new Exception("not yet supported");
    }

    private LocalDateTime on(String when) throws Exception {
        throw new Exception("not yet supported");
    }

    private LocalDateTime every(String when) throws Exception {
        throw new Exception("not yet supported");
    }

    private LocalDateTime freeForm(String when) throws Exception {
        throw new Exception("unrecognized time mark.");
    }

    private static HashMap<String, Integer> numbers= new HashMap<String, Integer>();
    private static HashMap<String, Integer> onumbers= new HashMap<String, Integer>();
    private static HashMap<String, Integer> tnumbers= new HashMap<String, Integer>();

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
        String [] splitted = input.toLowerCase().split(" ");

        for( String split : splitted ){
            if( numbers.get(split) != null ){
                temp = numbers.get(split);
                sum = sum + temp;
                previous = previous + temp;
            }
            else if( onumbers.get(split) != null ){
                if ( sum != 0 )
                    sum = sum - previous;
                sum = sum + previous * onumbers.get(split);
                temp = null;
                previous = 0;
            }
            else if( tnumbers.get(split) != null ){
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
