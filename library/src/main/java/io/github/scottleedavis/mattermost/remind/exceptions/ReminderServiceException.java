package io.github.scottleedavis.mattermost.remind.exceptions;

public class ReminderServiceException extends Exception {
    public ReminderServiceException(String message) {
        super(message);
    }

    public ReminderServiceException(String message, Throwable t) {
        super(message, t);
    }
}
