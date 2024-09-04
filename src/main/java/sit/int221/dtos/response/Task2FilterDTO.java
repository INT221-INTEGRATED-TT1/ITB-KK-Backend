package sit.int221.dtos.response;

import lombok.Data;
import sit.int221.entities.primary.Statuses2;

@Data
public class Task2FilterDTO {
    private Integer id;
    private String title;
    private String assignees;
    private Statuses2 status;
}
