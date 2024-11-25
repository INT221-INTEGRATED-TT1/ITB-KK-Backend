package sit.int221.dtos.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BoardAllDTORes {
    private List<PersonalBoardResDTO> personalBoards;
    private List<CollabsBoardResDTO> collaboratorBoards;


}
