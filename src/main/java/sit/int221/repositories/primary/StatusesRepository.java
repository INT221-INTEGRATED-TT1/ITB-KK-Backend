//package sit.int221.repositories.primary;
//
//import org.springframework.data.jpa.repository.JpaRepository;
//import sit.int221.entities.primary.Statuses2;
//
//
//public interface StatusesRepository extends JpaRepository<Statuses2, Integer> {
//    Statuses2 findByName(String name);
//
////    @Query("SELECT NEW sit.int221.dtos.response.StatusHomeCountDTO(s.id, s.name, s.description, s.color, COUNT(t2.status.id)) " +
////            "FROM Statuses s " +
////            "LEFT JOIN Tasks2 t2 ON s.id = t2.status.id " +
////            "GROUP BY s.id, s.name, s.description, s.color")
////    List<StatusHomeCountDTO> countTasksByStatus();
//
//
////    @Modifying
////    @Query(value = "UPDATE tasks2 SET statusNo = :newStatus WHERE statusNo = :oldStatus", nativeQuery = true)
////    int updateStatusNo(@Param("newStatus") int newStatus, @Param("oldStatus") int oldStatus);
//
//}
