package sit.int221.entities;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.sql.Timestamp;
import java.util.List;

@Entity
@Data
@Table(name = "tasks2")
public class Tasks2 {
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
    @Column(name = "createdOn", nullable = false, updatable = false, insertable = false)
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ssX")
    @CreationTimestamp
    private Timestamp createdOn;
    @Column(name = "updatedOn", nullable = false, updatable = false, insertable = false)
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ssX")
    @UpdateTimestamp
    private Timestamp updatedOn;
//    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "statusNo")
    private Statuses statuses;

}

