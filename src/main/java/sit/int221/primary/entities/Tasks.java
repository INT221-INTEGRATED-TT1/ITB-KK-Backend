package sit.int221.primary.entities;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.sql.Timestamp;

@Entity
@Data
@Table(name = "tasks")
public class Tasks {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, updatable = false, insertable = false)
    private Integer id;
    @Column(name = "title", length = 100, nullable = false)
    private String title;
    @Column(name = "description", length = 500, nullable = false)
    private String description;
    @Column(name = "assignees", length = 30)
    private String assignees;
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private TaskStatus status;
    @Column(name = "createdOn", nullable = false, updatable = false, insertable = false)
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ssX")
    @CreationTimestamp
    private Timestamp createdOn;
    @Column(name = "updatedOn", nullable = false, updatable = false, insertable = false)
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ssX")
    @UpdateTimestamp
    private Timestamp updatedOn;

}

