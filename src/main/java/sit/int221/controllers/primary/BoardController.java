package sit.int221.controllers.primary;

import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.web.csrf.InvalidCsrfTokenException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import sit.int221.components.JwtTokenUtil;
import sit.int221.dtos.request.EditVisibilityDTO;
import sit.int221.dtos.request.NewBoardDTO;
import sit.int221.dtos.response.BoardResDTO;
import sit.int221.entities.primary.Board;
import sit.int221.services.BoardService;
import sit.int221.services.AuthorizationService;

@CrossOrigin(origins = {"http://localhost:5173", "http://intproj23.sit.kmutt.ac.th", "http://localhost:80", "http://ip23tt1.sit.kmutt.ac.th", "http://ip23tt1.sit.kmutt.ac.th:1449", "http://intproj23.sit.kmutt.ac.th:8080"})
@RestController
@RequestMapping("/v3/boards")
@Slf4j
public class BoardController {
    @Autowired
    BoardService boardService;
    @Autowired
    AuthorizationService authorizationService;
    @Autowired
    JwtTokenUtil jwtTokenUtil;
    @Autowired
    ModelMapper modelMapper;


    @GetMapping("")
    public ResponseEntity<Object> getAllBoards(@RequestHeader(value = "Authorization" , required = false) String token) {
        try {
            Claims claims = authorizationService.validateToken(token);
            return ResponseEntity.ok(boardService.getAllBoards(claims));
        } catch (Exception ex) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/{boardId}")
    public ResponseEntity<BoardResDTO> findBoardById(@RequestHeader(value = "Authorization" , required = false) String token, @PathVariable String boardId) {
        Board board = authorizationService.getBoardId(boardId);

        // Call Method validateToken for check token from user
        if (board.getVisibility().equalsIgnoreCase("PUBLIC")) {
            return ResponseEntity.ok(boardService.getBoardById(boardId));
        }
        authorizationService.validateClaims(token);
        Claims claims = authorizationService.validateToken(token);
        return ResponseEntity.ok(boardService.getBoardById(claims, boardId));
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("")
    public ResponseEntity<BoardResDTO> createBoard(@RequestHeader("Authorization") String token,
                                                   @RequestBody NewBoardDTO boardDTO) {
        Claims claims = authorizationService.validateToken(token);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(boardService.insertBoard(claims, boardDTO));
    }

    @DeleteMapping("/{boardId}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<BoardResDTO> deleteBoardById(@RequestHeader("Authorization") String token, @PathVariable String boardId) {
        Claims claims = authorizationService.validateToken(token);
        return ResponseEntity.ok(boardService.removeBoardById(claims, boardId));
    }

    @PatchMapping("/{boardId}")
    public EditVisibilityDTO updateVisibility(@RequestHeader("Authorization") String token,
                                              @PathVariable String boardId,
                                              @RequestBody EditVisibilityDTO editVisibilityy) {
        Claims claims = authorizationService.validateToken(token);
        Board updatedVisibility = boardService.updateVisibility(claims, boardId, editVisibilityy);
        return modelMapper.map(updatedVisibility, EditVisibilityDTO.class);
    }

}
