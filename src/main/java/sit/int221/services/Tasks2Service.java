package sit.int221.services;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import sit.int221.dtos.request.NewTask2DTO;
import sit.int221.entities.Statuses;
import sit.int221.entities.Tasks2;
import sit.int221.exceptions.TaskNotFoundException;
import sit.int221.repositories.Task2Repository;


import java.util.List;

@Service
public class Tasks2Service {
    @Autowired
    Task2Repository task2Repository;
    @Autowired
    ModelMapper modelMapper;

    public List<Tasks2> getAllTasks2List() {
        return task2Repository.findAll();
    }

    public Tasks2 findTask2ById(Integer taskId) {
        return task2Repository.findById(taskId).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND,"Task2 "+ taskId + " Doesn't Exist!!!"));
    }

    public Tasks2 insertTask2(NewTask2DTO tasks2){
        tasks2.setTitle(tasks2.getTitle().trim());
        if(tasks2.getDescription() != null && !tasks2.getDescription().isBlank()){tasks2.setDescription(tasks2.getDescription().trim());}
        else{tasks2.setDescription(null);}
        if(tasks2.getAssignees() != null && !tasks2.getAssignees().isBlank()){tasks2.setAssignees(tasks2.getAssignees().trim());}
        else{tasks2.setAssignees(null);}
        if(tasks2.getStatuses() == null){
            tasks2.setStatuses(new Statuses());
            tasks2.getStatuses().setId(101);
            tasks2.getStatuses().setName("No Status");
            tasks2.getStatuses().setDescription(null);
            tasks2.getStatuses().setColor("#5A5A5A");
        }
        Tasks2 task2 = modelMapper.map(tasks2, Tasks2.class);
        return task2Repository.saveAndFlush(task2);
    }

    public Tasks2 removeTask2(Integer taskId) {
        Tasks2 findTasks = task2Repository.findById(taskId).orElseThrow(
                () -> new TaskNotFoundException("NOT FOUND"));
        task2Repository.deleteById(taskId);
        return findTasks;
    }


    public Tasks2 updateTask2(Integer taskId, Tasks2 newTaskData) {
        Tasks2 findTasks = task2Repository.findById(taskId).orElseThrow(
                () -> new TaskNotFoundException("NOT FOUND"));
        findTasks.setTitle(newTaskData.getTitle().trim());
        findTasks.setAssignees(newTaskData.getAssignees().trim());
        findTasks.setDescription(newTaskData.getDescription().trim());
//        findTasks.setStatus(newTaskData.getStatus());
        if(findTasks.getStatuses() != newTaskData.getStatuses()){
            findTasks.getStatuses().setId(newTaskData.getStatuses().getId());
            findTasks.getStatuses().setName(newTaskData.getStatuses().getName());
            findTasks.getStatuses().setDescription(newTaskData.getStatuses().getDescription());
            findTasks.getStatuses().setColor(newTaskData.getStatuses().getColor());
        }
        task2Repository.save(findTasks);
        return findTasks;
    }

}
