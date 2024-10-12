package sit.int221.repositories.primary;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import sit.int221.entities.primary.Board;

import java.util.List;

public interface BoardRepository extends JpaRepository<Board, String> {
    List<Board> findAllByOwnerId(String ownerId);
    //    Boolean findByOwnerId(String oid);
    // Find boards where the user is a collaborator
    @Query("SELECT b FROM Board b JOIN b.collaborators c WHERE c.localUser.oid = :oid")
    List<Board> findBoardsByUserOid(@Param("oid") String oid);
}