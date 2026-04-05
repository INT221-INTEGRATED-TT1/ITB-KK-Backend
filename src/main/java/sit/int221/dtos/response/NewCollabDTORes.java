package sit.int221.dtos.response;

import lombok.Data;
import sit.int221.entities.enums.InvitationStatus;

@Data
public class NewCollabDTORes {
    private String boardId;
    private String collaboratorName;
    private String collaboratorEmail;
    private String accessRight;
    private InvitationStatus invitationStatus;
}
