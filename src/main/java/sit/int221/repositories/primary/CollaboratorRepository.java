package sit.int221.repositories.primary;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import sit.int221.entities.primary.Board;
import sit.int221.entities.primary.Collaborator;

import java.util.List;
import java.util.Optional;

public interface CollaboratorRepository extends JpaRepository<Collaborator, Integer> {
    Optional<Collaborator> findByBoardIdAndLocalUserOid(String boardId, String oid);
    Collaborator findByLocalUserOid(String oid);
    Boolean existsByBoardAndLocalUserEmail(Board board, String email);

    @Query("SELECT c FROM Collaborator c WHERE c.board.id = :boardId AND c.localUser.oid = :oid")
    Collaborator findByBoardIdAndLocalUserOidOrThrow(@Param("boardId") String boardId, @Param("oid") String oid);

}
