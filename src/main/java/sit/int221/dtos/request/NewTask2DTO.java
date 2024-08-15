package sit.int221.dtos.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class NewTask2DTO {
    @NotNull
    @NotEmpty
    @Size(max = 100)
    private String title;
    @Size(max = 500)
    private String description;
    @Size(max = 30)
    private String assignees;
    private Integer status;
}
