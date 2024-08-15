package sit.int221.primary.entities;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "statuses")
public class Statuses {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "statusNo", nullable = false, updatable = false, insertable = false)
    private Integer id;
    @Column(name = "statusName", length = 50, nullable = false, unique = true)
    private String name;
    @Column(name = "statusDescription", length = 200)
    private String description;
    @Column(name = "statusColor", length = 10)
    private String color;
//    @Column(name = "limitMaximumTask", nullable = false)
//    private Boolean limitMaximumTask;
}
