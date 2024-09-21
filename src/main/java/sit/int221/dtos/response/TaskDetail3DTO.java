package sit.int221.dtos.response;

import lombok.Data;
import sit.int221.entities.primary.Statuses3;

@Data
public class TaskDetail3DTO {
    private Integer id;
    private String title;
    private String description;
    private String assignees;
    private Statuses3 statuses3;
}
