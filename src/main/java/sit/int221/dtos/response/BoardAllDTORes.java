package sit.int221.dtos.response;

import lombok.Data;

@Data
public class BoardAllDTORes {
    private String id;
    private String name;
    private String visibility;
    private String ownerId;
    private String createdOn;
    private String updatedOn;
}
