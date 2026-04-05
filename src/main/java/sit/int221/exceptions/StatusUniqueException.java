package sit.int221.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class StatusUniqueException extends RuntimeException {
    public StatusUniqueException(String message) {
        super(message);
    }
}
