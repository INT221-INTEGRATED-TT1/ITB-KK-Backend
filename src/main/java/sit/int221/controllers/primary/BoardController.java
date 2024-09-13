package sit.int221.controllers.primary;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import sit.int221.components.JwtTokenUtil;
import sit.int221.dtos.request.NewBoardDTO;
import sit.int221.dtos.response.BoardResDTO;
import sit.int221.services.BoardService;
import sit.int221.utils.AuthorizationUtil;

import java.util.List;

@RestController
@RequestMapping("/v3/boards")
public class BoardController {
    @Autowired
    BoardService boardService;
    @Autowired
    JwtTokenUtil jwtTokenUtil;
    @Autowired
    AuthorizationUtil authorizationUtil;

    @GetMapping("")
    public ResponseEntity<Object> getAllBoards(@RequestHeader("Authorization") String token) {
        Claims claims = authorizationUtil.validateToken(token);
        return ResponseEntity.ok(boardService.getAllBoards(claims));
    }

    @GetMapping("/{boardId}")
    public ResponseEntity<BoardResDTO> findBoardById(@RequestHeader("Authorization") String token,@PathVariable String boardId){

        // Call Method validateToken for check token from user

        Claims claims = authorizationUtil.validateToken(token);



        return  ResponseEntity.ok(boardService.getBoardById(claims,boardId));
    }

    @PostMapping("")
    public ResponseEntity<BoardResDTO> createBoard(@RequestHeader("Authorization") String token,
                                                   @RequestBody NewBoardDTO boardDTO) {
        Claims claims = authorizationUtil.validateToken(token);
//
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(boardService.insertBoard(claims, boardDTO));
    }




}
