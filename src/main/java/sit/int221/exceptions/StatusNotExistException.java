package sit.int221.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class StatusNotExistException extends RuntimeException {
    public StatusNotExistException( String message) {
        super(message);
    }
}
