package me.scottleedavis.mattermostremind.reminders;

import me.scottleedavis.mattermostremind.messages.Action;
import me.scottleedavis.mattermostremind.messages.Attachment;
import me.scottleedavis.mattermostremind.messages.Context;
import me.scottleedavis.mattermostremind.messages.Integration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
public class ReminderOptions {

    @Value("${appUrl}")
    private String appUrl;

    public List<Action> setActions() {
        return Arrays.asList(delete(), view());
    }

    public List<Action> finishedActions() {
        return Arrays.asList(complete(), delete(), snooze());
    }

    private Action delete() {
        Context context = new Context();
        context.setAction("delete");
        Integration integration = new Integration();
        integration.setContext(context);
        integration.setUrl(appUrl + "/delete");
        Action action = new Action();
        action.setIntegration(integration);
        action.setName("Delete");
        return action;
    }

    private Action view() {
        Context context = new Context();
        context.setAction("view");
        Integration integration = new Integration();
        integration.setContext(context);
        integration.setUrl(appUrl + "/view");
        Action action = new Action();
        action.setIntegration(integration);
        action.setName("View Reminders");
        return action;
    }

    private Action complete() {
        Context context = new Context();
        context.setAction("complete");
        Integration integration = new Integration();
        integration.setContext(context);
        integration.setUrl(appUrl + "/complete");
        Action action = new Action();
        action.setIntegration(integration);
        action.setName("Mark as Complete");
        return action;
    }

    private Action snooze() {
        Context context = new Context();
        context.setAction("snooze");
        Integration integration = new Integration();
        integration.setContext(context);
        integration.setUrl(appUrl + "/snooze");
        Action action = new Action();
        action.setIntegration(integration);
        action.setName("Snooze");
        return action;
    }
}
