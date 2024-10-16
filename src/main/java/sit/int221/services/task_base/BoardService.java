package sit.int221.services.task_base;

import com.aventrix.jnanoid.jnanoid.NanoIdUtils;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import sit.int221.dtos.request.EditVisibilityDTO;
import sit.int221.dtos.request.NewBoardDTO;
import sit.int221.dtos.response.*;
import sit.int221.entities.task_base.Board;
import sit.int221.entities.task_base.Collaborator;
import sit.int221.entities.itbkk_shared.User;
import sit.int221.exceptions.ItemNotFoundException;
import sit.int221.repositories.task_base.BoardRepository;
import sit.int221.repositories.task_base.CollaboratorRepository;
import sit.int221.repositories.itbkk_shared.UserRepository;
import sit.int221.services.itbkk_shared.AuthorizationService;

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

    public List<BoardAllDTORes> getAllBoards(Claims claims) {
        String oid = (String) claims.get("oid");
        User user = userRepository.findById(oid).orElseThrow(() -> new ItemNotFoundException("User id " + oid + " DOES NOT EXIST!!!"));

        System.out.println(oid);

        System.out.println(user);
        //get personal boards
        List<Board> personalBoards = boardRepository.findAllByOwnerId(oid);

        // get collab boards
        List<Board> collaboratorBoards = boardRepository.findBoardsByUserOid(oid);

        List<Collaborator> collaborators =  collaboratorRepository.findByLocalUserOid(oid);


//        System.out.println("collab" + collaborator);

        // Map personal boards to BoardResDTO
        List<PersonalBoardResDTO> personalBoardDTOs = personalBoards.stream()
                .map(board -> new PersonalBoardResDTO(board.getId(), board.getName(),
                        board.getVisibility(),
                        new OwnerBoardDTORes(user.getOid(), user.getName()))).toList();

        List<CollabsBoardResDTO> collaboratorBoardDTOs = collaboratorBoards.stream()
                .map(board -> new CollabsBoardResDTO(
                        board.getId(),
                        board.getName(),
                        new OwnerBoardCollabDTORes(userRepository.findById(board.getOwnerId()).orElseThrow(() ->
                                new ItemNotFoundException("Owner Not Found")).getName()),
                        collaborators.stream().filter(c -> c.getBoard().getId().equals(board.getId()))
                                .map(Collaborator::getAccessRight)
                                .collect(Collectors.toList()).toString()
                )).toList();
        System.out.println(personalBoardDTOs);

        System.out.println(collaboratorBoardDTOs);

        // Collect one BoardAllDTORes per user’s boards
        BoardAllDTORes boardAllDTORes = new BoardAllDTORes(personalBoardDTOs, collaboratorBoardDTOs);

        // Return list with BoardAllDTORes
        return List.of(boardAllDTORes);
    }

    public PersonalBoardResDTO getBoardById(Claims claims, String id) {
        String oid = (String) claims.get("oid");
        Board board = boardRepository.findById(id).orElseThrow(() -> new ItemNotFoundException("Board id " + id + " not found"));
        User user = userRepository.findById(oid).orElseThrow(() -> new ItemNotFoundException("User id " + oid + " DOES NOT EXIST!!!"));
        boolean isCollaborator = collaboratorRepository.findByBoardIdAndLocalUserOid(board.getId(), oid).isPresent();

        // check if is owner of the board or is Collaborator of the board can access board
        if (oid.equals(board.getOwnerId()) || isCollaborator) {
            return getBoardResDTO(user, board);
        } else {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Cannot access board: board visibility is PRIVATE or You are not collaborator");
        }
    }

    public PersonalBoardResDTO getBoardById(String id) {
        Board board = boardRepository.findById(id).orElseThrow(() -> new ItemNotFoundException("Board id " + id + " not found"));
        User user = userRepository.findById(board.getOwnerId()).orElseThrow(() -> new ItemNotFoundException("User id " + board.getOwnerId() + " DOES NOT EXIST!!!"));
        return getBoardResDTO(user, board);
    }

//    @Transactional(transactionManager = "itBkkTransactionManager")
    public PersonalBoardResDTO insertBoard(Claims claims, NewBoardDTO boardDTO) {
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

    public PersonalBoardResDTO removeBoardById(Claims claims, String boardId) {
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

    //    check board have in database yet ?
    public boolean boardExist(String boardId) {
        return boardRepository.existsById(boardId);
    }


    private PersonalBoardResDTO getBoardResDTO(User user, Board board) {
        OwnerBoardDTORes ownerBoardDTORes = new OwnerBoardDTORes();
        ownerBoardDTORes.setOid(user.getOid());
        ownerBoardDTORes.setName(user.getName());

        PersonalBoardResDTO personalBoardResDTO = new PersonalBoardResDTO();
        personalBoardResDTO.setId(board.getId());
        personalBoardResDTO.setName(board.getName());
        personalBoardResDTO.setVisibility(board.getVisibility());
        personalBoardResDTO.setOwner(ownerBoardDTORes);
        return personalBoardResDTO;
    }

}
