package sit.int221.dtos.request;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import sit.int221.entities.primary.TaskStatus;

@Data
public class NewTaskDTO {
    @NotEmpty
//    @Size(max = 100)
    private String title;
//    @Size(max = 500)
    private String description;
//    @Size(max = 30)
    private String assignees;
    private TaskStatus status;
}
