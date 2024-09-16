//package sit.int221.repositories.primary;
//
//import org.springframework.data.jpa.repository.JpaRepository;
//import org.springframework.data.jpa.repository.Modifying;
//import org.springframework.data.jpa.repository.Query;
//import sit.int221.entities.primary.Statuses2;
//import sit.int221.entities.primary.Tasks2;
//
//
//public interface Task2Repository extends JpaRepository<Tasks2, Integer> {
//
//    int countByStatus(Statuses2 statuses2);
//    @Modifying
//    @Query("UPDATE Tasks2 t SET t.status.id = :newStatus WHERE t.status.id = :oldStatus")
//    void transferStatusAllBy(Integer newStatus, Integer oldStatus);
////    @Modifying
////    @Query("INSERT INTO Tasks2(t2.title, t2.description, t2.assignees, t2.statusNo) " + "VALUES (:newTitle, :newDescription, :newAssignees, :newStatusNo)")
////    Tasks2 insertTask(String newTitle, String newDescription, String newAssignees, Integer newStatusNo);
//}
