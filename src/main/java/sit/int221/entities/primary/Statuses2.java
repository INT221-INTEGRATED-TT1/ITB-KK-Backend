package sit.int221.entities.primary;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "statuses2")
public class Statuses2 {
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
