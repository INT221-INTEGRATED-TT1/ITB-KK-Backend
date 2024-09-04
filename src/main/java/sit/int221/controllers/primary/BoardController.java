package sit.int221.controllers.primary;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import sit.int221.dtos.request.NewBoardDTO;
import sit.int221.entities.primary.Board;
import sit.int221.services.BoardService;

import java.util.List;

@RestController
@RequestMapping("/v3/boards")
public class BoardController {
    @Autowired
    BoardService boardService;

    @GetMapping("")
    public List<Board> getAllBoards(){
        return boardService.getAllBoards();
    }

    @PostMapping("")
    @ResponseStatus(HttpStatus.CREATED)
    public Board createBoard(@RequestBody NewBoardDTO boardDTO){
        return boardService.insertBoard(boardDTO);
    }
}
