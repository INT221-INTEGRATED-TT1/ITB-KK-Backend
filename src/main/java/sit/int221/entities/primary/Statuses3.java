package sit.int221.entities.primary;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.LinkedHashSet;
import java.util.Set;

@Data
@Entity
@Table(name = "statuses")
public class Statuses3 {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "statusID", nullable = false, length = 45, updatable = false, insertable = false)
    private Integer statusID;

    @Size(max = 50)
    @NotNull
    @Column(name = "statusName", nullable = false, length = 50)
    private String statusName;

    @Size(max = 200)
    @Column(name = "statusDescription", length = 200)
    private String statusDescription;

    @Size(max = 10)
    @Column(name = "statusColor", length = 10)
    private String statusColor;

    @NotNull
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "boardID", nullable = false)
    private Board boardID;

    @NotNull
    @Column(name = "createOn", nullable = false)
    private Instant createOn;

    @NotNull
    @Column(name = "updateOn", nullable = false)
    private Instant updateOn;

    @JsonIgnore
    @OneToMany(mappedBy = "statuses3")
    private Set<Tasks3> tasks3s = new LinkedHashSet<>();

}