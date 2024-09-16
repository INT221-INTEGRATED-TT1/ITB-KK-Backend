package sit.int221.entities.primary;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.sql.Timestamp;
import java.time.Instant;

@Data
@Entity
@Table(name = "tasks3")
public class Tasks3 {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "taskID", nullable = false, length = 45, updatable = false, insertable = false)
    private Integer taskID;

    @Size(max = 100)
    @NotNull
    @Column(name = "taskTitle", nullable = false, length = 100)
    private String taskTitle;

    @Size(max = 500)
    @Column(name = "description", length = 500)
    private String description;

    @Size(max = 30)
    @Column(name = "assignees", length = 30)
    private String assignees;

    @NotNull
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "boardID", nullable = false)
    private Board board;

    @Column(name = "createOn", nullable = false)
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ssX")
    @CreationTimestamp
    private Timestamp createOn;

    @Column(name = "updateOn", nullable = false)
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ssX")
    @UpdateTimestamp
    private Timestamp updateOn;

    @NotNull
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "statusID", nullable = false)
    private Statuses3 statuses3;

}