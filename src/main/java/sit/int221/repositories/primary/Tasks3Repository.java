package sit.int221.repositories.primary;

import org.springframework.data.jpa.repository.JpaRepository;
import sit.int221.entities.primary.Board;
import sit.int221.entities.primary.Tasks3;

import java.util.List;

public interface Tasks3Repository extends JpaRepository<Tasks3, Integer> {
    List<Tasks3> findAllByBoard(Board boardId);
}