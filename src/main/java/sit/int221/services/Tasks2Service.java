package sit.int221.services;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import sit.int221.dtos.request.NewTask2DTO;
import sit.int221.entities.Statuses;
import sit.int221.entities.Tasks2;
import sit.int221.exceptions.ErrorResponse;
import sit.int221.exceptions.StatusNotExistException;
import sit.int221.exceptions.StatusNotFoundException;
import sit.int221.exceptions.TaskNotFoundException;
import sit.int221.repositories.Task2Repository;


import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class Tasks2Service {
    @Autowired
    Task2Repository task2Repository;
    @Autowired
    StatusesService statusesService;
    @Autowired
    ModelMapper modelMapper;

    public List<Tasks2> getFilterTasksAndSorted(String sortBy, String[] filterStatuses, String direction) {
        Sort sort = direction.equalsIgnoreCase("ASC") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        List<Tasks2> allTasksSorted = task2Repository.findAll(sort);
        if (filterStatuses.length > 0) {
            List<String> filterStatusList = Arrays.asList(filterStatuses);
            return allTasksSorted.stream().filter(tasks2 -> filterStatusList.contains(tasks2.getStatus().getName())).toList();
        }
        return allTasksSorted;
    }

    public Tasks2 findTask2ById(Integer taskId) {
        return task2Repository.findById(taskId).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Task2 " + taskId + " Doesn't Exist!!!"));
    }

    public Tasks2 insertTask2(NewTask2DTO tasks2) {
        Tasks2 newTasks2 = new Tasks2();
        newTasks2.setTitle(tasks2.getTitle().trim());
        if (tasks2.getDescription() != null && !tasks2.getDescription().isBlank()) {
            newTasks2.setDescription(tasks2.getDescription().trim());
        } else {
            newTasks2.setDescription(null);
        }
        if (tasks2.getAssignees() != null && !tasks2.getAssignees().isBlank()) {
            newTasks2.setAssignees(tasks2.getAssignees().trim());
        } else {
            newTasks2.setAssignees(null);
        }
        newTasks2.setStatus(new Statuses());
        if (tasks2.getStatus() == null || tasks2.getStatus() < 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Status Does not exist");
        } else {
            try{
                Statuses statuses = statusesService.findStatusById(tasks2.getStatus());
                newTasks2.setStatus(statuses);
            } catch (Exception e){
                Map<String, Object> body = new HashMap<>();
                body.put("timestamp", LocalDateTime.now().toString());
                body.put("status", HttpStatus.BAD_REQUEST.value());
                body.put("error", "Bad Request");
                body.put("message", "Status Does not exist");
                body.put("field", "status");
                body.put("path", "/v2/tasks");
                throw new StatusNotExistException("status", "Status Does not exist");
            }
        }
        return task2Repository.saveAndFlush(newTasks2);

    }

    public Tasks2 removeTask2(Integer taskId) {
        Tasks2 findTasks = task2Repository.findById(taskId).orElseThrow(
                () -> new TaskNotFoundException("NOT FOUND"));
        task2Repository.deleteById(taskId);
        return findTasks;
    }

    public Tasks2 updateTask2(Integer taskId, NewTask2DTO newTaskData) {
        Tasks2 findTasks = task2Repository.findById(taskId).orElseThrow(
                () -> new TaskNotFoundException("NOT FOUND"));
        findTasks.setTitle(newTaskData.getTitle().trim());
        if (newTaskData.getAssignees() != null && !newTaskData.getAssignees().isBlank()) {
            findTasks.setAssignees(newTaskData.getAssignees().trim());
        } else {
            findTasks.setAssignees(null);
        }
        if (newTaskData.getDescription() != null && !newTaskData.getDescription().isBlank()) {
            findTasks.setDescription(newTaskData.getDescription().trim());
        } else {
            findTasks.setDescription(null);
        }
        if (newTaskData.getStatus() == null || newTaskData.getStatus() < 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No Status I SUS");
        } else {
            Statuses statuses = statusesService.findStatusById(newTaskData.getStatus());
            findTasks.setStatus(statuses);
        }
        task2Repository.save(findTasks);
        return findTasks;
    }

}
