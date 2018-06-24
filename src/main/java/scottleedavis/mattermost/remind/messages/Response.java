package scottleedavis.mattermost.remind.messages;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import java.util.List;

@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class Response {

    private String text;
    private String username;
    private String channel;
    private ResponseType responseType;
    private List<Attachment> attachments;

    public void setText(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public void setResponseType(ResponseType responseType) {
        this.responseType = responseType;
    }

    public ResponseType getResponseType() {
        return responseType;
    }

    public enum ResponseType {
        @JsonProperty("ephemeral")
        EPHEMERAL,

        @JsonProperty("in_channel")
        IN_CHANNEL;
    }

    public List<Attachment> getAttachments() {
        return attachments;
    }

    public void setAttachments(List<Attachment> attachments) {
        this.attachments = attachments;
    }
}