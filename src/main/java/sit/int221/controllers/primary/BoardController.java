package sit.int221.controllers.primary;

import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sit.int221.components.JwtTokenUtil;
import sit.int221.dtos.request.NewBoardDTO;
import sit.int221.dtos.request.NewTask3DTO;
import sit.int221.dtos.response.BoardResDTO;
import sit.int221.entities.primary.Tasks3;
import sit.int221.services.BoardService;
import sit.int221.services.AuthorizationService;
import sit.int221.services.Tasks3Service;

@RestController
@RequestMapping("/v3/boards")
public class BoardController {
    @Autowired
    BoardService boardService;
    @Autowired
    JwtTokenUtil jwtTokenUtil;
    @Autowired
    AuthorizationService authorizationService;
    @Autowired
    Tasks3Service tasks3Service;

    @GetMapping("")
    public ResponseEntity<Object> getAllBoards(@RequestHeader("Authorization") String token) {
        Claims claims = authorizationService.validateToken(token);
        return ResponseEntity.ok(boardService.getAllBoards(claims));
    }

    @GetMapping("/{boardId}")
    public ResponseEntity<BoardResDTO> findBoardById(@RequestHeader("Authorization") String token,@PathVariable String boardId){

        // Call Method validateToken for check token from user

        Claims claims = authorizationService.validateToken(token);



        return  ResponseEntity.ok(boardService.getBoardById(claims,boardId));
    }

    @PostMapping("")
    public ResponseEntity<BoardResDTO> createBoard(@RequestHeader("Authorization") String token,
                                                   @RequestBody NewBoardDTO boardDTO) {
        Claims claims = authorizationService.validateToken(token);
//
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(boardService.insertBoard(claims, boardDTO));
    }


   @GetMapping("/{boardId}/tasks")
    public ResponseEntity<Object> getAllTaskByBoardId(@RequestHeader("Authorization") String token , @PathVariable String boardId){
        authorizationService.validateToken(token);
        return ResponseEntity.ok(tasks3Service.getAllTaskByBoardId(boardId));
   }

    @PostMapping("/{boardId}/tasks")
    public Tasks3 createTaskWithBoardId(@RequestHeader("Authorization") String token , @PathVariable String boardId, @RequestBody NewTask3DTO task3DTO){
        authorizationService.validateToken(token);
        return tasks3Service.createNewTaskByBoardId(boardId, task3DTO);
    }
}
