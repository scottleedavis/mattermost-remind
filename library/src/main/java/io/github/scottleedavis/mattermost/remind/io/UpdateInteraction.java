package io.github.scottleedavis.mattermost.remind.io;

import io.github.scottleedavis.mattermost.remind.messages.Interaction;
import io.github.scottleedavis.mattermost.remind.messages.UpdateResponse;
import io.github.scottleedavis.mattermost.remind.reminders.Updates;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UpdateInteraction {

    @Autowired
    private Updates updates;

    @RequestMapping(value = "/delete", produces = "application/json")
    public UpdateResponse delete(@RequestBody Interaction interaction) throws Exception {
        return updates.delete(interaction);
    }

    @RequestMapping(value = "/view", produces = "application/json")
    public UpdateResponse view(@RequestBody Interaction interaction) throws Exception {
        return updates.view(interaction);
    }

    @RequestMapping(value = "/complete", produces = "application/json")
    public UpdateResponse complete(@RequestBody Interaction interaction) throws Exception {
        return updates.complete(interaction);
    }

    @RequestMapping(value = "/snooze", produces = "application/json")
    public UpdateResponse snooze(@RequestBody Interaction interaction) throws Exception {
        return updates.snooze(interaction);
    }
}
