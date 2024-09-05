package sit.int221.dtos.response;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class BoardResDTO {
    private String boardID;
    private String boardName;
    private OwnerBoard owner;
}
