package sit.int221.exceptions;

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
        this.timestamp = timestamp;
        this.status = status;
        this.message = message;
        this.instance = instance.replace("uri=", "");
    }
}
