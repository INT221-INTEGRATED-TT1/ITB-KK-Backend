package sit.int221.repositories.task_base;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import sit.int221.entities.task_base.Board;
import sit.int221.entities.task_base.Statuses3;
import sit.int221.entities.task_base.Tasks3;

import java.util.List;

public interface Tasks3Repository extends JpaRepository<Tasks3, Integer> {
    List<Tasks3> findAllByBoard(Board boardId, Sort sort);
    int countByStatuses3(Statuses3 statuses3);

    @Modifying
    @Query("UPDATE Tasks3 t SET t.statuses3.id = :newStatus WHERE t.statuses3.id = :oldStatus")
    void transferStatusAllBy(Integer newStatus, Integer oldStatus);

}