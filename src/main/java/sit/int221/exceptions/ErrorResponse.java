package sit.int221.exceptions;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class ErrorResponse {
    private String timestamp;
    private int status;
    private String message;
    private String instance;
    public ErrorResponse(String timestamp, int status, String message, String instance) {
        this.timestamp = timestamp + "+00:00";
        this.status = status;
        this.message = message;
        this.instance = instance.replace("uri=", "");
    }
}
