package sit.int221.dtos.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PersonalBoardResDTO {
    private String id;
    private String name;
    private String visibility;
    private LocalDateTime createdOn;
    private OwnerBoardDTORes owner;
}
