package sit.int221.dtos.response;

import lombok.Data;
import sit.int221.entities.TaskStatus;

@Data
public class TaskResDTO {
    private Integer id;
    private String title;
    private String description;
    private String assignees;
    private TaskStatus status;
}
