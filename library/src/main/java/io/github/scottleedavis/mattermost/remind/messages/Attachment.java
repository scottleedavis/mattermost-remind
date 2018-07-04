package io.github.scottleedavis.mattermost.remind.messages;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import java.util.List;

@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class Attachment {

    private String text;
    private List<Action> actions;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public List<Action> getActions() {
        return actions;
    }

    public void setActions(List<Action> actions) {
        this.actions = actions;
    }
}
