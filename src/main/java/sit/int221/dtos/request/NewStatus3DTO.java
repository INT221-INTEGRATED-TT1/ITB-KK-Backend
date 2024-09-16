package sit.int221.dtos.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class NewStatus3DTO {
    @NotNull
    @NotEmpty
    @Size(max = 50)
    private String name;
    @Size(max = 200)
    private String description;
    private String color;
//    @NotNull
//    @NotEmpty
//    @Size(min = 10, max = 10)
//    private String boardId;
}
