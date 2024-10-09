package sit.int221.services;

import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import sit.int221.dtos.request.NewTask3DTO;
import sit.int221.entities.primary.*;
import sit.int221.exceptions.ItemNotFoundException;
import sit.int221.exceptions.StatusNotExistException;
import sit.int221.exceptions.TaskNotFoundException;
import sit.int221.repositories.primary.BoardRepository;
import sit.int221.repositories.primary.CollaboratorRepository;
import sit.int221.repositories.primary.Tasks3Repository;

import java.util.Arrays;
import java.util.List;

@Service
public class Tasks3Service {
    @Autowired
    Tasks3Repository tasks3Repository;
    @Autowired
    Statuses3Service statuses3Service;
    @Autowired
    BoardRepository boardRepository;
    @Autowired
    CollaboratorRepository collaboratorRepository;
    @Autowired
    AuthorizationService authorizationService;


    // need to fix allow access by board's collaborator as well #Checked
    public List<Tasks3> getFilterTasksAndSorted(Claims claims, String sortBy, String[] filterStatuses, String direction, String boardId) {
        Sort sort = direction.equalsIgnoreCase("ASC") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Board board = authorizationService.getBoardId(boardId);

        String oid = (String) claims.get("oid");
        boolean isCollaborator = collaboratorRepository.findByBoardIdAndLocalUserOid(board.getId(), oid).isPresent();
        if (oid.equals(board.getOwnerId()) || isCollaborator) {
            List<Tasks3> allTasksSorted = tasks3Repository.findAllByBoard(board, sort);
            if (filterStatuses.length > 0) {
                List<String> filterStatusList = Arrays.asList(filterStatuses);
                return allTasksSorted.stream().filter(tasks3 -> filterStatusList.contains(tasks3.getStatuses3().getName())).toList();
            }
            return allTasksSorted;
        } else {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Cannot access tasks: board visibility is PRIVATE or You are not collaborator");
        }
    }

    public List<Tasks3> getFilterTasksAndSorted(String sortBy, String[] filterStatuses, String direction, String boardId) {
        Sort sort = direction.equalsIgnoreCase("ASC") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Board board = authorizationService.getBoardId(boardId);
        List<Tasks3> allTasksSorted = tasks3Repository.findAllByBoard(board, sort);
        if (filterStatuses.length > 0) {
            List<String> filterStatusList = Arrays.asList(filterStatuses);
            return allTasksSorted.stream().filter(tasks3 -> filterStatusList.contains(tasks3.getStatuses3().getName())).toList();
        }
        return allTasksSorted;
    }

    public Tasks3 findTask3ById(Claims claims, String boardId, Integer taskId) {
        Board board = authorizationService.getBoardId(boardId);
        String oid = (String) claims.get("oid");
        boolean isCollaborator = collaboratorRepository.findByBoardIdAndLocalUserOid(board.getId(), oid).isPresent();

        if (oid.equals(board.getOwnerId()) || isCollaborator) {
            Tasks3 tasks3Id = tasks3Repository.findById(taskId).orElseThrow(() -> new ItemNotFoundException("Task id " + taskId + " not found"));
            return checkTasksThatBelongsToBoard(tasks3Id, board.getId());
        } else {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Cannot access task: board visibility is PRIVATE or You are not collaborator");
        }
    }

    public Tasks3 findTask3ById(String boardId, Integer taskId) {
        Board board = authorizationService.getBoardId(boardId);
        Tasks3 tasks3Id = tasks3Repository.findById(taskId).orElseThrow(() -> new ItemNotFoundException("Task id " + taskId + " not found"));
        return checkTasksThatBelongsToBoard(tasks3Id, board.getId());
    }

    public Tasks3 createNewTaskByBoardId(Claims claims, String boardId, NewTask3DTO tasks3) {
        Board board = authorizationService.getBoardId(boardId);
        String oid = (String) claims.get("oid");
        if (oid.equals(board.getOwnerId())) {
            Tasks3 newTasks3 = new Tasks3();
            newTasks3.setBoard(authorizationService.getBoardId(boardId));
            newTasks3.setTitle(tasks3.getTitle().trim());
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
            if (tasks3.getTitle() == null || tasks3.getTitle().isBlank()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
            }
            if (tasks3.getStatus3() == null) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Status Does not exist");
            } else {
                try {
                    Statuses3 statuses3 = statuses3Service.findStatusByOnlyId(tasks3.getStatus3());
                    newTasks3.setStatuses3(statuses3);
                } catch (Exception e) {
                    throw new StatusNotExistException("Status Does not exist");
                }
            }
            return tasks3Repository.saveAndFlush(newTasks3);
        } else {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You are not allow to access this board");
        }
    }

    public Tasks3 removeTask3ById(Claims claims, String boardId, Integer taskId) {
        Board board = authorizationService.getBoardId(boardId);
        String oid = (String) claims.get("oid");
        if (oid.equals(board.getOwnerId())) {
            authorizationService.checkIdThatBelongsToUser(claims, boardId);
            Tasks3 tasks3Delete = tasks3Repository.findById(taskId).orElseThrow(() -> new ItemNotFoundException("Task id " + taskId + " not found"));
            tasks3Repository.deleteById(tasks3Delete.getId());
            return checkTasksThatBelongsToBoard(tasks3Delete, board.getId());
        } else {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You are not allow to access this board");
        }

    }

    public Tasks3 updateTask3(Claims claims, String boardId, Integer taskId, NewTask3DTO newTaskData) {
        Board board = authorizationService.getBoardId(boardId);
        String oid = (String) claims.get("oid");
        if (oid.equals(board.getOwnerId())) {
            Tasks3 tasks3Update = tasks3Repository.findById(taskId).orElseThrow(
                    () -> new TaskNotFoundException("Task id " + taskId + " not found"));
            checkTasksThatBelongsToBoard(tasks3Update, board.getId());
            tasks3Update.setTitle(newTaskData.getTitle().trim());
            if (newTaskData.getAssignees() != null && !newTaskData.getAssignees().isBlank()) {
                tasks3Update.setAssignees(newTaskData.getAssignees().trim());
            } else {
                tasks3Update.setAssignees(null);
            }
            if (newTaskData.getDescription() != null && !newTaskData.getDescription().isBlank()) {
                tasks3Update.setDescription(newTaskData.getDescription().trim());
            } else {
                tasks3Update.setDescription(null);
            }
            if (newTaskData.getStatus3() == null || newTaskData.getStatus3() < 0) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No Status Selected");
            } else {
                try {
                    Statuses3 statuses3 = statuses3Service.findStatusByOnlyId(newTaskData.getStatus3());
                    tasks3Update.setStatuses3(statuses3);
                } catch (Exception e) {
                    throw new StatusNotExistException("Status Does not exist");
                }
            }
            tasks3Repository.save(tasks3Update);
            return tasks3Update;
        } else {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You are not allow to access this board");
        }
    }

    public Tasks3 checkTasksThatBelongsToBoard(Tasks3 tasks3, String boardId) {
        if (!tasks3.getBoard().getId().equals(boardId)) {
            throw new ItemNotFoundException("Task id " + tasks3.getId() + " does not belong to Board id " + boardId);
        }
        return tasks3;
    }

}
