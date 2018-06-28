package scottleedavis.mattermost.remind.reminders;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import scottleedavis.mattermost.remind.messages.ParsedRequest;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class Formatter {

    @Autowired
    Occurrence occurrence;

    public String setReminder(ParsedRequest parsedRequest) throws Exception {
        String when = parsedRequest.getWhen();
        String amPm;
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
                amPm = (ldt.getHour() >= 12) ? "PM" : "AM";
                when = time + amPm + " " + day;
                break;
            case ON:
                ldt = occurrence.calculate(parsedRequest.getWhen());
                Matcher match = Pattern.compile("((mon|tues|wed(nes)?|thur(s)?|fri|sat(ur)?|sun)(day)?)",
                        Pattern.CASE_INSENSITIVE).matcher(when);
                if( match.find() ) {

                    //TODO at 9AM Monday, July 2nd.
                    timeRaw = ldt.getHour() % 12;
                    timeRaw = timeRaw == 0 ? 12 : timeRaw;
                    amPm = (ldt.getHour() >= 12) ? "PM" : "AM";
                    String dayOfWeek = DayOfWeek.of(ldt.getDayOfWeek().getValue()).toString();
                    String month = ldt.getMonth().toString();
                    day = Integer.toString(ldt.getDayOfMonth());
                    when = "at " + timeRaw + amPm + " " + dayOfWeek + ", " + month + " " + day;

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
}
