package io.github.scottleedavis.mattermost.remind.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.FORBIDDEN)
public class TokenException extends Exception {
    public TokenException(String message) {
        super(message);
    }
}
