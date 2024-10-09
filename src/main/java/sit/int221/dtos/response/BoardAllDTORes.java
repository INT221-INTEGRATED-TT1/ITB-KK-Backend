package sit.int221.dtos.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BoardAllDTORes {
    private List<BoardResDTO> personalBoards;
    private List<BoardResDTO> collaboratorBoards;


}
