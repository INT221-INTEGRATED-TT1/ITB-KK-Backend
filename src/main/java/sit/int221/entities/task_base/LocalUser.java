package sit.int221.entities.primary;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.sql.Timestamp;
import java.util.LinkedHashSet;
import java.util.Set;


@Data
@Entity
@Table(name = "localusers")
public class LocalUser {
    @Id
    @Size(max = 36)
    @Column(name = "oid", nullable = false, length = 36)
    private String oid;

    @Size(max = 100)
    @NotNull
    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Size(max = 50)
    @NotNull
    @Column(name = "username", nullable = false, length = 50)
    private String username;

    @Size(max = 50)
    @NotNull
    @Column(name = "email", nullable = false, length = 50)
    private String email;

    @Column(name = "created_on", nullable = false, updatable = false, insertable = false)
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ssX")
    @CreationTimestamp
    private Timestamp createdOn;

    @Column(name = "updated_on", nullable = false, updatable = false, insertable = false)
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ssX")
    @UpdateTimestamp
    private Timestamp updatedOn;

    @OneToMany(mappedBy = "localUser")
    private Set<Collaborator> collaborators = new LinkedHashSet<>();

//    @OneToMany(mappedBy = "ownerId")
//    private Set<Board> boards = new LinkedHashSet<>();

}