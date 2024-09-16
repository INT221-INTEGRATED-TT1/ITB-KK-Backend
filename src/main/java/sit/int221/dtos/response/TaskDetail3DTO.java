package sit.int221.dtos.response;

import lombok.Data;
import sit.int221.entities.primary.Statuses3;
import sit.int221.entities.primary.TaskStatus;

@Data
public class TaskDetail3DTO {
    private Integer taskId;
    private String taskTitle;
    private String description;
    private String assignees;
    private Statuses3 statuses3;
}
