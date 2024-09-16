package sit.int221.services;

import io.jsonwebtoken.Claims;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import sit.int221.dtos.request.NewStatus3DTO;
import sit.int221.dtos.response.Status3HomeCountDTO;
import sit.int221.entities.primary.Board;
import sit.int221.entities.primary.Statuses3;
import sit.int221.exceptions.ItemNotFoundException;
import sit.int221.exceptions.StatusNotFoundException;
import sit.int221.exceptions.StatusUniqueException;
import sit.int221.repositories.primary.*;

import java.util.ArrayList;
import java.util.List;

@Service
public class Statuses3Service {
    @Autowired
    Statuses3Repository statuses3Repository;
    @Autowired
    Tasks3Repository task3Repository;
    @Autowired
    BoardRepository boardRepository;
    @Autowired
    AuthorizationService authorizationService;
    @Autowired
    ModelMapper modelMapper;

    public List<Status3HomeCountDTO> getAllStatusesWithCountTasksInUse(Claims claims, String boardId) {
        authorizationService.checkIdThatBelongsToUser(claims, boardId);
        Board board = authorizationService.getBoardId(boardId);
        List<Statuses3> statuses3List = statuses3Repository.findAllByBoardId(board);
        List<Status3HomeCountDTO> statusHomeCountDTOS = new ArrayList<>();
        for (int i = 0; i < statuses3List.stream().count(); i++) {
            Status3HomeCountDTO status3HomeCountDTO = new Status3HomeCountDTO();
            status3HomeCountDTO.setId(statuses3List.get(i).getStatusID());
            status3HomeCountDTO.setName(statuses3List.get(i).getStatusName());
            status3HomeCountDTO.setDescription(statuses3List.get(i).getStatusDescription());
            status3HomeCountDTO.setColor(statuses3List.get(i).getStatusColor());
            status3HomeCountDTO.setCount(task3Repository.countByStatuses3(statuses3List.get(i)));
            statusHomeCountDTOS.add(status3HomeCountDTO);
        }
        return statusHomeCountDTOS;
    }

    public Statuses3 findStatusById(Claims claims, String boardId, Integer statusId) {
        authorizationService.checkIdThatBelongsToUser(claims, boardId);
        Board board = authorizationService.getBoardId(boardId);
        Statuses3 statuses3Id = statuses3Repository.findById(statusId).orElseThrow(() -> new StatusNotFoundException("Status id " + statusId + " not found"));
        return checkStatusesThatBelongsToBoard(statuses3Id, board.getBoardID());
    }

    public Statuses3 findStatusByOnlyId(Integer statusId) {
        return statuses3Repository.findById(statusId).orElseThrow(() -> new StatusNotFoundException("Status id " + statusId + " not found"));
    }

    public Statuses3 checkStatusesThatBelongsToBoard(Statuses3 statuses3, String boardId) {
        if (!statuses3.getBoardId().getBoardID().equals(boardId)) {
            throw new ItemNotFoundException("Status id " + statuses3.getStatusID() + " does not belong to Board id " + boardId);
        }
        return statuses3;
    }

    public Statuses3 insertStatus(Claims claims, String boardId, NewStatus3DTO newStatusDTO) {
        authorizationService.checkIdThatBelongsToUser(claims, boardId);
        newStatusDTO.setName(newStatusDTO.getName().trim());
        if (statuses3Repository.existsByStatusNameAndBoardId(newStatusDTO.getName(), authorizationService.getBoardId(boardId))) {
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
    }

    public NewStatus3DTO insertDefault(String boardId) {
        // Default Status
        String[] defaultStatus = {"No Status", "To Do", "Doing", "Done"};
        String[] defaultColor = {"#5A5A5A", "#0090FF", "#E9EB87", "#1A9338"};
        String[] defaultDescription = {"A status has not been assigned", "The task is included in the project", "The task is being worked on", "The task has been completed"};
//        if(defaultStatus.length != defaultColor.length || defaultColor.length != defaultDescription.length){
//            throw new RuntimeException("Status name not belongs to color and description");
//        }
        NewStatus3DTO newStatus3DTO = new NewStatus3DTO();

        for (int i = 0; i < 4; i++) {
            newStatus3DTO.setName(defaultStatus[i]);
            newStatus3DTO.setColor(defaultColor[i]);
            newStatus3DTO.setDescription(defaultDescription[i]);
            Statuses3 statuses3 = modelMapper.map(newStatus3DTO, Statuses3.class);
            statuses3.setBoardId(authorizationService.getBoardId(boardId));
            statuses3Repository.saveAndFlush(statuses3);
        }
        return newStatus3DTO;
    }

    public Statuses3 removeStatus(Claims claims, String boardId, Integer statusId) {
        authorizationService.checkIdThatBelongsToUser(claims, boardId);
        Board board = authorizationService.getBoardId(boardId);
        Statuses3 statuses3Id = statuses3Repository.findById(statusId).orElseThrow(() -> new StatusNotFoundException("Status id " + statusId + " not found"));
        checkStatusesThatBelongsToBoard(statuses3Id, board.getBoardID());
        Statuses3 findStatus = findStatusByOnlyId(statusId);
        if (findStatus.getStatusName().equalsIgnoreCase("no status")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No Status cannot be deleted.");
        } else if (findStatus.getStatusName().equalsIgnoreCase("done")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Done cannot be deleted.");
        }
        try {
            statuses3Repository.deleteById(statusId);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "destination status for task transfer not specified.");
        }
        return statuses3Id;
    }

    public void removeOldStatus(Integer statusId) {
        Statuses3 findStatus = findStatusByOnlyId(statusId);
        if (findStatus.getStatusName().equalsIgnoreCase("no status")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No Status cannot be deleted.");
        } else if (findStatus.getStatusName().equalsIgnoreCase("done")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Done cannot be deleted.");
        }
        try {
            statuses3Repository.deleteById(statusId);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "destination status for task transfer not specified.");
        }
    }

    @Transactional(transactionManager = "primaryTransactionManager")
    public Statuses3 updateTasksStatusAndDelete(Claims claims, String boardId, Integer oldStatus, Integer newStatus) {
        authorizationService.checkIdThatBelongsToUser(claims, boardId);
        Board board = authorizationService.getBoardId(boardId);
        Statuses3 statuses3Id = statuses3Repository.findById(oldStatus).orElseThrow(() -> new StatusNotFoundException("Status id " + oldStatus + " not found"));
        checkStatusesThatBelongsToBoard(statuses3Id, board.getBoardID());
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
    }

    public Statuses3 updateStatus(Claims claims, String boardId, Integer statusId, NewStatus3DTO newStatus) {
        authorizationService.checkIdThatBelongsToUser(claims, boardId);
        Board board = authorizationService.getBoardId(boardId);
        Statuses3 statuses3Id = statuses3Repository.findById(statusId).orElseThrow(() -> new StatusNotFoundException("Status id " + statusId + " not found"));
        checkStatusesThatBelongsToBoard(statuses3Id, board.getBoardID());
        Statuses3 findStatus = findStatusByOnlyId(statusId);
        if (findStatus.getStatusName().equalsIgnoreCase("no status")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No Status cannot be modified.");
        } else if (findStatus.getStatusName().equalsIgnoreCase("done")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Done cannot be modified.");
        }
        if (statuses3Repository.existsByStatusNameAndBoardId(newStatus.getName(), board)) {
            throw new StatusUniqueException("Status name must be unique within the board");
        }
        if (newStatus.getName() != null && !newStatus.getName().trim().isEmpty()) {
            findStatus.setStatusName(newStatus.getName().trim());
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Status name does not empty");
        }
        if (newStatus.getDescription() != null && !newStatus.getDescription().isBlank()) {
            findStatus.setStatusDescription(newStatus.getDescription().trim());
        } else {
            findStatus.setStatusDescription(null);
        }
        if (newStatus.getColor() != null && !newStatus.getColor().isBlank()) {
            findStatus.setStatusColor(newStatus.getColor().trim());
        } else {
            findStatus.setStatusColor(null);
        }
        statuses3Repository.save(findStatus);
        return findStatus;
    }

//    public LimitStatusMaskRes toggleLimitStatusMask(LimitStatusMaskReq limitStatusMaskReq) {
//        LimitStatusMaskRes limitStatusMaskRes = new LimitStatusMaskRes();
//        limitStatusMaskRes.setName("Limit Task Status");
//        limitStatusMaskRes.setLimit(limitStatusMaskReq.getLimit());
//        limitStatusMaskRes.setLimitMaximumTask(limitStatusMaskReq.getLimitMaximumTask());
//        return limitStatusMaskRes;
//    }
}
