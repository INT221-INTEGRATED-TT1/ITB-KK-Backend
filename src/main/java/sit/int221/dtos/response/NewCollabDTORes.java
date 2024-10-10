package sit.int221.dtos.response;

import lombok.Data;

@Data
public class NewCollabDTORes {
    private String boardId;
    private String collaboratorName;
    private String collaboratorEmail;
    private String accessRight;
}
