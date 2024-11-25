package sit.int221.dtos.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PersonalBoardResDTO {
    private String id;
    private String name;
    private String visibility;
    private OwnerBoardDTORes owner;
}
