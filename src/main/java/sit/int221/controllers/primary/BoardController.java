package sit.int221.controllers.primary;

import io.jsonwebtoken.Claims;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sit.int221.components.JwtTokenUtil;
import sit.int221.dtos.request.NewBoardDTO;
import sit.int221.dtos.response.BoardResDTO;
import sit.int221.services.BoardService;
import sit.int221.services.AuthorizationService;
@CrossOrigin(origins = {"http://localhost:5173", "http://intproj23.sit.kmutt.ac.th", "http://localhost:80", "http://ip23tt1.sit.kmutt.ac.th", "http://ip23tt1.sit.kmutt.ac.th:1449", "http://intproj23.sit.kmutt.ac.th:8080"})
@RestController
@RequestMapping("/v3/boards")
public class BoardController {
    @Autowired
    BoardService boardService;
    @Autowired
    AuthorizationService authorizationService;
    @Autowired
    JwtTokenUtil jwtTokenUtil;


    @GetMapping("")
    public ResponseEntity<Object> getAllBoards(@RequestHeader("Authorization") String token) {
        Claims claims = authorizationService.validateToken(token);
        return ResponseEntity.ok(boardService.getAllBoards(claims));
    }

    @GetMapping("/{boardId}")
    public ResponseEntity<BoardResDTO> findBoardById(@RequestHeader("Authorization") String token, @PathVariable String boardId) {
        // Call Method validateToken for check token from user
        Claims claims = authorizationService.validateToken(token);
        return ResponseEntity.ok(boardService.getBoardById(claims, boardId));
    }

    @PostMapping("")
    public ResponseEntity<BoardResDTO> createBoard(@RequestHeader("Authorization") String token,
                                                   @RequestBody NewBoardDTO boardDTO) {
        Claims claims = authorizationService.validateToken(token);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(boardService.insertBoard(claims, boardDTO));
    }

}
