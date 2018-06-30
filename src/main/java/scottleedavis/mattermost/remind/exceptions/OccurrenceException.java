package scottleedavis.mattermost.remind.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
public class OccurrenceException extends Exception {
    public OccurrenceException(String message){ super(message);}
}
