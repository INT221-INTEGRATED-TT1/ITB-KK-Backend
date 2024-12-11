package sit.int221.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.FORBIDDEN)
public class UploadForbiddenException extends RuntimeException {

    public UploadForbiddenException(String message) {
        super(message);
    }

}
