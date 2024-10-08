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
import sit.int221.dtos.response.CollaboratorDTORes;
import sit.int221.dtos.response.OwnerBoard;
import sit.int221.entities.primary.Board;
import sit.int221.entities.primary.Collaborator;
import sit.int221.entities.primary.LocalUser;
import sit.int221.entities.secondary.User;
import sit.int221.exceptions.ItemNotFoundException;
import sit.int221.repositories.primary.BoardRepository;
import sit.int221.repositories.primary.CollaboratorRepository;
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
    private CollaboratorRepository collaboratorRepository;
    @Autowired
    AuthorizationService authorizationService;
    @Autowired
    Statuses3Service statuses3Service;
    @Autowired
    ModelMapper modelMapper;


    // code get all here
    public List<CollaboratorDTORes> getAllCollaborators(Claims claims, String boardId) {
        Board board = authorizationService.getBoardId(boardId);
        validateAccess(claims, board);
        List<Collaborator> collaborators = collaboratorRepository.findAll();
        return collaborators.stream()
                .map(collaborator -> getCollabResDTO(collaborator, collaborator.getLocalUser()))
                .collect(Collectors.toList());
    }

    public List<CollaboratorDTORes> getAllCollaborators(String boardId) {
        List<Collaborator> collaborators = collaboratorRepository.findAll();
        return collaborators.stream()
                .map(collaborator -> getCollabResDTO(collaborator, collaborator.getLocalUser()))
                .collect(Collectors.toList());
    }

    public CollaboratorDTORes getCollabById(Claims claims, String boardId, String oid) {
        Board board = authorizationService.getBoardId(boardId);
        validateAccess(claims, board);
        Collaborator collaborator = collaboratorRepository.findByLocalUserOid(oid);
        return getCollabResDTO(collaborator, collaborator.getLocalUser());
    }

    public CollaboratorDTORes getCollabById(String boardId, String oid) {
        Board board = authorizationService.getBoardId(boardId);
        Collaborator collaborator = collaboratorRepository.findByLocalUserOid(oid);
        return getCollabResDTO(collaborator, collaborator.getLocalUser());
    }

    private CollaboratorDTORes getCollabResDTO(Collaborator collaborator, LocalUser localUser) {
        CollaboratorDTORes collabDTO = new CollaboratorDTORes();
        // set fields from localUser
        collabDTO.setOid(localUser.getOid());
        collabDTO.setName(localUser.getName());
        collabDTO.setEmail(localUser.getEmail());
        // set fields from collaborator
        collabDTO.setAccessRight(collaborator.getAccessRight());
        collabDTO.setAddedOn(collaborator.getAddedOn());
        return collabDTO;
    }

    private void validateAccess(Claims claims, Board board) {
        String oid = (String) claims.get("oid");
        boolean isOwner = oid.equals(board.getOwnerId());
        // try to understand this condition
        boolean isCollaborator = collaboratorRepository.findByBoardIdAndLocalUserOid(board.getId(), oid).isPresent();
        System.out.println("dfs" + isCollaborator);
        if (!isOwner && !isCollaborator) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Cannot access board: board visibility is PRIVATE");
        }
        if (!isCollaborator) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Collaborator not found");
        }
    }

    // fix response to DTO
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
}
