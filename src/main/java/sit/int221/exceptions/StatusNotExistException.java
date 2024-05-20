package sit.int221.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class StatusNotExistException extends RuntimeException {
    private String field;
    public StatusNotExistException(String field, String message) {
        super(message);
        this.field = field;
    }
    public String getField() {
        return field;
    }
}
