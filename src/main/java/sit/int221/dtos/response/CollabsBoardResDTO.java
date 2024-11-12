package sit.int221.dtos.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import sit.int221.entities.enums.InvitationStatus;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CollabsBoardResDTO {
    private String id;
    private String name;
    private OwnerBoardCollabDTORes owner;
    private String accessRight;
    private InvitationStatus invitationStatus;
}
