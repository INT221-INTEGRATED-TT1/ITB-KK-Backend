package sit.int221.services;

import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import sit.int221.dtos.request.NewCollaboratorDTO;
import sit.int221.dtos.response.CollaboratorDTORes;
import sit.int221.dtos.response.NewCollabDTORes;
import sit.int221.entities.primary.Board;
import sit.int221.entities.primary.Collaborator;
import sit.int221.entities.primary.LocalUser;
import sit.int221.entities.secondary.User;
import sit.int221.exceptions.ItemNotFoundException;
import sit.int221.repositories.primary.BoardRepository;
import sit.int221.repositories.primary.CollaboratorRepository;
import sit.int221.repositories.primary.LocalUserRepository;
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
    private LocalUserRepository localUserRepository;
    @Autowired
    AuthorizationService authorizationService;

    public NewCollabDTORes createNewCollaborator(Claims claims, String boardId, NewCollaboratorDTO newCollab) {
        String oid = (String) claims.get("oid");
        String email = (String) claims.get("email");
        // Retrieve the board by boardId, throws exception if board does not exist
        Board board = authorizationService.getBoardId(boardId);

        // check if email is in share_itbkk
        Boolean existsEmailShared = userRepository.existsByEmail(newCollab.getEmail());
        // check if oid is the owner of the board
        if (oid.equals(board.getOwnerId())) {
            // Validate email and accessRight fields
            if (newCollab.getEmail() == null || newCollab.getAccessRight() == null) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email or Access right cannot be null");
            }

            // e-mail.NOT.exists.in.itbkk_shared
            if (existsEmailShared == false) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Email does not exist in itbkk_shared");
            }

            // Check if the user is trying to add themselves as a collaborator
            if (email.equalsIgnoreCase(newCollab.getEmail())) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Cannot add yourself as a collaborator");
            }

            Boolean existsEmailCollab = collaboratorRepository.existsByBoardAndLocalUserEmail(board, newCollab.getEmail());

            // Check if the user is already a collaborator on the board
            if (existsEmailCollab) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "User is already a collaborator on this board");
            }

            // access_right not {READ, WRITE}: #Checked
            if (!newCollab.getAccessRight().equalsIgnoreCase("READ") &&
                    !newCollab.getAccessRight().equalsIgnoreCase("WRITE")) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Access right must be either 'READ' or 'WRITE'");
            }

            // Fetch the LocalUser by email
            LocalUser localUser = localUserRepository.findByEmail(newCollab.getEmail());

            Collaborator newCollaborator = new Collaborator();
            newCollaborator.setBoard(board);
            newCollaborator.setLocalUser(localUser);
            newCollaborator.setAccessRight(newCollab.getAccessRight().toUpperCase());
            collaboratorRepository.save(newCollaborator);

            // create responseDTO
            return collaboratorResponse(boardId, localUser, newCollaborator.getAccessRight());

        } else {
            // token.is.valid AND board($id).exists AND token.oid.is.NOT.board.owner return 403
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Not Board Owner");
        }
    }

    private NewCollabDTORes collaboratorResponse(String boardId, LocalUser localUser, String accessRight) {
        NewCollabDTORes dtoResponse = new NewCollabDTORes();
        dtoResponse.setBoardId(boardId);
        dtoResponse.setCollaboratorName(localUser.getName());
        dtoResponse.setCollaboratorEmail(localUser.getEmail());
        dtoResponse.setAccessRight(accessRight);
        return dtoResponse;
    }
}
