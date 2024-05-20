package sit.int221.dtos.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import sit.int221.entities.Statuses;
import sit.int221.entities.TaskStatus;

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
