package sit.int221.dtos.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class NewBoardDTO {
    @Size(min = 0, max = 120)
    @NotNull
    @NotBlank
    private String name;
}
