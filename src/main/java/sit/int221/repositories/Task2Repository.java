package sit.int221.repositories;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import sit.int221.entities.Statuses;
import sit.int221.entities.Tasks2;

import java.util.List;


public interface Task2Repository extends JpaRepository<Tasks2, Integer> {
    
    int countByStatus(Statuses statuses);
    @Modifying
    @Query("UPDATE Tasks2 t SET t.status.id = :newStatus WHERE t.status.id = :oldStatus")
    void transferStatusAllBy(Integer newStatus, Integer oldStatus);
//    @Modifying
//    @Query("INSERT INTO Tasks2(t2.title, t2.description, t2.assignees, t2.statusNo) " + "VALUES (:newTitle, :newDescription, :newAssignees, :newStatusNo)")
//    Tasks2 insertTask(String newTitle, String newDescription, String newAssignees, Integer newStatusNo);
}
