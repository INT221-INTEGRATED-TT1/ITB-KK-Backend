package sit.int221.dtos.response;

import lombok.Data;
import sit.int221.primary.entities.TaskStatus;
@Data
public class TaskHomeDTO {
    private Integer id;
    private String title;
    private String assignees;
    private TaskStatus status;
}
