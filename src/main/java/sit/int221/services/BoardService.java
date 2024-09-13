package sit.int221.services;

import com.aventrix.jnanoid.jnanoid.NanoIdUtils;
import io.jsonwebtoken.Claims;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.server.ResponseStatusException;
import sit.int221.components.JwtTokenUtil;
import sit.int221.dtos.request.NewBoardDTO;
import sit.int221.dtos.response.BoardResDTO;
import sit.int221.dtos.response.OwnerBoard;
import sit.int221.entities.primary.Board;
import sit.int221.entities.secondary.User;
import sit.int221.exceptions.ItemNotFoundException;
import sit.int221.repositories.primary.BoardRepository;
import sit.int221.repositories.secondary.UserRepository;

import java.util.List;

@Service
public class BoardService {
    @Autowired
    private BoardRepository boardRepository;
    @Autowired
    ModelMapper modelMapper;
    @Autowired
    private UserRepository userRepository;

    public List<Board> getAllBoards(Claims claims) {
        String oid = (String) claims.get("oid");

//        Board board = boardRepository.findById(id).orElseThrow(() -> new ItemNotFoundException("Board id " + id + "not found"));
        return boardRepository.findAllByOwnerID(oid);


    }

    public BoardResDTO insertBoard(Claims claims, NewBoardDTO boardDTO) {

        String oid = (String) claims.get("oid");
        System.out.println(oid);
        User user = userRepository.findById(oid).orElseThrow(() -> new ItemNotFoundException("User id " + oid + " DOES NOT EXIST!!!"));
        Board newBoard = new Board();
        if (newBoard.getBoardID() == null || newBoard.getBoardID().isEmpty()) {
            newBoard.setBoardID(NanoIdUtils.randomNanoId(
                    NanoIdUtils.DEFAULT_NUMBER_GENERATOR,
                    NanoIdUtils.DEFAULT_ALPHABET, 10));
        }
//        newBoard.setBoardName(boardDTO.getBoardName().trim());
//        if (boardDTO.getBoardName() != null && !boardDTO.getBoardName().isBlank()) {
//            newBoard.setBoardName(boardDTO.getBoardName().trim());
//        } else {
//            newBoard.setBoardName(null);
//        }

        newBoard.setOwnerID(oid);
        newBoard.setBoardName(boardDTO.getBoardName());
        Board createdBoard = boardRepository.saveAndFlush(newBoard);
        return getBoardResDTO(user, createdBoard);

    }

    public BoardResDTO getBoardResDTO(User user, Board board) {
        // Need Refactor
        OwnerBoard ownerBoard = new OwnerBoard();
        ownerBoard.setOid(user.getOid());
        ownerBoard.setName(user.getName());
//        System.out.println(ownerBoard.getName());

        BoardResDTO boardResDTO = new BoardResDTO();
        boardResDTO.setBoardID(board.getBoardID());
        boardResDTO.setBoardName(board.getBoardName());
        boardResDTO.setOwner(ownerBoard);
        return boardResDTO;
    }

    public BoardResDTO getBoardById(Claims claims,String id){
        String oid = (String) claims.get("oid");

        Board board = boardRepository.findById(id).orElseThrow(() -> new ItemNotFoundException("Board id " + id + "not found"));
        User user = userRepository.findById(oid).orElseThrow(() -> new ItemNotFoundException("User id " + oid + " DOES NOT EXIST!!!"));
        if(oid.equals(board.getOwnerID()) ){
            return getBoardResDTO(user, board) ;
        }
        else {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,"This user cannot access this board");
        }

    }
}
