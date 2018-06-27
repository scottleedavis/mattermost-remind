package scottleedavis.mattermost.remind.reminders;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class Formatter {

    @Autowired
    Occurrence occurrence;

    public String setReminder(String target, String message, String when) throws Exception {
        switch(occurrence.classify(when)) {
            case AT:
                LocalDateTime ldt = occurrence.calculate(when);
                when = Integer.toString(ldt.getHour()) +
                        "TODO AM/PM " + " today or tomorrow";
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
