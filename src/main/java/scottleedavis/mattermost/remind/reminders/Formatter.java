package scottleedavis.mattermost.remind.reminders;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Component
public class Formatter {

    @Autowired
    Occurrence occurrence;

    public String setReminder(String target, String message, String when) throws Exception {
        switch (occurrence.classify(when)) {
            case AT:
                LocalDateTime ldt = occurrence.calculate(when);
                LocalDateTime now = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);
                Integer timeRaw = ldt.getHour() % 12;
                timeRaw = timeRaw == 0 ? 12 : timeRaw;
                String time = Integer.toString(timeRaw);
                if (ldt.getMinute() > 0)
                    time += ":" + String.format("%02d", ldt.getMinute());
                String day = (ldt.getDayOfMonth() == now.getDayOfMonth()) ? "today" : "tomorrow";
                String amPm = (ldt.getHour() >= 12) ? "PM" : "AM";
                when = time + amPm + " " + day;
                break;
            case IN:
            default:
                break;
        }
        return ":thumbsup: I will remind " +
                (target.equals("me") ? "you" : target) +
                " \"" + message + "\" " + when;
    }
}
