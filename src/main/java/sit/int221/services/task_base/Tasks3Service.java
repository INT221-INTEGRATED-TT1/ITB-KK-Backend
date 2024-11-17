package sit.int221.services.task_base;

import io.jsonwebtoken.Claims;
import lombok.Data;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import sit.int221.dtos.request.NewTask3DTO;
import sit.int221.dtos.response.TaskListDTO;
import sit.int221.entities.enums.InvitationStatus;
import sit.int221.entities.task_base.*;
import sit.int221.exceptions.ItemNotFoundException;
import sit.int221.exceptions.StatusNotExistException;
import sit.int221.exceptions.TaskNotFoundException;
import sit.int221.properties.FileStorageProperties;
import sit.int221.repositories.task_base.BoardRepository;
import sit.int221.repositories.task_base.CollaboratorRepository;
import sit.int221.repositories.task_base.Tasks3Repository;
import sit.int221.services.ListMapper;
import sit.int221.services.itbkk_shared.AuthorizationService;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Data
@Service
public class Tasks3Service {
    private Path fileStorageLocation;

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
    @Autowired
    ListMapper listMapper;
    @Autowired
    ModelMapper modelMapper;

    @Autowired
    public void FileService(FileStorageProperties fileStorageProperties) {
        this.fileStorageLocation = Paths.get(fileStorageProperties
                .getUploadDir()).toAbsolutePath().normalize();
        try {
            if (!Files.exists(this.fileStorageLocation)) {
                Files.createDirectories(this.fileStorageLocation);
            }
        } catch (IOException ex) {
            throw new RuntimeException(
                    "Could not create the directory where the uploaded files will be stored.", ex);
        }
    }

    public List<TaskListDTO> getFilterTasksAndSorted(Claims claims, String sortBy, String[] filterStatuses, String direction, String boardId) {
        Sort sort = direction.equalsIgnoreCase("ASC") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Board board = authorizationService.getBoardId(boardId);

        String oid = (String) claims.get("oid");
        Optional<Collaborator> collaboratorOpt = collaboratorRepository.findByBoardIdAndLocalUserOid(board.getId(), oid);

        boolean isCollaboratorAccepted = collaboratorOpt
                .map(collaborator -> InvitationStatus.ACCEPTED.equals(collaborator.getInvitationStatus()))
                .orElse(false);

        if (oid.equals(board.getOwnerId()) || isCollaboratorAccepted) {
            List<Tasks3> allTasksSorted = tasks3Repository.findAllByBoard(board, sort);
            if (filterStatuses.length > 0) {
                List<String> filterStatusList = Arrays.asList(filterStatuses);
                allTasksSorted = allTasksSorted.stream().filter(tasks3 -> filterStatusList.contains(tasks3.getStatuses3().getName())).toList();
            }
            return allTasksSorted.stream()
                    .map(task -> {
                        TaskListDTO dto = modelMapper.map(task, TaskListDTO.class);
                        int attachmentCount = countAttachment(board.getId(), task.getId());
                        // If no attachments, set the count to "-". -1 -> "-" on FE
                        dto.setAttachmentCount(attachmentCount > 0 ? attachmentCount : -1);
                        return dto;
                    })
                    .collect(Collectors.toList());
        } else {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Cannot access tasks: board visibility is PRIVATE or You are not collaborator");
        }
    }

    private int countAttachment(String boardId, Integer taskId) {
        Path taskStorageLocation = fileStorageLocation
                .resolve(boardId)
                .resolve(String.valueOf(taskId));
        try {
            if (Files.exists(taskStorageLocation)) {
                // Count only the regular files in the directory
                return (int) Files.list(taskStorageLocation)
                        .filter(Files::isRegularFile) // Filter for regular files
                        .count();
            }
        } catch (IOException ex) {
            throw new RuntimeException("Error counting attachments for taskId: " + taskId, ex);
        }
        return 0; // No attachments
    }

    public List<TaskListDTO> getFilterTasksAndSorted(String sortBy, String[] filterStatuses, String direction, String boardId) {
        Sort sort = direction.equalsIgnoreCase("ASC") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Board board = authorizationService.getBoardId(boardId);
        List<Tasks3> allTasksSorted = tasks3Repository.findAllByBoard(board, sort);
        if (filterStatuses.length > 0) {
            List<String> filterStatusList = Arrays.asList(filterStatuses);
            allTasksSorted = allTasksSorted.stream().filter(tasks3 -> filterStatusList.contains(tasks3.getStatuses3().getName())).toList();
        }
        return allTasksSorted.stream()
                .map(task -> {
                    TaskListDTO dto = modelMapper.map(task, TaskListDTO.class);
                    int attachmentCount = countAttachment(board.getId(), task.getId());
                    // If no attachments, set the count to "-". -1 -> "-" on FE
                    dto.setAttachmentCount(attachmentCount > 0 ? attachmentCount : -1);
                    return dto;
                })
                .collect(Collectors.toList());
    }

    public Tasks3 findTask3ById(Claims claims, String boardId, Integer taskId) {
        Board board = authorizationService.getBoardId(boardId);
        String oid = (String) claims.get("oid");
        Optional<Collaborator> collaboratorOpt = collaboratorRepository.findByBoardIdAndLocalUserOid(board.getId(), oid);
        boolean isCollaboratorAccepted = collaboratorOpt
                .map(collaborator -> InvitationStatus.ACCEPTED.equals(collaborator.getInvitationStatus()))
                .orElse(false);

        if (oid.equals(board.getOwnerId()) || isCollaboratorAccepted) {
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
        Optional<Collaborator> collaborator = collaboratorRepository.findByBoardIdAndLocalUserOid(boardId, oid);

        if (oid.equals(board.getOwnerId()) ||
                collaborator.isPresent() && collaborator.get().getAccessRight().equals("WRITE")) {
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
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Not authorized to create a task on this board");
        }
    }

    public Tasks3 removeTask3ById(Claims claims, String boardId, Integer taskId) {
        Board board = authorizationService.getBoardId(boardId);
        String oid = (String) claims.get("oid");
        Optional<Collaborator> collaborator = collaboratorRepository.findByBoardIdAndLocalUserOid(boardId, oid);

        if (oid.equals(board.getOwnerId()) || collaborator.isPresent() && collaborator.get().getAccessRight().equals("WRITE")) {
            Tasks3 tasks3Delete = tasks3Repository.findById(taskId).orElseThrow(() -> new ItemNotFoundException("Task id " + taskId + " not found"));
            checkTasksThatBelongsToBoard(tasks3Delete, boardId);
            deleteAttachmentsForTask(board.getId(), tasks3Delete.getId());
            tasks3Repository.deleteById(tasks3Delete.getId());
            return tasks3Delete;
        } else {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Not authorized to create a task on this board");
        }
    }

    private void deleteAttachmentsForTask(String boardId, Integer taskId) {
        Path taskStorageLocation = this.fileStorageLocation.resolve(boardId).resolve(String.valueOf(taskId));
        try {
            // Delete all files in the task's attachment directory
            if (Files.exists(taskStorageLocation)) {
                Files.walk(taskStorageLocation)
                        .sorted(Comparator.reverseOrder()) // Delete files first, then directories
                        .forEach(file -> {
                            try {
                                Files.delete(file); // Delete the file
                            } catch (IOException e) {
                                // Log the error if any file cannot be deleted
                                throw new RuntimeException("Failed to delete attachment: " + file, e);
                            }
                        });
            }
        } catch (IOException ex) {
            throw new RuntimeException("Failed to delete attachments for taskId: " + taskId, ex);
        }
    }

    public Tasks3 updateTask3(Claims claims, String boardId, Integer taskId, NewTask3DTO newTaskData) {
        Board board = authorizationService.getBoardId(boardId);
        String oid = (String) claims.get("oid");
        Optional<Collaborator> collaborator = collaboratorRepository.findByBoardIdAndLocalUserOid(boardId, oid);

        if (oid.equals(board.getOwnerId()) ||
                collaborator.isPresent() && collaborator.get().getAccessRight().equals("WRITE")) {
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
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Not authorized to create a task on this board");
        }
    }

    public Tasks3 checkTasksThatBelongsToBoard(Tasks3 tasks3, String boardId) {
        if (!tasks3.getBoard().getId().equals(boardId)) {
            throw new ItemNotFoundException("Task id " + tasks3.getId() + " does not belong to Board id " + boardId);
        }
        return tasks3;
    }

}
