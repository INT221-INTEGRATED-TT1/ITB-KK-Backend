package sit.int221.services;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import sit.int221.dtos.request.LimitStatusMaskReq;
import sit.int221.dtos.request.NewStatusDTO;
import sit.int221.dtos.response.LimitStatusMaskRes;
import sit.int221.dtos.response.StatusHomeCountDTO;
import sit.int221.entities.Statuses;
import sit.int221.entities.Tasks;
import sit.int221.exceptions.StatusNotFoundException;
import sit.int221.exceptions.StatusUniqueException;
import sit.int221.exceptions.TaskNotFoundException;
import sit.int221.repositories.StatusesRepository;
import sit.int221.repositories.Task2Repository;

import java.util.ArrayList;
import java.util.List;

@Service
public class StatusesService {
    @Autowired
    StatusesRepository statusesRepository;
    @Autowired
    Task2Repository task2Repository;
    @Autowired
    ModelMapper modelMapper;

    public List<Statuses> getAllStatusesList() {
        return statusesRepository.findAll();
    }

    public Statuses findStatusById(Integer statusID) {
        return statusesRepository.findById(statusID).orElseThrow(
                () -> new StatusNotFoundException("NOT FOUND"));
    }

    public Statuses findStatusByName(String statusName) {
        return statusesRepository.findByName(statusName);
    }

    public Statuses insertStatus(NewStatusDTO newStatusDTO) {
        newStatusDTO.setName(newStatusDTO.getName().trim());
        if (findStatusByName(newStatusDTO.getName()) != null) {
            throw new StatusUniqueException("Status name must be unique");
        }
        if (newStatusDTO.getDescription() != null && !newStatusDTO.getDescription().isBlank()) {
            newStatusDTO.setDescription(newStatusDTO.getDescription().trim());
        } else {
            newStatusDTO.setDescription(null);
        }
        Statuses statuses = modelMapper.map(newStatusDTO, Statuses.class);
        return statusesRepository.saveAndFlush(statuses);
    }

    public void removeStatus(Integer statusId) {
        Statuses findStatus = findStatusById(statusId);
        if (findStatus.getName().equalsIgnoreCase("no status")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No Status cannot be deleted.");
        } else if (findStatus.getName().equalsIgnoreCase("done")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Done cannot be deleted.");
        }
        try {
            statusesRepository.deleteById(statusId);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "destination status for task transfer not specified.");
        }
    }

    @Transactional
    public void updateTasksStatusAndDelete(Integer oldStatus, Integer newStatus) {
        findStatusById(oldStatus);
        if(oldStatus == newStatus){throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "destination status for task transfer must be different from current status.");}
        try {
            task2Repository.transferStatusAllBy(newStatus, oldStatus);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "the specified status for task transfer does not exist.");
        }
        removeStatus(oldStatus);
    }

    public Statuses updateStatus(Integer statusId, NewStatusDTO newStatus) {
        Statuses findStatus = findStatusById(statusId);
        if (findStatus.getName().equalsIgnoreCase("no status")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No Status cannot be modified.");
        } else if (findStatus.getName().equalsIgnoreCase("done")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Done cannot be modified.");
        }
        if ((findStatusByName(newStatus.getName()) != null) && !(findStatus.getName().equals(newStatus.getName()))) {
            throw new StatusUniqueException("Status name must be unique");
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
        statusesRepository.save(findStatus);
        return findStatus;
    }

    public List<StatusHomeCountDTO> getStatusWithCountTasksInUse() {
        List<Statuses> statusesList = getAllStatusesList();
        List<StatusHomeCountDTO> statusHomeCountDTOS = new ArrayList<>();
        for (int i = 0; i < statusesList.stream().count(); i++) {
            StatusHomeCountDTO statusHomeCountDTO = new StatusHomeCountDTO();
            statusHomeCountDTO.setId(statusesList.get(i).getId());
            statusHomeCountDTO.setName(statusesList.get(i).getName());
            statusHomeCountDTO.setDescription(statusesList.get(i).getDescription());
            statusHomeCountDTO.setColor(statusesList.get(i).getColor());
            statusHomeCountDTO.setCount(task2Repository.countByStatus(statusesList.get(i)));
            statusHomeCountDTOS.add(statusHomeCountDTO);
        }
        return statusHomeCountDTOS;
    }

    public LimitStatusMaskRes toggleLimitStatusMask(LimitStatusMaskReq limitStatusMaskReq) {
        LimitStatusMaskRes limitStatusMaskRes = new LimitStatusMaskRes();
        limitStatusMaskRes.setName("Limit Task Status");
        limitStatusMaskRes.setLimit(limitStatusMaskReq.getLimit());
        limitStatusMaskRes.setLimitMaximumTask(limitStatusMaskReq.getLimitMaximumTask());
        return limitStatusMaskRes;
    }
}
