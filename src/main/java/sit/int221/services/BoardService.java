package sit.int221.services;

import com.aventrix.jnanoid.jnanoid.NanoIdUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import sit.int221.components.JwtTokenUtil;
import sit.int221.dtos.request.NewBoardDTO;
import sit.int221.entities.primary.Board;
import sit.int221.repositories.primary.BoardRepository;

import java.util.List;

@Service
public class BoardService {
    @Autowired
    private BoardRepository boardRepository;

    @Autowired
    private JwtUserDetailsService jwtUserDetailsService;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    public List<Board> getAllBoards() {
        return boardRepository.findAll();
    }

    public Board insertBoard(NewBoardDTO boardDTO) {
        Board newBoard = new Board();
        newBoard.setBoardName(boardDTO.getBoardName().trim());
        if (boardDTO.getBoardName() != null && !boardDTO.getBoardName().isBlank()) {
            newBoard.setBoardName(boardDTO.getBoardName().trim());
        } else {
            newBoard.setBoardName(null);
        }

        newBoard.setBoardID(NanoIdUtils.randomNanoId(
                NanoIdUtils.DEFAULT_NUMBER_GENERATOR,
                NanoIdUtils.DEFAULT_ALPHABET, 10));


        UserDetails userDetails = jwtUserDetailsService.loadUserByUsername();
        boardDTO.getOwner().setOid(jwtUserDetailsService);
        System.out.println(newBoard.getBoardID());
        return boardRepository.save(newBoard);

    }
}
