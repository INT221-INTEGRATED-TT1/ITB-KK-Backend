package sit.int221.repositories.primary;

import org.springframework.data.jpa.repository.JpaRepository;
import sit.int221.entities.primary.Board;

public interface BoardRepository extends JpaRepository<Board, String> {
}