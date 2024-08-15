package sit.int221.dtos.response;

import lombok.Data;
import sit.int221.primary.entities.Statuses;

@Data
public class Task2DetailDTO {
    private Integer id;
    private String title;
    private String description;
    private String assignees;
    private Statuses status;
}
