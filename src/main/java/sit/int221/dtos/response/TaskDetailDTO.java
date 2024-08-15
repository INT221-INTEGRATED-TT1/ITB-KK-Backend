package sit.int221.dtos.response;

import lombok.Data;
import sit.int221.primary.entities.TaskStatus;

@Data
public class TaskDetailDTO {
    private Integer id;
    private String title;
    private String description;
    private String assignees;
    private TaskStatus status;
}
