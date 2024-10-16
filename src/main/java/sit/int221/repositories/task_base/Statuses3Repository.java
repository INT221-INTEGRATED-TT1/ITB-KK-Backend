package sit.int221.repositories.task_base;

import org.springframework.data.jpa.repository.JpaRepository;
import sit.int221.entities.task_base.Board;
import sit.int221.entities.task_base.Statuses3;

import java.util.List;


public interface Statuses3Repository extends JpaRepository<Statuses3, Integer> {
//    Statuses3 findByName(String name);
    Boolean existsByNameAndBoardId(String name, Board boardId);
    List<Statuses3> findAllByBoardId(Board boardId);

//    Statuses3 findByBoardIdAndId(String boardId, String Id);
}