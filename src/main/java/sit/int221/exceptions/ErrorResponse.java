package sit.int221.exceptions;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.TimeZone;


@Getter
@Setter
public class ErrorResponse {
    private String timestamp;
    private int status;
    private String message;
    private String instance;
    public ErrorResponse(int status, String message, String instance) {
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
        sdf2.setTimeZone(TimeZone.getTimeZone("UTC"));
        sdf2.format(timestamp);
        this.timestamp = sdf2.format(timestamp) + "+00:00";
        this.status = status;
        this.message = message;
        this.instance = instance.replace("uri=", "");
    }
}
