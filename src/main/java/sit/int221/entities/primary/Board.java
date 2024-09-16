package sit.int221.entities.primary;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
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

    @Size(max = 120)
    @NotNull
    @Column(name = "boardName", nullable = false, length = 100)
    private String boardName;

    @Size(max = 36)
    @NotNull
    @Column(name = "ownerID", nullable = false)
    private String ownerID;

    @Column(name = "createOn", nullable = false, updatable = false, insertable = false)
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ssX")
    @CreationTimestamp
    private Timestamp createOn;

    @Column(name = "updateOn", nullable = false, updatable = false, insertable = false)
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ssX")
    @UpdateTimestamp
    private Timestamp updateOn;

    @JsonIgnore
    @OneToMany(mappedBy = "board", fetch = FetchType.EAGER)
    private Set<Tasks3> tasks3s = new LinkedHashSet<>();

//    @JsonIgnore
//    @OneToMany(mappedBy = "boardId", fetch = FetchType.EAGER)
//    private Set<Statuses3> statuses3s = new LinkedHashSet<>();
}