package sit.int221.primary.controllers;

import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sit.int221.dtos.request.NewTask2DTO;
import sit.int221.dtos.response.Task2DetailDTO;
import sit.int221.dtos.response.Task2FilterDTO;
import sit.int221.dtos.response.Task2HomeDTO;
import sit.int221.primary.entities.Tasks2;
import sit.int221.services.ListMapper;
import sit.int221.services.Tasks2Service;

import java.util.List;

@CrossOrigin(origins = {"http://localhost:5173","http://intproj23.sit.kmutt.ac.th","http://localhost:8080" ,"http://ip23tt1.sit.kmutt.ac.th","http://ip23tt1.sit.kmutt.ac.th:1449", "http://intproj23.sit.kmutt.ac.th:8080", "http://10.5.5.180:5173"})
@RestController
@RequestMapping("/v2/tasks")
public class Tasks2Controller {
    @Autowired
    Tasks2Service tasks2Service;
    @Autowired
    ListMapper listMapper;
    @Autowired
    ModelMapper modelMapper;

    @GetMapping("")
    public ResponseEntity<Object> getAllTasksSortedAndFiltered(
            @RequestParam(defaultValue = "createdOn") String sortBy,
            @RequestParam(defaultValue = "") String[] filterStatuses,
            @RequestParam(defaultValue = "ASC") String direction
    ) {
        List<Tasks2> tasksList = tasks2Service.getFilterTasksAndSorted(sortBy, filterStatuses, direction);
        if(!sortBy.isEmpty() && filterStatuses.length > 0 && !direction.isEmpty() ){
            List<Task2FilterDTO> task2FilterDTOS = listMapper.mapList(tasksList, Task2FilterDTO.class, modelMapper);
            return  ResponseEntity.ok(task2FilterDTOS);
        }
        return ResponseEntity.ok(listMapper.mapList(tasksList, Task2HomeDTO.class, modelMapper));
    }

    @GetMapping("/{taskId}")
    public Tasks2 findTaskById(@PathVariable Integer taskId) {
        return tasks2Service.findTask2ById(taskId);
    }

    @PostMapping("")
    @ResponseStatus(HttpStatus.CREATED)
    public Task2DetailDTO createTask(@Valid @RequestBody NewTask2DTO tasks){
        Tasks2 insertedTask = tasks2Service.insertTask2(tasks);
        return modelMapper.map(insertedTask, Task2DetailDTO.class);
    }

    @DeleteMapping("/{taskId}")
    public Task2HomeDTO deleteTask(@PathVariable Integer taskId){
        Tasks2 deleteTask = tasks2Service.removeTask2(taskId);
        return modelMapper.map(deleteTask, Task2HomeDTO.class);
    }

    @PutMapping("/{taskId}")
    public Task2DetailDTO putTask(@PathVariable Integer taskId,@Valid @RequestBody NewTask2DTO newTaskData){
        Tasks2 updateTask = tasks2Service.updateTask2(taskId, newTaskData);
        return modelMapper.map(updateTask, Task2DetailDTO.class);
    }


}
