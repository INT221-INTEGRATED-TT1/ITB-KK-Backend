package sit.int221.services;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import sit.int221.dtos.request.NewTaskDTO;
import sit.int221.primary.entities.TaskStatus;
import sit.int221.primary.entities.Tasks;
import sit.int221.exceptions.TaskNotFoundException;
import sit.int221.primary.repositories.TasksRepository;


import java.util.List;

@Service
public class TasksService {
    @Autowired
    TasksRepository tasksRepository;
    @Autowired
    ModelMapper modelMapper;

    public List<Tasks> getAllTasksList() {
        return tasksRepository.findAll();
    }

    public Tasks findTaskById(Integer taskId) {
        return tasksRepository.findById(taskId).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND,"Task "+ taskId + " Doesn't Exist!!!"));
    }

    public Tasks insertTask(NewTaskDTO newTaskDTO){
        newTaskDTO.setTitle(newTaskDTO.getTitle().trim());
        if(newTaskDTO.getDescription() != null && !newTaskDTO.getDescription().isBlank()){newTaskDTO.setDescription(newTaskDTO.getDescription().trim());}
        else{newTaskDTO.setDescription(null);}
        if(newTaskDTO.getAssignees() != null && !newTaskDTO.getAssignees().isBlank()){newTaskDTO.setAssignees(newTaskDTO.getAssignees().trim());}
        else{newTaskDTO.setAssignees(null);}
        if(newTaskDTO.getStatus() == null || newTaskDTO.getStatus().toString().trim().isEmpty()){newTaskDTO.setStatus(TaskStatus.NO_STATUS);}
        Tasks task = modelMapper.map(newTaskDTO, Tasks.class);
        return tasksRepository.saveAndFlush(task);
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
        findTasks.setTitle(newTaskData.getTitle().trim());
        findTasks.setAssignees(newTaskData.getAssignees().trim());
        findTasks.setDescription(newTaskData.getDescription().trim());
        findTasks.setStatus(newTaskData.getStatus());
        tasksRepository.save(findTasks);
        return findTasks;
    }

}
