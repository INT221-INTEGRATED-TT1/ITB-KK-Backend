package sit.int221.dtos.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import sit.int221.entities.task_base.Board;
import sit.int221.entities.task_base.Statuses3;

import java.sql.Timestamp;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TaskListDTO {
    private Integer id;
    private String title;
    private String description;
    private String assignees;
    private Board board;
    private Timestamp createdOn;
    private Timestamp updatedOn;
    private Statuses3 statuses3;
    private Integer attachmentCount;
}
