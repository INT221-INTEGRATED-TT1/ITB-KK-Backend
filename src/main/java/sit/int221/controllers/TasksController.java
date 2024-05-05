package sit.int221.controllers;

import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;
import sit.int221.dtos.request.NewTaskDTO;
import sit.int221.dtos.response.TaskHomeDTO;
import sit.int221.dtos.response.TaskDetailDTO;
import sit.int221.entities.Tasks;
import sit.int221.exceptions.ErrorResponse;
import sit.int221.exceptions.TaskNotFoundException;
import sit.int221.services.ListMapper;
import sit.int221.services.TasksService;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@CrossOrigin(origins = {"http://localhost:5173","http://localhost:5174","http://localhost:80" ,"http://ip23tt1.sit.kmutt.ac.th:80","http://ip23tt1.sit.kmutt.ac.th:1449", "http://192.168.2.87:5173"})
@RestController
@RequestMapping("/v1/tasks")
public class TasksController {
    @Autowired
    TasksService tasksService;
    @Autowired
    ListMapper listMapper;
    @Autowired
    ModelMapper modelMapper;

//    @Tag(name = "get", description = "GET methods of Tasks APIs")
    @GetMapping("")
    public List<TaskHomeDTO> getAllTasks() {
        List<Tasks> tasksList = tasksService.getAllTasksList();
        return listMapper.mapList(tasksList, TaskHomeDTO.class, modelMapper);
    }

//    @Tag(name = "get", description = "GET methods of Tasks APIs")
    @GetMapping("/{taskId}")
    public Tasks findTaskById(@PathVariable Integer taskId) {
        return tasksService.findTaskById(taskId);
    }

    @PostMapping("")
    @ResponseStatus(HttpStatus.CREATED)
    public TaskDetailDTO createTask(@Valid @RequestBody NewTaskDTO tasks){
        Tasks insertedTask = tasksService.insertTask(tasks);
        return modelMapper.map(insertedTask, TaskDetailDTO.class);
    }

    @DeleteMapping("/{taskId}")
    public TaskHomeDTO deleteTask(@PathVariable Integer taskId){
        Tasks deleteTask = tasksService.removeTask(taskId);
        return modelMapper.map(deleteTask, TaskHomeDTO.class);
    }

    @PutMapping("/{taskId}")
    public TaskDetailDTO putTask(@PathVariable Integer taskId, @RequestBody Tasks newTaskData){
        Tasks updateTask = tasksService.updateTask(taskId, newTaskData);
        return modelMapper.map(updateTask, TaskDetailDTO.class);
    }

    @ExceptionHandler(TaskNotFoundException.class)
    @ResponseStatus(code = HttpStatus.NOT_FOUND)
    public ResponseEntity<ErrorResponse> handleTaskNotFoundException(TaskNotFoundException ex, WebRequest request) {
        ZonedDateTime zonedDateTime = ZonedDateTime.now(ZoneOffset.UTC);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
        ErrorResponse errorResponse = new ErrorResponse(
                zonedDateTime.format(formatter),
                HttpStatus.NOT_FOUND.value(),
                ex.getMessage(),
                request.getDescription(false)
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }

}
