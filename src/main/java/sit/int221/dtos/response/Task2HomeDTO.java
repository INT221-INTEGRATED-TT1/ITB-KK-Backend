package sit.int221.dtos.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import sit.int221.entities.Statuses;
import sit.int221.entities.TaskStatus;
import sit.int221.entities.Tasks2;

@Data
public class Task2HomeDTO {
    private Integer id;
    private String title;
    private String assignees;
    private Statuses statuses;
}
