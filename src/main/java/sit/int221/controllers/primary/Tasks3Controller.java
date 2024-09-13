package sit.int221.controllers.primary;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
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
    public ResponseEntity<Object> getAllTaskByBoardId(@RequestHeader("Authorization") String token, @PathVariable String boardId) {
        authorizationService.validateToken(token);
        return ResponseEntity.ok(tasks3Service.getAllTaskByBoardId(boardId));
    }

    @PostMapping("/{boardId}/tasks")
    public Tasks3 createTaskWithBoardId(@RequestHeader("Authorization") String token, @PathVariable String boardId, @RequestBody NewTask3DTO task3DTO) {
        authorizationService.validateToken(token);
        return tasks3Service.createNewTaskByBoardId(boardId, task3DTO);
    }

    @GetMapping("/{boardId}/tasks/{taskId}")
    public TaskDetail3DTO getTaskById(@RequestHeader("Authorization") String token, @PathVariable String boardId, @PathVariable Integer taskId) {
        authorizationService.validateToken(token);
        Tasks3 findTasks3 = tasks3Service.findTask3ById(boardId, taskId);
        return modelMapper.map(findTasks3, TaskDetail3DTO.class);
    }
//    @PutMapping("/{boardId}/tasks/{taskId}")
//    public ResponseEntity<Object> updateTaskById(@RequestHeader("Authorization") String token , @PathVariable String boardId, @PathVariable Integer taskId){
//        authorizationService.validateToken(token);
//        return ResponseEntity.ok(tasks3Service.getAllTaskByBoardId(boardId));
//    }
//    @DeleteMapping("/{boardId}/tasks/{taskId}")
//    public ResponseEntity<Object> deleteTaskById(@RequestHeader("Authorization") String token , @PathVariable String boardId, @PathVariable Integer taskId){
//        authorizationService.validateToken(token);
//        return ResponseEntity.ok(tasks3Service.getAllTaskByBoardId(boardId));
//    }
}
