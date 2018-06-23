package me.scottleedavis.mattermostremind.reminders;

import me.scottleedavis.mattermostremind.parser.ReminderRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class Scheduler {

    @Autowired
    ReminderRequest reminderRequest;

    public ResponseMessage setReminder(String userName, String message) {

        ResponseMessage responseMessage = new ResponseMessage();

        try {
            String target = reminderRequest.findTarget(message);
            String when = reminderRequest.findWhen(message);

            responseMessage.setText("Ok " + userName +", I'll remind you about " + message);

        } catch( Exception e ) {
            responseMessage.setText("Sorry, I didn’t quite get that. I’m easily confused. Perhaps try the words in a different order? This usually works: `/remind [@someone or #channel] [what] [when]`.\n");
        }

        responseMessage.setResponseType(ResponseMessage.ResponseType.EPHEMERAL);

        return responseMessage;
    }
}
