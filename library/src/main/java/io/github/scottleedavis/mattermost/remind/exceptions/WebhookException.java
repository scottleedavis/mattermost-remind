package io.github.scottleedavis.mattermost.remind.exceptions;

public class WebhookException extends Exception {
    public WebhookException(String message, Throwable t) {
        super(message, t);
    }
}
