package sit.int221.services;

import com.aventrix.jnanoid.jnanoid.NanoIdUtils;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import sit.int221.dtos.request.EditVisibilityDTO;
import sit.int221.dtos.request.NewBoardDTO;
import sit.int221.dtos.response.BoardResDTO;
import sit.int221.dtos.response.OwnerBoard;
import sit.int221.entities.primary.Board;
import sit.int221.entities.secondary.User;
import sit.int221.exceptions.ItemNotFoundException;
import sit.int221.exceptions.TaskNotFoundException;
import sit.int221.repositories.primary.BoardRepository;
import sit.int221.repositories.secondary.UserRepository;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class BoardService {
    @Autowired
    private BoardRepository boardRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    AuthorizationService authorizationService;
    @Autowired
    Statuses3Service statuses3Service;
    @Autowired
    ModelMapper modelMapper;


    public List<Board> getAllBoards(Claims claims) {
        String oid = (String) claims.get("oid");
        return boardRepository.findAllByOwnerId(oid);
    }

    public BoardResDTO getBoardById(Claims claims, String id) {
        String oid = (String) claims.get("oid");
        Board board = boardRepository.findById(id).orElseThrow(() -> new ItemNotFoundException("Board id " + id + " not found"));
        User user = userRepository.findById(oid).orElseThrow(() -> new ItemNotFoundException("User id " + oid + " DOES NOT EXIST!!!"));
        if (oid.equals(board.getOwnerId())) {
            return getBoardResDTO(user, board);
        } else {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Cannot access board: board visibility is PRIVATE");
        }
    }

    public BoardResDTO getBoardById(String id) {
        Board board = boardRepository.findById(id).orElseThrow(() -> new ItemNotFoundException("Board id " + id + " not found"));
        User user = userRepository.findById(board.getOwnerId()).orElseThrow(() -> new ItemNotFoundException("User id " + board.getOwnerId() + " DOES NOT EXIST!!!"));
        return getBoardResDTO(user, board);
    }


    public BoardResDTO insertBoard(Claims claims, NewBoardDTO boardDTO) {
        String oid = (String) claims.get("oid");
//        System.out.println(oid);
        User user = userRepository.findById(oid).orElseThrow(() -> new ItemNotFoundException("User id " + oid + " DOES NOT EXIST!!!"));
        String nanoId = NanoIdUtils.randomNanoId(NanoIdUtils.DEFAULT_NUMBER_GENERATOR, NanoIdUtils.DEFAULT_ALPHABET, 10);
        while (boardRepository.findById(nanoId).isPresent()) {
            nanoId = NanoIdUtils.randomNanoId(NanoIdUtils.DEFAULT_NUMBER_GENERATOR, NanoIdUtils.DEFAULT_ALPHABET, 10);
        }
        Board newBoard = new Board();
        // set board ID
        if (newBoard.getId() == null || newBoard.getId().isEmpty()) {
            newBoard.setId(NanoIdUtils.randomNanoId(NanoIdUtils.DEFAULT_NUMBER_GENERATOR, NanoIdUtils.DEFAULT_ALPHABET, 10));
        }
        // set board visibility
        newBoard.setVisibility("PRIVATE");

        if (boardDTO.getName() == null || boardDTO.getName().isEmpty() || boardDTO.getName().length() > 120) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "you must need to insert board name or board name is more than 120 character");
        }
//        oid must use from localUsersDB
        newBoard.setOwnerId(oid);
        newBoard.setName(boardDTO.getName());
        Board createdBoard = boardRepository.saveAndFlush(newBoard);

        // create default statuses
        statuses3Service.insertDefaultStatus(createdBoard.getId());

        return getBoardResDTO(user, createdBoard);

    }

    public BoardResDTO removeBoardById(Claims claims, String boardId) {
        Board board = authorizationService.getBoardId(boardId);
        String oid = (String) claims.get("oid");
        if (oid.equals(board.getOwnerId())) {
            User user = userRepository.findById(oid).orElseThrow(() -> new ItemNotFoundException("User id " + oid + " DOES NOT EXIST!!!"));
            authorizationService.checkIdThatBelongsToUser(claims, boardId);
            boardRepository.deleteById(boardId);
            return getBoardResDTO(user, board);
        } else {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You are not allow to access this board");
        }
    }

    public BoardResDTO getBoardResDTO(User user, Board board) {
        OwnerBoard ownerBoard = new OwnerBoard();
        ownerBoard.setOid(user.getOid());
        ownerBoard.setName(user.getName());

        BoardResDTO boardResDTO = new BoardResDTO();
        boardResDTO.setId(board.getId());
        boardResDTO.setName(board.getName());
        boardResDTO.setVisibility(board.getVisibility());
        boardResDTO.setOwner(ownerBoard);
        return boardResDTO;
    }

    public Board updateVisibility(Claims claims, String boardId, EditVisibilityDTO newVisibility) {
        Board board = authorizationService.getBoardId(boardId);
        String oid = (String) claims.get("oid");
        if (oid.equals(board.getOwnerId())) {
            authorizationService.checkIdThatBelongsToUser(claims, boardId);
            if (!newVisibility.getVisibility().equalsIgnoreCase("PUBLIC") && !newVisibility.getVisibility().equalsIgnoreCase("PRIVATE")) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Visibility must be either 'PUBLIC' or 'PRIVATE'");
            }
            Board updateBoardVisibility = boardRepository.findById(boardId).orElseThrow(() -> new ItemNotFoundException("Board id " + boardId + " not found"));
            updateBoardVisibility.setVisibility(newVisibility.getVisibility().toUpperCase());
            return boardRepository.save(updateBoardVisibility);
        } else {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You are not allow to access this board");
        }
    }
}
