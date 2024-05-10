package sit.int221.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import sit.int221.entities.Statuses;

import java.util.List;


public interface StatusesRepository extends JpaRepository<Statuses, Integer> {
    Statuses findByName(String name);
//    @Modifying
//    @Query(value = "UPDATE tasks2 SET statusNo = :newStatus WHERE statusNo = :oldStatus", nativeQuery = true)
//    int updateStatusNo(@Param("newStatus") int newStatus, @Param("oldStatus") int oldStatus);

}
