package sit.int221.repositories.task_base;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import sit.int221.entities.task_base.Board;
import sit.int221.entities.task_base.Collaborator;

import java.util.List;
import java.util.Optional;

public interface CollaboratorRepository extends JpaRepository<Collaborator, Integer> {
    Optional<Collaborator> findByBoardIdAndLocalUserOid(String boardId, String oid);
    List<Collaborator> findByLocalUserOid(String oid);

    Boolean existsByBoardAndLocalUserEmail(Board board, String email);

    List<Collaborator> findAllByBoardId(String boardId);

    @Query("SELECT c FROM Collaborator c WHERE c.board.id = :boardId AND c.localUser.oid = :oid")
    Collaborator findByBoardIdAndLocalUserOidOrThrow(@Param("boardId") String boardId, @Param("oid") String oid);


}
