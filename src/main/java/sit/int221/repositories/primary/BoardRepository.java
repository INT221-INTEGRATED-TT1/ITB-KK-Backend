package sit.int221.repositories.primary;

import org.springframework.data.jpa.repository.JpaRepository;
import sit.int221.entities.primary.Board;

import java.util.List;

public interface BoardRepository extends JpaRepository<Board, String> {
    List<Board> findAllByOwnerId(String ownerId);
//    Boolean findByOwnerId(String oid);
    boolean existsById(String boardId);

}