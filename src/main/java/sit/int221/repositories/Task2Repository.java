package sit.int221.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import sit.int221.entities.Tasks2;


public interface Task2Repository extends JpaRepository<Tasks2, Integer> {
    @Modifying
    @Query("UPDATE Tasks2 t SET t.statuses.id = :newStatus WHERE t.statuses.id = :oldStatus")
    void transferStatusAllBy(Integer newStatus, Integer oldStatus);
}
