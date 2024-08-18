package sit.int221.dtos.response;

import lombok.Data;
import sit.int221.entities.primary.Statuses;

@Data
public class Task2FilterDTO {
    private Integer id;
    private String title;
    private String assignees;
    private Statuses status;
}
