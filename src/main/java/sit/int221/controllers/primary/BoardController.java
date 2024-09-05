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
import sit.int221.entities.primary.Board;
import sit.int221.services.BoardService;

import java.util.List;

@RestController
@RequestMapping("/v3/boards")
public class BoardController {
    @Autowired
    BoardService boardService;
    @Autowired
    JwtTokenUtil jwtTokenUtil;

    @GetMapping("")
    public ResponseEntity<Object> getAllBoards() {
        return ResponseEntity.ok(boardService.getAllBoards());
    }

    @GetMapping("/{boardId}")
    public ResponseEntity<BoardResDTO> findBoardById(@RequestHeader("Authorization") String token,@PathVariable String boardId){
        Claims claims = null;
        String jwtToken = null;
        if (token != null && token.startsWith("Bearer ")) {
            jwtToken = token.substring(7);
            try {
                claims = jwtTokenUtil.getAllClaimsFromToken(jwtToken);
            } catch (IllegalArgumentException e) {
                System.out.println("Unable to get JWT Token");
            } catch (ExpiredJwtException e) {
                System.out.println("JWT Token has expired");
            }
        } else {

            throw new ResponseStatusException(HttpStatus.EXPECTATION_FAILED, "JWT Token does not begin with Bearer String");
        }
        return  ResponseEntity.ok(boardService.getBoardById(claims,boardId));
    }

    @PostMapping("")
    public ResponseEntity<BoardResDTO> createBoard(@RequestHeader("Authorization") String token,
                                                   @RequestBody NewBoardDTO boardDTO) {
        Claims claims = null;
        String jwtToken = null;
        if (token != null && token.startsWith("Bearer ")) {
            jwtToken = token.substring(7);
            try {
                claims = jwtTokenUtil.getAllClaimsFromToken(jwtToken);
            } catch (IllegalArgumentException e) {
                System.out.println("Unable to get JWT Token");
            } catch (ExpiredJwtException e) {
                System.out.println("JWT Token has expired");
            }
        } else {
            throw new ResponseStatusException(HttpStatus.EXPECTATION_FAILED, "JWT Token does not begin with Bearer String");
        }
//
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(boardService.insertBoard(claims, boardDTO));
    }




}
