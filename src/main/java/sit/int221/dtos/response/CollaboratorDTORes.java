package sit.int221.dtos.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.sql.Timestamp;

@Data
public class CollaboratorDTORes {
    private String oid;
    private String name;
    private String email;
    private String accessRight;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ssX")
    private Timestamp addedOn;
}
