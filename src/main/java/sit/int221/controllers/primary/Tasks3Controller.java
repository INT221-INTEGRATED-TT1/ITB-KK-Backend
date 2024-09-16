package sit.int221.controllers.primary;

import io.jsonwebtoken.Claims;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sit.int221.dtos.request.NewTask3DTO;
import sit.int221.dtos.response.TaskDetail3DTO;
import sit.int221.entities.primary.Tasks3;
import sit.int221.services.AuthorizationService;
import sit.int221.services.ListMapper;
import sit.int221.services.Tasks3Service;

@RestController
@CrossOrigin(origins = {"http://localhost:5173", "http://intproj23.sit.kmutt.ac.th", "http://localhost:80", "http://ip23tt1.sit.kmutt.ac.th", "http://ip23tt1.sit.kmutt.ac.th:1449", "http://intproj23.sit.kmutt.ac.th:8080"})
@RequestMapping("/v3/boards")
public class Tasks3Controller {
    @Autowired
    Tasks3Service tasks3Service;
    @Autowired
    AuthorizationService authorizationService;
    @Autowired
    ListMapper listMapper;
    @Autowired
    ModelMapper modelMapper;

    @GetMapping("/{boardId}/tasks")
    public ResponseEntity<Object> getAllTaskByBoardId(@RequestHeader("Authorization") String token, @PathVariable String boardId,@RequestParam(defaultValue = "createOn") String sortBy,
                                                          @RequestParam(defaultValue = "") String[] filterStatuses,
                                                          @RequestParam(defaultValue = "ASC") String direction) {
        Claims claims = authorizationService.validateToken(token);
        return ResponseEntity.ok(tasks3Service.getFilterTasksAndSorted(claims, sortBy,filterStatuses,direction,boardId)); 
    }


    @PostMapping("/{boardId}/tasks")
    @ResponseStatus(HttpStatus.CREATED)
    public TaskDetail3DTO createTaskWithBoardId(@RequestHeader("Authorization") String token, @PathVariable String boardId, @RequestBody NewTask3DTO task3DTO) {
        Claims claims = authorizationService.validateToken(token);
        Tasks3 tasks3 =  tasks3Service.createNewTaskByBoardId(claims, boardId, task3DTO);
        return modelMapper.map(tasks3, TaskDetail3DTO.class);
    }

    @GetMapping("/{boardId}/tasks/{taskId}")
    public Tasks3 getTaskById(@RequestHeader("Authorization") String token, @PathVariable String boardId, @PathVariable Integer taskId) {
        Claims claims = authorizationService.validateToken(token);
        Tasks3 findTasks3ById = tasks3Service.findTask3ById(claims, boardId, taskId);
        return modelMapper.map(findTasks3ById, Tasks3.class);
    }

    @PutMapping("/{boardId}/tasks/{taskId}")
    public TaskDetail3DTO updateTaskById(@RequestHeader("Authorization") String token, @PathVariable String boardId,
                                         @PathVariable Integer taskId,
                                         @RequestBody NewTask3DTO newTaskData) {
        Claims claims = authorizationService.validateToken(token);
        Tasks3 updateTask = tasks3Service.updateTask3(claims, boardId, taskId, newTaskData);
        return modelMapper.map(updateTask, TaskDetail3DTO.class);
    }

    @DeleteMapping("/{boardId}/tasks/{taskId}")
    public Tasks3 deleteTaskById(@RequestHeader("Authorization") String token, @PathVariable String boardId, @PathVariable Integer taskId) {
        Claims claims = authorizationService.validateToken(token);
        return tasks3Service.removeTask3ById(claims, boardId, taskId);
    }
}
