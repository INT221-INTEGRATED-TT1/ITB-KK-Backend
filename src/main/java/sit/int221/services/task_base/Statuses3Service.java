package sit.int221.services.task_base;

import io.jsonwebtoken.Claims;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import sit.int221.dtos.request.NewStatus3DTO;
import sit.int221.dtos.response.Status3HomeCountDTO;
import sit.int221.entities.task_base.Board;
import sit.int221.entities.task_base.Collaborator;
import sit.int221.entities.task_base.Statuses3;
import sit.int221.exceptions.ItemNotFoundException;
import sit.int221.exceptions.StatusNotFoundException;
import sit.int221.exceptions.StatusUniqueException;
import sit.int221.repositories.task_base.*;
import sit.int221.services.itbkk_shared.AuthorizationService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class Statuses3Service {
    @Autowired
    Statuses3Repository statuses3Repository;
    @Autowired
    Tasks3Repository task3Repository;
    @Autowired
    BoardRepository boardRepository;
    @Autowired
    CollaboratorRepository collaboratorRepository;
    @Autowired
    AuthorizationService authorizationService;
    @Autowired
    ModelMapper modelMapper;

    public List<Status3HomeCountDTO> getAllStatusesWithCountTasksInUse(Claims claims, String boardId) {
        Board board = authorizationService.getBoardId(boardId);
        String oid = (String) claims.get("oid");
        boolean isCollaborator = collaboratorRepository.findByBoardIdAndLocalUserOid(board.getId(), oid).isPresent();

        if (oid.equals(board.getOwnerId()) || isCollaborator) {
            List<Statuses3> statuses3List = statuses3Repository.findAllByBoardId(board);
            List<Status3HomeCountDTO> statusHomeCountDTOS = new ArrayList<>();
            for (int i = 0; i < statuses3List.stream().count(); i++) {
                Status3HomeCountDTO status3HomeCountDTO = new Status3HomeCountDTO();
                status3HomeCountDTO.setId(statuses3List.get(i).getId());
                status3HomeCountDTO.setName(statuses3List.get(i).getName());
                status3HomeCountDTO.setDescription(statuses3List.get(i).getDescription());
                status3HomeCountDTO.setColor(statuses3List.get(i).getColor());
                status3HomeCountDTO.setCount(task3Repository.countByStatuses3(statuses3List.get(i)));
                statusHomeCountDTOS.add(status3HomeCountDTO);
            }
            return statusHomeCountDTOS;
        } else {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Cannot access statuses: board visibility is PRIVATE or You are not collaborator");
        }
    }

    public List<Status3HomeCountDTO> getAllStatusesWithCountTasksInUse(String boardId) {
        Board board = authorizationService.getBoardId(boardId);
        List<Statuses3> statuses3List = statuses3Repository.findAllByBoardId(board);
        List<Status3HomeCountDTO> statusHomeCountDTOS = new ArrayList<>();
        for (int i = 0; i < statuses3List.stream().count(); i++) {
            Status3HomeCountDTO status3HomeCountDTO = new Status3HomeCountDTO();
            status3HomeCountDTO.setId(statuses3List.get(i).getId());
            status3HomeCountDTO.setName(statuses3List.get(i).getName());
            status3HomeCountDTO.setDescription(statuses3List.get(i).getDescription());
            status3HomeCountDTO.setColor(statuses3List.get(i).getColor());
            status3HomeCountDTO.setCount(task3Repository.countByStatuses3(statuses3List.get(i)));
            statusHomeCountDTOS.add(status3HomeCountDTO);
        }
        return statusHomeCountDTOS;
    }

    public Statuses3 findStatusById(Claims claims, String boardId, Integer statusId) {
        Board board = authorizationService.getBoardId(boardId);
        String oid = (String) claims.get("oid");
        boolean isCollaborator = collaboratorRepository.findByBoardIdAndLocalUserOid(board.getId(), oid).isPresent();

        if (oid.equals(board.getOwnerId()) || isCollaborator) {
            Statuses3 statuses3Id = statuses3Repository.findById(statusId).orElseThrow(() -> new StatusNotFoundException("Status id " + statusId + " not found"));
            return checkStatusesThatBelongsToBoard(statuses3Id, board.getId());
        } else {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Cannot access status: board visibility is PRIVATE or You are not collaborator");
        }
    }

    public Statuses3 findStatusById(String boardId, Integer statusId) {
        Board board = authorizationService.getBoardId(boardId);
        Statuses3 statuses3Id = statuses3Repository.findById(statusId).orElseThrow(() -> new StatusNotFoundException("Status id " + statusId + " not found"));
        return checkStatusesThatBelongsToBoard(statuses3Id, board.getId());
    }

    public Statuses3 findStatusByOnlyId(Integer statusId) {
        return statuses3Repository.findById(statusId).orElseThrow(() -> new StatusNotFoundException("Status id " + statusId + " not found"));
    }

    public Statuses3 checkStatusesThatBelongsToBoard(Statuses3 statuses3, String boardId) {
        if (!statuses3.getBoardId().getId().equals(boardId)) {
            throw new ItemNotFoundException("Status id " + statuses3.getId() + " does not belong to Board id " + boardId);
        }
        return statuses3;
    }

    public Statuses3 insertStatus(Claims claims, String boardId, NewStatus3DTO newStatusDTO) {
        Board board = authorizationService.getBoardId(boardId);
        String oid = (String) claims.get("oid");
        Optional<Collaborator> collaborator = collaboratorRepository.findByBoardIdAndLocalUserOid(boardId, oid);

        if (oid.equals(board.getOwnerId()) || collaborator.isPresent() && collaborator.get().getAccessRight().equals("WRITE")) {
            newStatusDTO.setName(newStatusDTO.getName().trim());
            if (statuses3Repository.existsByNameAndBoardId(newStatusDTO.getName(), authorizationService.getBoardId(boardId))) {
                throw new StatusUniqueException("Status name must be unique within the board");
            }
            if (newStatusDTO.getDescription() != null && !newStatusDTO.getDescription().isBlank()) {
                newStatusDTO.setDescription(newStatusDTO.getDescription().trim());
            } else {
                newStatusDTO.setDescription(null);
            }
            Statuses3 statuses3 = modelMapper.map(newStatusDTO, Statuses3.class);
            statuses3.setBoardId(authorizationService.getBoardId(boardId));
            return statuses3Repository.saveAndFlush(statuses3);
        } else {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Not authorized to create a task on this board");
        }
    }

    public void insertDefaultStatus(String boardId) {
        // Default Status
        String[] defaultStatus = {"No Status", "To Do", "Doing", "Done"};
        String[] defaultColor = {"#5A5A5A", "#0090FF", "#E9EB87", "#1A9338"};
        String[] defaultDescription = {"A status has not been assigned", "The task is included in the project", "The task is being worked on", "The task has been completed"};
        NewStatus3DTO newStatus3DTO = new NewStatus3DTO();

        for (int i = 0; i < 4; i++) {
            newStatus3DTO.setName(defaultStatus[i]);
            newStatus3DTO.setColor(defaultColor[i]);
            newStatus3DTO.setDescription(defaultDescription[i]);
            Statuses3 statuses3 = modelMapper.map(newStatus3DTO, Statuses3.class);
            statuses3.setBoardId(authorizationService.getBoardId(boardId));
            statuses3Repository.saveAndFlush(statuses3);
        }
    }

    public Statuses3 removeStatus(Claims claims, String boardId, Integer statusId) {
        Board board = authorizationService.getBoardId(boardId);
        String oid = (String) claims.get("oid");
        Optional<Collaborator> collaborator = collaboratorRepository.findByBoardIdAndLocalUserOid(boardId, oid);

        if (oid.equals(board.getOwnerId()) || collaborator.isPresent() && collaborator.get().getAccessRight().equals("WRITE")) {
            Statuses3 statuses3Id = statuses3Repository.findById(statusId).orElseThrow(() -> new StatusNotFoundException("Status id " + statusId + " not found"));
            checkStatusesThatBelongsToBoard(statuses3Id, board.getId());
            Statuses3 findStatus = findStatusByOnlyId(statusId);
            if (findStatus.getName().equalsIgnoreCase("no status")) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No Status cannot be deleted.");
            } else if (findStatus.getName().equalsIgnoreCase("done")) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Done cannot be deleted.");
            }
            try {
                statuses3Repository.deleteById(statusId);
            } catch (Exception e) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "destination status for task transfer not specified.");
            }
            return statuses3Id;
        } else {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Not authorized to create a task on this board");
        }
    }

    public void removeOldStatus(Integer statusId) {
        Statuses3 findStatus = findStatusByOnlyId(statusId);
        if (findStatus.getName().equalsIgnoreCase("no status")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No Status cannot be deleted.");
        } else if (findStatus.getName().equalsIgnoreCase("done")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Done cannot be deleted.");
        }
        try {
            statuses3Repository.deleteById(statusId);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "destination status for task transfer not specified.");
        }
    }

    @Transactional(transactionManager = "itBkkTransactionManager")
    public Statuses3 updateTasksStatusAndDelete(Claims claims, String boardId, Integer oldStatus, Integer newStatus) {
        Board board = authorizationService.getBoardId(boardId);
        String oid = (String) claims.get("oid");
        Optional<Collaborator> collaborator = collaboratorRepository.findByBoardIdAndLocalUserOid(boardId, oid);

        if (oid.equals(board.getOwnerId()) || collaborator.isPresent() && collaborator.get().getAccessRight().equals("WRITE")) {
            Statuses3 statuses3Id = statuses3Repository.findById(oldStatus).orElseThrow(() -> new StatusNotFoundException("Status id " + oldStatus + " not found"));
            checkStatusesThatBelongsToBoard(statuses3Id, board.getId());
            findStatusByOnlyId(oldStatus);
            if (oldStatus == newStatus) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "destination status for task transfer must be different from current status.");
            }
            try {
                task3Repository.transferStatusAllBy(newStatus, oldStatus);
            } catch (Exception e) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "the specified status for task transfer does not exist.");
            }
            removeOldStatus(oldStatus);
            return statuses3Id;
        } else {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Not authorized to create a task on this board");
        }
    }

    public Statuses3 updateStatus(Claims claims, String boardId, Integer statusId, NewStatus3DTO newStatus) {
        Board board = authorizationService.getBoardId(boardId);
        String oid = (String) claims.get("oid");
        Optional<Collaborator> collaborator = collaboratorRepository.findByBoardIdAndLocalUserOid(boardId, oid);

        if (oid.equals(board.getOwnerId()) || collaborator.isPresent() && collaborator.get().getAccessRight().equals("WRITE")) {
            Statuses3 statuses3Id = statuses3Repository.findById(statusId).orElseThrow(() -> new StatusNotFoundException("Status id " + statusId + " not found"));
            checkStatusesThatBelongsToBoard(statuses3Id, board.getId());
            Statuses3 findStatus = findStatusByOnlyId(statusId);
            if (findStatus.getName().equalsIgnoreCase("no status")) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No Status cannot be modified.");
            } else if (findStatus.getName().equalsIgnoreCase("done")) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Done cannot be modified.");
            }
            if (statuses3Repository.existsByNameAndBoardId(newStatus.getName(), board)) {
                throw new StatusUniqueException("Status name must be unique within the board");
            }
            if (newStatus.getName() != null && !newStatus.getName().trim().isEmpty()) {
                findStatus.setName(newStatus.getName().trim());
            } else {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Status name does not empty");
            }
            if (newStatus.getDescription() != null && !newStatus.getDescription().isBlank()) {
                findStatus.setDescription(newStatus.getDescription().trim());
            } else {
                findStatus.setDescription(null);
            }
            if (newStatus.getColor() != null && !newStatus.getColor().isBlank()) {
                findStatus.setColor(newStatus.getColor().trim());
            } else {
                findStatus.setColor(null);
            }
            statuses3Repository.save(findStatus);
            return findStatus;
        } else {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Not authorized to create a task on this board");
        }
    }

//    public LimitStatusMaskRes toggleLimitStatusMask(LimitStatusMaskReq limitStatusMaskReq) {
//        LimitStatusMaskRes limitStatusMaskRes = new LimitStatusMaskRes();
//        limitStatusMaskRes.setName("Limit Task Status");
//        limitStatusMaskRes.setLimit(limitStatusMaskReq.getLimit());
//        limitStatusMaskRes.setLimitMaximumTask(limitStatusMaskReq.getLimitMaximumTask());
//        return limitStatusMaskRes;
//    }
}
