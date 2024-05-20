package sit.int221.exceptions;

import lombok.Data;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@Data
@ResponseStatus(HttpStatus.NOT_FOUND)
public class StatusNotFoundException extends RuntimeException {
    public StatusNotFoundException(String message) {
        super(message);
    }
}
