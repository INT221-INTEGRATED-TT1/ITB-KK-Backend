package sit.int221.repositories.primary;

import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import sit.int221.entities.primary.Board;
import sit.int221.entities.primary.Statuses2;
import sit.int221.entities.primary.Statuses3;
import sit.int221.entities.primary.Tasks3;

import java.util.List;

public interface Tasks3Repository extends JpaRepository<Tasks3, Integer> {
    List<Tasks3> findAllByBoard(Board board);



    int countByStatuses3(Statuses3 statuses3);

    @Modifying
    @Query("UPDATE Tasks3 t SET t.statuses3.statusID = :newStatus WHERE t.statuses3.statusID = :oldStatus")
    void transferStatusAllBy(Integer newStatus, Integer oldStatus);
}