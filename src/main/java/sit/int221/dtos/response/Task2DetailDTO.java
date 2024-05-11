package sit.int221.dtos.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import sit.int221.entities.Statuses;

import java.sql.Timestamp;

@Data
public class Task2DetailDTO {
    private Integer id;
    private String title;
    private String description;
    private String assignees;
    private Statuses status;
}
