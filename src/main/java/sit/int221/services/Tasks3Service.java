package sit.int221.services;

import com.aventrix.jnanoid.jnanoid.NanoIdUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import sit.int221.dtos.request.NewTask3DTO;
import sit.int221.entities.primary.*;
import sit.int221.exceptions.ItemNotFoundException;
import sit.int221.exceptions.StatusNotExistException;
import sit.int221.repositories.primary.BoardRepository;
import sit.int221.repositories.primary.Tasks3Repository;

import java.sql.Timestamp;
import java.util.List;

@Service
public class Tasks3Service {
    @Autowired
    Tasks3Repository tasks3Repository;
    @Autowired
    Statuses3Service statuses3Service;
    @Autowired
    BoardRepository boardRepository;


    public List<Tasks3> getAllTaskByBoardId(String boardId) {
        Board board = boardRepository.findById(boardId).orElseThrow(() -> new ItemNotFoundException("Board id " + boardId + "not found"));
        return tasks3Repository.findAllByBoard(board);

    }

    public Tasks3 createNewTaskByBoardId(String boardId, NewTask3DTO tasks3) {
        Tasks3 newTasks3 = new Tasks3();
        newTasks3.setBoard(boardRepository.findById(boardId).orElseThrow(() -> new ItemNotFoundException("Board id " + boardId + "not found")));
        newTasks3.setTaskTitle(tasks3.getTitle().trim());
        if (tasks3.getDescription() != null && !tasks3.getDescription().isBlank()) {
            newTasks3.setDescription(tasks3.getDescription().trim());
        } else {
            newTasks3.setDescription(null);
        }
        if (tasks3.getAssignees() != null && !tasks3.getAssignees().isBlank()) {
            newTasks3.setAssignees(tasks3.getAssignees().trim());
        } else {
            newTasks3.setAssignees(null);
        }
        newTasks3.setStatuses3(new Statuses3());
        if (tasks3.getStatus3Id() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Status Does not exist");
        } else {
            try {
                Statuses3 statuses3 = statuses3Service.findStatusById(tasks3.getStatus3Id());
                newTasks3.setStatuses3(statuses3);
            } catch (Exception e) {
                throw new StatusNotExistException("Status Does not exist");
            }
        }
        return tasks3Repository.saveAndFlush(newTasks3);
    }

}
