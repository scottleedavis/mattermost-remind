package io.github.scottleedavis.mattermost.remind.messages;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class UpdateResponse {

    private Update update;
    private String ephemeralText;

    public Update getUpdate() {
        return update;
    }

    public void setUpdate(Update update) {
        this.update = update;
    }

    public String getEphemeralText() {
        return ephemeralText;
    }

    public void setEphemeralText(String ephemeralText) {
        this.ephemeralText = ephemeralText;
    }
}
