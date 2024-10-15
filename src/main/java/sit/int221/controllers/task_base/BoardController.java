package sit.int221.controllers.task_base;

import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import sit.int221.components.JwtTokenUtil;
import sit.int221.dtos.request.EditVisibilityDTO;
import sit.int221.dtos.request.NewBoardDTO;
import sit.int221.dtos.request.NewCollaboratorDTO;
import sit.int221.dtos.response.PersonalBoardResDTO;
import sit.int221.dtos.response.CollaboratorDTORes;
import sit.int221.dtos.response.EditAccessRightDTO;
import sit.int221.dtos.response.NewCollabDTORes;
import sit.int221.entities.task_base.Board;
import sit.int221.services.task_base.BoardService;
import sit.int221.services.itbkk_shared.AuthorizationService;
import sit.int221.services.task_base.CollaboratorService;

import java.util.List;

@CrossOrigin(origins = {"http://localhost:5173", "https://intproj23.sit.kmutt.ac.th", "http://localhost:80", "https://ip23tt1.sit.kmutt.ac.th", "https://ip23tt1.sit.kmutt.ac.th"})


@RestController
@RequestMapping("/v3/boards")
@Slf4j
public class BoardController {
    @Autowired
    BoardService boardService;
    @Autowired
    CollaboratorService collaboratorService;
    @Autowired
    AuthorizationService authorizationService;
    @Autowired
    JwtTokenUtil jwtTokenUtil;
    @Autowired
    ModelMapper modelMapper;


    @GetMapping("")
    public ResponseEntity<Object> getAllBoards(@RequestHeader(value = "Authorization", required = false) String token) {
        try {
            Claims claims = authorizationService.validateToken(token);
            return ResponseEntity.ok(boardService.getAllBoards(claims));
        } catch (Exception ex) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/{boardId}")
    public ResponseEntity<PersonalBoardResDTO> findBoardById(@RequestHeader(value = "Authorization", required = false) String token, @PathVariable String boardId) {
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
    public ResponseEntity<PersonalBoardResDTO> createBoard(@RequestHeader("Authorization") String token, @RequestBody NewBoardDTO boardDTO) {
        Claims claims = authorizationService.validateToken(token);
        return ResponseEntity.status(HttpStatus.CREATED).body(boardService.insertBoard(claims, boardDTO));
    }

    @DeleteMapping("/{boardId}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<PersonalBoardResDTO> deleteBoardById(@RequestHeader("Authorization") String token, @PathVariable String boardId) {
        Claims claims = authorizationService.validateToken(token);
        return ResponseEntity.ok(boardService.removeBoardById(claims, boardId));
    }

    @PatchMapping("/{boardId}")
    public EditVisibilityDTO updateVisibility(@RequestHeader("Authorization") String token, @PathVariable String boardId, @RequestBody EditVisibilityDTO editVisibility) {
        Claims claims = authorizationService.validateToken(token);
        Board updatedVisibility = boardService.updateVisibility(claims, boardId, editVisibility);
        return modelMapper.map(updatedVisibility, EditVisibilityDTO.class);
    }

    @GetMapping("/{boardId}/collabs")
    public List<CollaboratorDTORes> getCollaborators(@RequestHeader(value = "Authorization", required = false) String token, @PathVariable String boardId) {
        Board board = authorizationService.getBoardId(boardId);
        if (board.getVisibility().equalsIgnoreCase("PUBLIC")) {
            return collaboratorService.getAllCollaborators(boardId);
        }
        authorizationService.validateClaims(token);
        Claims claims = authorizationService.validateToken(token);
        return collaboratorService.getAllCollaborators(claims, boardId);
    }

    @GetMapping("/{boardId}/collabs/{collabId}")
    public ResponseEntity<CollaboratorDTORes> findCollabById(@RequestHeader(value = "Authorization", required = false) String token, @PathVariable String boardId, @PathVariable String collabId) {
        Board board = authorizationService.getBoardId(boardId);
        if (board.getVisibility().equalsIgnoreCase("PUBLIC")) {
            return ResponseEntity.ok(collaboratorService.getCollabById(boardId, collabId));
        }
        authorizationService.validateClaims(token);
        Claims claims = authorizationService.validateToken(token);
        return ResponseEntity.ok(collaboratorService.getCollabById(claims, boardId, collabId));
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/{boardId}/collabs")
    public ResponseEntity<NewCollabDTORes> createCollaborator(@RequestHeader("Authorization") String token, @PathVariable String boardId, @RequestBody NewCollaboratorDTO newCollab) {
        Claims claims = authorizationService.validateToken(token);
        return ResponseEntity.status(HttpStatus.CREATED).body(collaboratorService.createNewCollaborator(claims, boardId, newCollab));
    }

    @PatchMapping("/{boardId}/collabs/{oid}")
    public ResponseEntity<EditAccessRightDTO> updateAccessRight(@RequestHeader("Authorization") String token, @PathVariable String boardId, @PathVariable String oid, @RequestBody EditAccessRightDTO editAccessRight) {
        Claims claims = authorizationService.validateToken(token);
        return ResponseEntity.ok(collaboratorService.updateAccessRight(claims, boardId, oid, editAccessRight));
    }

    @DeleteMapping("/{boardId}/collabs/{oid}")
    public ResponseEntity<CollaboratorDTORes> removeCollaborator(@RequestHeader("Authorization") String token,
                                                           @PathVariable String boardId,
                                                           @PathVariable String oid) {
        Claims claims = authorizationService.validateToken(token);
        return ResponseEntity.ok(collaboratorService.removeCollaborator(claims, boardId, oid));
    }


}
