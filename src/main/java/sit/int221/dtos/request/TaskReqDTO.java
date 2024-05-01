package sit.int221.dtos.request;

import lombok.Data;
import sit.int221.entities.TaskStatus;
@Data
public class TaskReqDTO {
    private Integer id;
    private String title;
    private String assignees;
    private TaskStatus status;
}
