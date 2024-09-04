package sit.int221.entities.primary;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.LinkedHashSet;
import java.util.Set;

@Data
@Entity
@Table(name = "boards")
public class Board {
    @Id
    @Size(max = 45)
    @Column(name = "boardID", nullable = false, length = 45)
    private String boardID;

    @Size(max = 100)
    @NotNull
    @Column(name = "boardName", nullable = false, length = 100)
    private String boardName;

    @Size(max = 10)
    @NotNull
    @Column(name = "ownerID", nullable = false, length = 10)
    private String ownerID;

    @NotNull
    @Column(name = "createOn", nullable = false)
    private Timestamp createOn;

    @NotNull
    @Column(name = "updateOn", nullable = false)
    private Timestamp updateOn;

    @JsonIgnore
    @OneToMany(mappedBy = "boardID")
    private Set<Tasks3> tasks3s = new LinkedHashSet<>();

}