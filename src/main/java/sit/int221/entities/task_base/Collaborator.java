package sit.int221.entities.task_base;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.sql.Timestamp;

@Data
@Entity
@Table(name = "collaborator")
public class Collaborator {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "collabId", nullable = false)
    private Integer id;

    @NotNull
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "board_id", nullable = false)
    private Board board;

    @NotNull
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private LocalUser localUser;

    @Size(max = 45)
    @NotNull
    @Column(name = "accessRight", nullable = false, length = 45)
    private String accessRight;

    @Column(name = "addedOn", nullable = false)
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ssX")
    @CreationTimestamp
    private Timestamp addedOn;

}