package sit.int221.repositories.primary;

import org.springframework.data.jpa.repository.JpaRepository;
import sit.int221.entities.primary.Board;
import sit.int221.entities.primary.Collaborator;

import java.util.List;
import java.util.Optional;

public interface CollaboratorRepository extends JpaRepository<Collaborator, Integer> {
    //    List<Collaborator> findByBoardIdAndLocalUserOid(String boardId, String oid);
    Optional<Collaborator> findByBoardIdAndLocalUserOid(String boardId, String oid);

    Collaborator findByLocalUserOid(String oid);
    List<Collaborator> findAllByBoard(Board board);

//    List<Collaborator> findByBoardId(String boardId);
}
