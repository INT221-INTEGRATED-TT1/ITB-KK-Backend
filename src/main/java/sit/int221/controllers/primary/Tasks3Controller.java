package sit.int221.controllers.primary;

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
//    public ResponseEntity<Object> getAllTaskByBoardId(@RequestHeader("Authorization") String token, @PathVariable String boardId) {
//        authorizationService.validateToken(token);
//        return ResponseEntity.ok(tasks3Service.getAllTaskByBoardId(boardId));
//    }
        public ResponseEntity<Object> getAllTaskByBoardId(@RequestHeader("Authorization") String token, @PathVariable String boardId,@RequestParam(defaultValue = "createOn") String sortBy,
                                                          @RequestParam(defaultValue = "") String[] filterStatuses,
                                                          @RequestParam(defaultValue = "ASC") String direction) {
        authorizationService.validateToken(token);
        return ResponseEntity.ok(tasks3Service.getFilterTasksAndSorted(sortBy,filterStatuses,direction,boardId));

    }


    @PostMapping("/{boardId}/tasks")
    @ResponseStatus(HttpStatus.CREATED)
    public TaskDetail3DTO createTaskWithBoardId(@RequestHeader("Authorization") String token, @PathVariable String boardId, @RequestBody NewTask3DTO task3DTO) {
        authorizationService.validateToken(token);
        Tasks3 tasks3 =  tasks3Service.createNewTaskByBoardId(boardId, task3DTO);
        return modelMapper.map(tasks3, TaskDetail3DTO.class);
    }

    @GetMapping("/{boardId}/tasks/{taskId}")
    public Tasks3 getTaskById(@RequestHeader("Authorization") String token, @PathVariable String boardId, @PathVariable Integer taskId) {
        authorizationService.validateToken(token);
        Tasks3 findTasks3ById = tasks3Service.findTask3ById(boardId, taskId);
        return modelMapper.map(findTasks3ById, Tasks3.class);
    }

    @PutMapping("/{boardId}/tasks/{taskId}")
    public TaskDetail3DTO updateTaskById(@RequestHeader("Authorization") String token, @PathVariable String boardId,
                                         @PathVariable Integer taskId,
                                         @RequestBody NewTask3DTO newTaskData) {
        authorizationService.validateToken(token);
        Tasks3 updateTask = tasks3Service.updateTask3(boardId, taskId, newTaskData);
        return modelMapper.map(updateTask, TaskDetail3DTO.class);
    }

    @DeleteMapping("/{boardId}/tasks/{taskId}")
    public Tasks3 deleteTaskById(@RequestHeader("Authorization") String token, @PathVariable String boardId, @PathVariable Integer taskId) {
        authorizationService.validateToken(token);
        return tasks3Service.removeTask3ById(boardId, taskId);
    }
}
