package sit.int221.dtos.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import sit.int221.entities.Statuses;
import sit.int221.entities.TaskStatus;

@Data
public class NewTask2DTO {
    @NotEmpty
    private String title;
    private String description;
    private String assignees;
    private Integer status;
}
