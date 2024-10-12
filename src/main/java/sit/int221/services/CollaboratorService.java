package sit.int221.services;

import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import sit.int221.dtos.request.NewCollaboratorDTO;
import sit.int221.dtos.response.CollaboratorDTORes;
import sit.int221.dtos.response.EditAccessRightDTO;
import sit.int221.dtos.response.NewCollabDTORes;
import sit.int221.entities.primary.Board;
import sit.int221.entities.primary.Collaborator;
import sit.int221.entities.primary.LocalUser;
import sit.int221.repositories.primary.BoardRepository;
import sit.int221.repositories.primary.CollaboratorRepository;
import sit.int221.repositories.primary.LocalUserRepository;
import sit.int221.repositories.secondary.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

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

    public List<CollaboratorDTORes> getAllCollaborators(Claims claims, String boardId) {
        Board board = authorizationService.getBoardId(boardId);
        validateAccess(claims, board);
        List<Collaborator> collaborators = collaboratorRepository.findAll();
        return collaborators.stream().map(collaborator -> getCollabResDTO(collaborator, collaborator.getLocalUser())).collect(Collectors.toList());
    }

    public List<CollaboratorDTORes> getAllCollaborators() {
        List<Collaborator> collaborators = collaboratorRepository.findAll();
        return collaborators.stream().map(collaborator -> getCollabResDTO(collaborator, collaborator.getLocalUser())).collect(Collectors.toList());
    }

    public CollaboratorDTORes getCollabById(Claims claims, String boardId, String oid) {
        Board board = authorizationService.getBoardId(boardId);
        validateAccess(claims, board);
        boolean isCollaborator = collaboratorRepository.findByBoardIdAndLocalUserOid(board.getId(), oid).isPresent();
        if (!isCollaborator) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Collaborator not found");
        }
        Collaborator collaborator = collaboratorRepository.findByLocalUserOid(oid);
        return getCollabResDTO(collaborator, collaborator.getLocalUser());
    }

    public CollaboratorDTORes getCollabById(String boardId, String oid) {
        Board board = authorizationService.getBoardId(boardId);
        Collaborator collaborator = collaboratorRepository.findByLocalUserOid(oid);
        boolean isCollaborator = collaboratorRepository.findByBoardIdAndLocalUserOid(board.getId(), oid).isPresent();
        if (!isCollaborator) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Collaborator not found");
        }
        return getCollabResDTO(collaborator, collaborator.getLocalUser());
    }

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

            // Set 'READ' as the default value for accessRight if it's empty
            if (newCollab.getAccessRight().isBlank()) {
                newCollab.setAccessRight("READ");
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

            // access_right not {READ, WRITE}:
            if (!newCollab.getAccessRight().equalsIgnoreCase("READ") && !newCollab.getAccessRight().equalsIgnoreCase("WRITE")) {
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

    public EditAccessRightDTO updateAccessRight(Claims claims, String boardId, String oid, EditAccessRightDTO editAccessRightDTO) {
        // token.is.valid AND board($id).NOT.exists
        Board board = authorizationService.getBoardId(boardId);
        String boardOid = (String) claims.get("oid");

        // owner can edit access right of collaborator
        if (boardOid.equals(board.getOwnerId())) {
            // Get collaborator or throw exception if not found
            Collaborator collaborator = collaboratorRepository.findByBoardIdAndLocalUserOidOrThrow(boardId, oid);
            if (collaborator != null) {
                // access_right not {READ, WRITE}:
                if (!editAccessRightDTO.getAccessRight().equalsIgnoreCase("READ") && !editAccessRightDTO.getAccessRight().equalsIgnoreCase("WRITE")) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Access right must be either 'READ' or 'WRITE'");
                }
                collaborator.setAccessRight(editAccessRightDTO.getAccessRight().toUpperCase());
                collaboratorRepository.save(collaborator);

                // response DTO
                EditAccessRightDTO editDtoResponse = new EditAccessRightDTO();
                editDtoResponse.setAccessRight(collaborator.getAccessRight());
                return editDtoResponse;
            } else {
                // Collaborator not found
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Collaborator not found on the board with ID: " + boardId);
            }
        } else {
            // token.is.valid AND board($id).exists AND token.oid.is.NOT.board.owner return 403
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Not Board Owner");
        }
    }

    public CollaboratorDTORes removeCollaborator(Claims claims, String boardId, String oid) {
//        token.is.valid AND board($id).NOT.exists
        Board board = authorizationService.getBoardId(boardId);
        String boardOid = (String) claims.get("oid");
        Collaborator collaborator = collaboratorRepository.findByBoardIdAndLocalUserOidOrThrow(boardId, oid);
        // owner and collaborator themselves can remove collaborator
        if (boardOid.equals(board.getOwnerId()) || boardOid.equals(oid)) {
            if (collaborator != null) {
                Collaborator collabToDelete = collaboratorRepository.findByLocalUserOid(oid);
                LocalUser localUser = collaborator.getLocalUser();
                collaboratorRepository.delete(collabToDelete);
                return getCollabResDTO(collaborator, localUser);
            } else {
                // Collaborator not found
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Collaborator not found on the board with ID: " + boardId);
            }
        } else {
            // token.is.valid AND board($id).exists AND
            // (token.oid.is.NOT.board.owner OR token.oid.is.NOT.board.collaborator
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Not Board Owner or Not Board Collaborator");
        }
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
