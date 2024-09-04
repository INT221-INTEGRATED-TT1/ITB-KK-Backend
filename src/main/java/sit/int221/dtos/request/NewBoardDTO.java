package sit.int221.dtos.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class NewBoardDTO {
    @Size(min = 10, max = 10)
    private String boardID;
    @NotEmpty
    @Size(max = 120)
    private String boardName;
    private OwnerBoard owner;
}
