package sit.int221.entities;

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
    @Column(name = "id")
    private Integer id;
    @Column(name = "title")
    private String title;
    @Column(name = "description")
    private String description;
    @Column(name = "assignees")
    private String assignees;
    @Column(name = "status")
    private String status;
    @Column(name = "createdOn", nullable = false, updatable = false)
    @CreationTimestamp
    private Timestamp createdOn;
    @Column(name = "updatedOn", nullable = false, updatable = false)
    @UpdateTimestamp
    private Timestamp updatedOn;

}

