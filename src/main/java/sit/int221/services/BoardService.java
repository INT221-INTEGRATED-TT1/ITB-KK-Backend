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
import sit.int221.dtos.response.BoardAllDTORes;
import sit.int221.dtos.response.BoardResDTO;
import sit.int221.dtos.response.CollaboratorDTORes;
import sit.int221.dtos.response.OwnerBoard;
import sit.int221.entities.primary.Board;
import sit.int221.entities.primary.Collaborator;
import sit.int221.entities.primary.LocalUser;
import sit.int221.entities.secondary.User;
import sit.int221.exceptions.ItemNotFoundException;
import sit.int221.repositories.primary.BoardRepository;
import sit.int221.repositories.primary.CollaboratorRepository;
import sit.int221.repositories.primary.LocalUserRepository;
import sit.int221.repositories.secondary.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class BoardService {
    @Autowired
    private BoardRepository boardRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private LocalUserRepository localUserRepository;
    @Autowired
    private CollaboratorRepository collaboratorRepository;
    @Autowired
    AuthorizationService authorizationService;
    @Autowired
    Statuses3Service statuses3Service;
    @Autowired
    ModelMapper modelMapper;




    // fix response to DTO Update [GET]:/boards to return personal boards and collab boards!!!!!!!!!!  #Checked
    public BoardAllDTORes getAllBoards(Claims claims) {
        String oid = (String) claims.get("oid");
        User user = userRepository.findById(oid).orElseThrow(() -> new ItemNotFoundException("User id " + oid + " DOES NOT EXIST!!!"));

        //get personal boards
        List<Board> personalBoards = boardRepository.findAllByOwnerId(oid);

        // get collab boards
        List<Board> collaboratorBoards = boardRepository.findBoardsByUserOid(oid);

        // Map personal boards to BoardResDTO
        List<BoardResDTO> personalBoardDTOs = personalBoards.stream().map(board -> new BoardResDTO(board.getId(), board.getName(), board.getVisibility(), new OwnerBoard(user.getOid(), user.getName()))).toList();

        List<BoardResDTO> collaboratorBoardDTOs = collaboratorBoards.stream().map(board -> new BoardResDTO(board.getId(), board.getName(), board.getVisibility(), new OwnerBoard(userRepository.findById(board.getOwnerId()).orElseThrow(() -> new ItemNotFoundException("Owner Not Found")).getOid(), userRepository.findById(board.getOwnerId()).orElseThrow(() -> new ItemNotFoundException("Owner Not Found")).getName()))).toList();

        return new BoardAllDTORes(personalBoardDTOs, collaboratorBoardDTOs);
    }

    // need to fix allow access by board's collaborator as well #Checked
    public BoardResDTO getBoardById(Claims claims, String id) {
        String oid = (String) claims.get("oid");
        Board board = boardRepository.findById(id).orElseThrow(() -> new ItemNotFoundException("Board id " + id + " not found"));
        User user = userRepository.findById(oid).orElseThrow(() -> new ItemNotFoundException("User id " + oid + " DOES NOT EXIST!!!"));
        boolean isCollaborator = collaboratorRepository.findByBoardIdAndLocalUserOid(board.getId(), oid).isPresent();

        // check if is owner of the board or is Collaborator of the board
        if (oid.equals(board.getOwnerId()) || isCollaborator) {
            return getBoardResDTO(user, board);
        } else {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Cannot access board: board visibility is PRIVATE or You are not collaborator");
        }
    }

    public BoardResDTO getBoardById(String id) {
        Board board = boardRepository.findById(id).orElseThrow(() -> new ItemNotFoundException("Board id " + id + " not found"));
        User user = userRepository.findById(board.getOwnerId()).orElseThrow(() -> new ItemNotFoundException("User id " + board.getOwnerId() + " DOES NOT EXIST!!!"));
        return getBoardResDTO(user, board);
    }

    public BoardResDTO insertBoard(Claims claims, NewBoardDTO boardDTO) {
        String oid = (String) claims.get("oid");
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

    public Board updateVisibility(Claims claims, String boardId, EditVisibilityDTO newVisibility) {
        Board board = authorizationService.getBoardId(boardId);
        String oid = (String) claims.get("oid");
//        System.out.println(newVisibility.getVisibility());
        if (oid.equals(board.getOwnerId())) {
            authorizationService.checkIdThatBelongsToUser(claims, boardId);
            if (newVisibility.getVisibility() != null) {
                if (!newVisibility.getVisibility().equalsIgnoreCase("PUBLIC") && !newVisibility.getVisibility().equalsIgnoreCase("PRIVATE")) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Visibility must be either 'PUBLIC' or 'PRIVATE'");
                }
            } else {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Visibility cannot be null");
            }
            Board updateBoardVisibility = boardRepository.findById(boardId).orElseThrow(() -> new ItemNotFoundException("Board id " + boardId + " not found"));
            updateBoardVisibility.setVisibility(newVisibility.getVisibility().toUpperCase());
            return boardRepository.save(updateBoardVisibility);
        } else {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You are not allow to access this board");
        }
    }

    private BoardResDTO getBoardResDTO(User user, Board board) {
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

}
