package sit.int221.services;

import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import sit.int221.dtos.request.NewCollaboratorDTO;
import sit.int221.entities.primary.Board;
import sit.int221.entities.primary.Collaborator;
import sit.int221.repositories.primary.BoardRepository;
import sit.int221.repositories.primary.CollaboratorRepository;
import sit.int221.repositories.secondary.UserRepository;

@Service
public class CollaboratorService {
    @Autowired
    private BoardRepository boardRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private CollaboratorRepository collaboratorRepository;
    @Autowired
    AuthorizationService authorizationService;

    public Collaborator createNewCollaborator(Claims claims, String boardId, NewCollaboratorDTO newCollab) {
        Board board = authorizationService.getBoardId(boardId);
        String oid = (String) claims.get("oid");
        // check if oid can access board
        if (oid.equals(board.getOwnerId())) {
            // check if email and accessRight is not null
            if (newCollab.getEmail() != null && newCollab.getAccessRight() != null) {
                // check if accessRight is not equal to 'READ', 'WRITE'
                // access_right ∉ {READ, WRITE}: #PASS
                if (!newCollab.getAccessRight().equalsIgnoreCase("READ") &&
                        !newCollab.getAccessRight().equalsIgnoreCase("WRITE")) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Access right must be either 'READ' or 'WRITE'");
                }
            } else {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email or Access right cannot be null");
            }


        }
// not finish

    }
}
