package sit.int221.entities.task_base;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Data
@Entity
@Table(name = "boards")
public class Board {
    @Id
    @Size(max = 45)
    @Column(name = "boardId", nullable = false, length = 45)
    private String id;

    @Size(max = 120)
    @NotNull
    @Column(name = "name", nullable = false, length = 120)
    private String name;

    @Size(max = 45)
    @NotNull
    @Column(name = "visibility", nullable = false, length = 45)
    private String visibility;

    @Size(max = 36)
    @NotNull
    @Column(name = "owner_id", nullable = false)
    private String ownerId;

    @Column(name = "created_on", nullable = false, updatable = false, insertable = false)
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ssX")
    @CreationTimestamp
    private Timestamp createdOn;

    @Column(name = "updated_on", nullable = false, updatable = false, insertable = false)
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ssX")
    @UpdateTimestamp
    private Timestamp updatedOn;

    @JsonIgnore
    @OneToMany(mappedBy = "board")
    private Set<Tasks3> tasks3s = new LinkedHashSet<>();

    @JsonIgnore
    @OneToMany(mappedBy = "board")
    private Set<Collaborator> collaborators = new LinkedHashSet<>();

//    @JsonIgnore
//    @OneToMany(mappedBy = "boardId", fetch = FetchType.EAGER)
//    private Set<Statuses3> statuses3s = new LinkedHashSet<>();
}