package sit.int221.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.server.ResponseStatusException;
import sit.int221.dtos.request.TaskReqDTO;
import sit.int221.dtos.response.TaskResDTO;
import sit.int221.entities.Tasks;
import sit.int221.exceptions.TaskNotFoundException;
import sit.int221.repositories.TasksRepository;


import java.util.List;

@Service

public class TasksService {
    @Autowired
    TasksRepository tasksRepository;

    public List<Tasks> getAllTasksList() {
        return tasksRepository.findAll();
    }

    public Tasks findTaskById(Integer taskId) {
        return tasksRepository.findById(taskId).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND,"Task "+ taskId + " Doesn't Exist!!!"));
    }

    public Tasks insertTask(Tasks tasks){
        return tasksRepository.save(tasks);
    }

    public Tasks removeTask(Integer taskId) {
        Tasks findTasks = tasksRepository.findById(taskId).orElseThrow(
                () -> new TaskNotFoundException("NOT FOUND"));
        tasksRepository.deleteById(taskId);
        return findTasks;
    }

    public Tasks updateTask(Integer taskId, Tasks newTaskData) {
        Tasks findTasks = tasksRepository.findById(taskId).orElseThrow(
                () -> new TaskNotFoundException("NOT FOUND"));
        findTasks.setTitle(newTaskData.getTitle());
        findTasks.setAssignees(newTaskData.getAssignees());
        findTasks.setDescription(newTaskData.getDescription());
        findTasks.setStatus(newTaskData.getStatus());
        tasksRepository.save(findTasks);
        return findTasks;
    }
}
