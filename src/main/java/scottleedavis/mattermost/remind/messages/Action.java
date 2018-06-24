package scottleedavis.mattermost.remind.messages;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class Action {

    private String name;
    private Integration integration;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integration getIntegration() {
        return integration;
    }

    public void setIntegration(Integration integration) {
        this.integration = integration;
    }
}
