package sit.int221.entities.primary;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;
import java.time.Instant;

@Data
@Entity
@Table(name = "tasks3")
public class Tasks3 {
    @Id
    @Size(max = 45)
    @Column(name = "taskID", nullable = false, length = 45)
    private String taskID;

    @Size(max = 100)
    @NotNull
    @Column(name = "taskTitle", nullable = false, length = 100)
    private String taskTitle;

    @Size(max = 500)
    @Column(name = "description", length = 500)
    private String description;

    @Size(max = 30)
    @Column(name = "assignee", length = 30)
    private String assignee;

    @NotNull
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "boardID", nullable = false)
    private Board boardID;

    @NotNull
    @Column(name = "createOn", nullable = false)
    private Timestamp createOn;

    @NotNull
    @Column(name = "updateOn", nullable = false)
    private Timestamp updateOn;

    @NotNull
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "statusID", nullable = false)
    private Statuses3 statusID;

}