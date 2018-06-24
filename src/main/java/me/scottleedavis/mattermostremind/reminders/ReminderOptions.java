package me.scottleedavis.mattermostremind.reminders;

import me.scottleedavis.mattermostremind.messages.Action;
import me.scottleedavis.mattermostremind.messages.Attachment;
import me.scottleedavis.mattermostremind.messages.Context;
import me.scottleedavis.mattermostremind.messages.Integration;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
public class ReminderOptions {

    public List<Action> remindActions() {
        Context context = new Context();
        context.setAction("delete");
        Integration integration = new Integration();
        integration.setContext(context);
        integration.setUrl("http://fooo.com");
        Action action = new Action();
        action.setIntegration(integration);
        action.setName("View Reminders");
        return Arrays.asList(action);
    }
}
